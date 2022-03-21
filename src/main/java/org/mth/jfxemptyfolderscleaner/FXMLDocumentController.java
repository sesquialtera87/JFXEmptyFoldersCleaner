package org.mth.jfxemptyfolderscleaner;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;

/**
 * @author Mattia Marelli
 */
public class FXMLDocumentController implements Initializable {

    private static final Logger log = Logger.getLogger(FXMLDocumentController.class.getName());

    @FXML
    private TextField folderField;
    @FXML
    private TextArea console;
    @FXML
    private ProgressIndicator busyIndicator;
    @FXML
    private Button cancelButton;
    @FXML
    private Button scanButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button browseButton;
    @FXML
    private StackPane stackPane;

    private Task<Void> currentTask;
    private final Queue<String> q = new ConcurrentLinkedQueue<>();
    private final BlockingQueue<String> messages = new ArrayBlockingQueue<>(1);
    private final LongProperty lastUpdate = new SimpleLongProperty();
    private final long minUpdateInterval = 0;
    private AnimationTimer timer;
    private boolean waitingForMessageQueue = false;
    /**
     * Indica se è attiva qualche task, come ad esempio la scansione di cartelle
     * o l'eliminazione
     */
    private final BooleanProperty taskRunning = new SimpleBooleanProperty(false);
    /**
     * Finestra di dialogo per la scelta di una cartella
     */
    private final DirectoryChooser dirChooser = new DirectoryChooser();

    /**
     * Show the dialog for the choice of the root folder, from which we start the scan
     */
    public void browseAction() {
        dirChooser.setInitialDirectory(new File(folderField.getText()));
        File file = dirChooser.showDialog(EmptyFoldersCleanerFX.mainStage);

        if (file != null)
            folderField.setText(file.getPath());
    }

    public final void cancelTaskAction() {
        log.info("Cancelling running task...");
        currentTask.cancel();
    }

    public void deleteEmptyFoldersAction() {
        int choice = DialogController.showConfirmDialog();

        if (choice == DialogController.CANCEL_OPTION) {
            return;
        }

        console.clear();

        DeletionTask task = new DeletionTask(folderField.getText());
        task.setMessageQueue(q);
        task.setOnSucceeded((WorkerStateEvent event) -> {
            taskRunning.set(false);
        });
        task.setOnCancelled((WorkerStateEvent event) -> {
            taskRunning.set(false);
        });
        task.setOnFailed((WorkerStateEvent event) -> {
            taskRunning.setValue(false);
        });

        currentTask = task;

        taskRunning.setValue(Boolean.TRUE);

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    public void scanAction() {
        console.clear();

//        timer.start();
        ScanEmptyFoldersTask task = new ScanEmptyFoldersTask(folderField.getText());
        task.setOnSucceeded(evt -> {
            if (q.isEmpty()) {
                taskRunning.set(false);
            } else {
                waitingForMessageQueue = true;
            }
        });
        task.setOnCancelled(evt -> {
            if (q.isEmpty()) {
                taskRunning.set(false);
            } else {
                waitingForMessageQueue = true;
            }
        });
        task.setOnFailed(evt -> {
            if (q.isEmpty()) {
                taskRunning.set(false);
            } else {
                waitingForMessageQueue = true;
            }
        });

        currentTask = task;

        taskRunning.set(true);

        Thread t = new Thread(task);
        t.setDaemon(false);
        t.start();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        TextLogger.setConsole(console);

        folderField.setText(System.getProperty("user.home"));
        folderField.editableProperty().bind(taskRunning.not());

        dirChooser.setInitialDirectory(new File(folderField.getText()));

        cancelButton.disableProperty().bind(taskRunning.not());

        scanButton.disableProperty().bind(taskRunning);

        deleteButton.disableProperty().bind(taskRunning);

        browseButton.disableProperty().bind(taskRunning);

        busyIndicator.visibleProperty().bind(taskRunning);

        console.setEditable(false);
        console.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            console.setScrollTop(Double.MAX_VALUE);
            console.setScrollLeft(Double.MIN_VALUE);
        });

        timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                if (now - lastUpdate.get() > minUpdateInterval) {
                    try {
                        //                    String message = q.poll();
                        String message = TextLogger.getMessageQueue().take();

                        console.appendText(message + "\n");

                        if (waitingForMessageQueue && q.isEmpty()) {
                            taskRunning.set(false);
                        }

                        lastUpdate.set(now);
                    } catch (InterruptedException ex) {
                        log.log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
    }

}

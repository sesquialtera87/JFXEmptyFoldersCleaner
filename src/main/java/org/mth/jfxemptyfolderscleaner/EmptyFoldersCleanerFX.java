package org.mth.jfxemptyfolderscleaner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * @author Mattia Marelli
 * @since 4-ott-2014
 */
public class EmptyFoldersCleanerFX extends Application {

    /**
     * Usato come finestra root per la visualizzazione di finestre di dialogo
     */
    public static Stage mainStage;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(EmptyFoldersCleanerFX.class.getResource("FXMLDocument.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("JFXEmptyFoldersCleaner");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("folder.png")));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("folder48.png")));
        stage.show();

        mainStage = stage;
    }

}

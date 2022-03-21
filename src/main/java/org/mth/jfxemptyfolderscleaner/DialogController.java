package org.mth.jfxemptyfolderscleaner;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Mattia Marelli
 * @since 7-ott-2014
 */
public class DialogController implements Initializable {

    public static final byte OK_OPTION = 0;
    public static final byte CANCEL_OPTION = 1;

    private final IntegerProperty userChoiche = new SimpleIntegerProperty(-10);

    public static int showConfirmDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(DialogController.class.getResource("Dialog.fxml"));
            Parent root = loader.load();
            DialogController controller = loader.getController();

            Scene dialog = new Scene(root);

            Stage s = new Stage(StageStyle.UTILITY);
            s.initModality(Modality.WINDOW_MODAL);
            s.initOwner(EmptyFoldersCleanerFX.mainStage);
            s.setAlwaysOnTop(true);
            s.setResizable(false);
            s.setScene(dialog);
            s.setTitle(ResourceBundle.getBundle("efc/resources/Bundle").getString("Application.title"));
            s.getIcons().add(new Image(DialogController.class.getResourceAsStream("/efc/resources/folder.png")));

            controller.userChoiche.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                s.getOwner().getScene().getRoot().setEffect(null);
                s.close();
            });
            s.getOwner().getScene().getRoot().setEffect(new BoxBlur());
            s.showAndWait();

            return controller.userChoiche.get();
        } catch (IOException ex) {
            Logger.getLogger(DialogController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return CANCEL_OPTION;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void OkAction() {
        userChoiche.setValue(OK_OPTION);
    }

    public void CancelAction() {
        userChoiche.setValue(CANCEL_OPTION);
    }
}

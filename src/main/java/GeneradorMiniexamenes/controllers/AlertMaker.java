package GeneradorMiniexamenes.controllers;

import javafx.scene.control.Alert;

/**
 * Created by Irvel on 11/13/16.
 */
public class AlertMaker {
    /**
     * displayError
     *
     * Inform the user of an error
     *
     * @param title The title of the error
     * @param content Information on what made the error occurr and what will be done
     */
    public static void displayError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * displayInfo
     *
     * Inform the user of something
     *
     * @param content Information on what happened
     */
    public static void displayInfo(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

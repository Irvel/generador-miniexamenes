package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.Main;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Imports the subjects from either a .json file or from the .txt legacy
 * format to the model
 *
 * The .json file is imported each time the application is opened
 * The .txt is opened only when the user selects to import from the legacy
 * format from the menu
 */
public class Import {

    /**
     * onClick
     *
     * method that opens a file to import data
     *
     * @param ae actionEvent to get the Window
     */
    public void onClick(ActionEvent ae){
        Node source = (Node) ae.getSource();
        Stage theStage = (Stage) source.getScene().getWindow();

        // Opens file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona tu archivo (txt o json)");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT", "*.txt"),
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        File file = fileChooser.showOpenDialog(theStage);
        
    }

}

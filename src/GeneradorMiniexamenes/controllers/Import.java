package GeneradorMiniexamenes.controllers;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

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

        // Checks the extension of the file selected (txt or json)
        String extension = "";
        String name = "";
        int i = file.getName().lastIndexOf('.');
        if(i > 0)
            extension = file.getName().substring(i + 1);
        name = file.getName().substring(0, i);

        if(extension.equals("txt"))
            importFromText(file, name);
        else
            importFromJson(file, name);
    }

    /**
     * importFromText
     *
     * method that lets us import miniExam from a txt file
     *
     * @param file that contains miniexam
     */
    private void importFromText(File file, String fileName){
        // Tries to open the file
        System.out.println(file.getPath());
        try {
            Scanner in = new Scanner(new FileReader(file.getPath()));
            String line = "";
            while(in.hasNextLine()){
                line = in.nextLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("El archivo no pudo ser abierto");
        }
    }

    private void importFromJson(File file, String fileName){

    }

}

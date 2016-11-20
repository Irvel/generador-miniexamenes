package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.Subject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * Saves a given Subject to a .json file
 */
public class Export {

    public void saveExternalJson(Subject subject, Stage currentStage) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // Indent output JSON
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        FileChooser fileChooser = new FileChooser();
        //Set extension filter
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
        fileChooser.setInitialFileName(subject.getSubjectName() + ".json");

        //Show save file dialog
        File file = fileChooser.showSaveDialog(currentStage);

        if(file != null){
            objectMapper.writeValue(file, subject);
        }
    }

    public void onClick(ActionEvent actionEvent, Subject subject) {
        Node source = (Node) actionEvent.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        try {
            saveExternalJson(subject, currentStage);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

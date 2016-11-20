package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.QuestionBank;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * Saves the current model to a .json file
 * TODO(Irvel): Clarify if each exported .json file should contain a single or multiple subjects
 */
public class Export {

    public void saveExternalJson(QuestionBank questionBank, Stage currentStage) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // Indent output JSON
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        FileChooser fileChooser = new FileChooser();
        //Set extension filter
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
        fileChooser.setInitialFileName("Banco de preguntas.json");

        //Show save file dialog
        File file = fileChooser.showSaveDialog(currentStage);

        if(file != null){
            objectMapper.writeValue(file, questionBank);
        }
    }

    public void onClick(ActionEvent actionEvent, QuestionBank questionBank) {
        Node source = (Node) actionEvent.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        try {
            saveExternalJson(questionBank, currentStage);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
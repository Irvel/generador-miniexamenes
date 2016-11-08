package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.QuestionBank;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.event.ActionEvent;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Saves the current model to a .json file
 */
public class Export {
    public void saveModelAsJson(QuestionBank questionBank) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // Indent output JSON
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        // Save object as JSON string
        FileOutputStream outFile = new FileOutputStream("data/questionBank.json");
        objectMapper.writeValue(outFile, questionBank);
    }

    public void onClick(ActionEvent actionEvent, QuestionBank questionBank) {
        try {
            saveModelAsJson(questionBank);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

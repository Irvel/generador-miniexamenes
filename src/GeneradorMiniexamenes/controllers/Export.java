package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.QuestionBank;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Saves the current model to a .json file
 */
public class Export {
    public void saveModelAsJson(QuestionBank questionBank){
        ObjectMapper objectMapper = new ObjectMapper();

        //convert Object to json string
        QuestionBank bank = questionBank;
    }
}

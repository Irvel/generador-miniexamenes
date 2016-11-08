package GeneradorMiniexamenes.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Saves the current model to a .json file
 */
public class Export {
    public void saveModelAsJson(){
        ObjectMapper objectMapper = new ObjectMapper();

        //convert Object to json string
        //QuestionBank bank = Main.getQuestionBank();
    }
}

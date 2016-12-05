package GeneradorMiniexamenes.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Block {
    private ArrayList<Question> mQuestions;
    public Block(@JsonProperty("questions") ArrayList<Question> questions) {
        this.mQuestions = questions;
    }

    public ArrayList<Question> getQuestions() {
        return mQuestions;
    }

}

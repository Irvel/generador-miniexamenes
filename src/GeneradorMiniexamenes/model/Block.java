package GeneradorMiniexamenes.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Block {
    private ArrayList<Question> mQuestions;
    private int sequenceNumber;

    public Block(@JsonProperty("questions") ArrayList<Question> questions,
                 @JsonProperty("sequenceNumber") int sequenceNumber) {
        this.mQuestions = questions;
        this.sequenceNumber = sequenceNumber;
    }

    public ArrayList<Question> getQuestions() {
        return mQuestions;
    }

    public void setQuestions(ArrayList<Question> mQuestions) {
        this.mQuestions = mQuestions;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}

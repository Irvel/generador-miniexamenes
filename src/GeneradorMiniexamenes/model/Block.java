package GeneradorMiniexamenes.model;

import java.util.ArrayList;

public class Block {
    private ArrayList<Question> mQuestions;
    private int sequenceNumber;

    public Block(ArrayList<Question> mQuestions, int sequenceNumber) {
        this.mQuestions = mQuestions;
        this.sequenceNumber = sequenceNumber;
    }

    public ArrayList<Question> getmQuestions() {
        return mQuestions;
    }

    public void setmQuestions(ArrayList<Question> mQuestions) {
        this.mQuestions = mQuestions;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}

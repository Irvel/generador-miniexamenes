package GeneradorMiniexamenes.model;

import java.util.ArrayList;

public class Question {
    private String mQuestion;
    private ArrayList<Answer> mAnswers;

    public Question(ArrayList<Answer> mAnswers, String mQuestion) {
        this.mAnswers = mAnswers;
        this.mQuestion = mQuestion;
    }

    public void setQuestion(String mQuestion) {
        this.mQuestion = mQuestion;
    }

    public void setAnswers(ArrayList<Answer> mAnswers) {
        this.mAnswers = mAnswers;
    }

    public String getQuestion() {

        return mQuestion;
    }

    public ArrayList<Answer> getAnswers() {
        return mAnswers;
    }
}

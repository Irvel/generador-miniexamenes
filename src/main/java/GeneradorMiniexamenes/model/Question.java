package GeneradorMiniexamenes.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Question {
    private String mQuestion;
    private ArrayList<Answer> mAnswers;

    public Question(@JsonProperty("answers") ArrayList<Answer> answers,
                    @JsonProperty("question") String question) {
        this.mAnswers = answers;
        this.mQuestion = question;
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

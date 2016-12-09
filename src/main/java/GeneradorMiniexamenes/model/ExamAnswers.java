package GeneradorMiniexamenes.model;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Irvel on 12/8/16.
 */
public class ExamAnswers {
    private SimpleStringProperty mAnswerLetter;
    private SimpleStringProperty mAnswerWeight;

    public ExamAnswers(SimpleStringProperty answerLetter, SimpleStringProperty answerWeight) {
        mAnswerLetter = answerLetter;
        mAnswerWeight = answerWeight;
    }

    public ExamAnswers(String answerLetter, String answerWeight) {
        mAnswerLetter = new SimpleStringProperty(answerLetter);
        mAnswerWeight = new SimpleStringProperty(answerWeight);
    }


    public String getAnswerLetter() {
        return mAnswerLetter.get();
    }

    public SimpleStringProperty answerLetterProperty() {
        return mAnswerLetter;
    }

    public void setAnswerLetter(String answerLetter) {
        this.mAnswerLetter.set(answerLetter);
    }

    public String getAnswerWeight() {
        return mAnswerWeight.get();
    }

    public SimpleStringProperty answerWeightProperty() {
        return mAnswerWeight;
    }

    public void setAnswerWeight(String answerWeight) {
        this.mAnswerWeight.set(answerWeight);
    }
}

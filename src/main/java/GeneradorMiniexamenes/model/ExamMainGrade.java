package GeneradorMiniexamenes.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Irvel on 12/8/16.
 * Model class for the table view used in grading exams
 */
public class ExamMainGrade {
    private IntegerProperty mQuestionNumber;
    private StringProperty mAnswerLetter;
    private SimpleStringProperty mAnswerWeight;
    private IntegerProperty mQuestionValue;

    public ExamMainGrade(int questionNumber, String answerLetter,
                         int answerWeight, int questionValue) {
        mQuestionNumber = new SimpleIntegerProperty(questionNumber);
        mAnswerLetter = new SimpleStringProperty(answerLetter);
        mAnswerWeight = new SimpleStringProperty("");
        mQuestionValue = new SimpleIntegerProperty(questionValue);
    }

    public int getQuestionNumber() {
        return mQuestionNumber.get();
    }

    public IntegerProperty questionNumberProperty() {
        return mQuestionNumber;
    }

    public String getAnswerLetter() {
        return mAnswerLetter.get();
    }

    public void setAnswerLetter(String answerLetter) {
        this.mAnswerLetter.set(answerLetter);
    }

    public StringProperty answerLetterProperty() {
        return mAnswerLetter;
    }

    public String getAnswerWeight() {
        return mAnswerWeight.get();
    }

    public SimpleStringProperty answerWeightProperty() {
        return mAnswerWeight;
    }

    public int getQuestionValue() {
        return mQuestionValue.get();
    }

    public IntegerProperty questionValueProperty() {
        return mQuestionValue;
    }
}

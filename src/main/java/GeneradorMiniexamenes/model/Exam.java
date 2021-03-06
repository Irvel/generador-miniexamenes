package GeneradorMiniexamenes.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by Irvel on 11/12/16.
 */
public class Exam {
    private String mSubject;
    private String mGroup;
    private ArrayList<Question> mQuestions;
    private int mExamNumber;

    public Exam(@JsonProperty("subjectName") String subject,
                @JsonProperty("group") String group,
                @JsonProperty("questions") ArrayList<Question> questions,
                @JsonProperty("examNumber") int examNumber) {
        mSubject = subject;
        mGroup = group;
        mQuestions = questions;
        mExamNumber = examNumber;
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String subject) {
        mSubject = subject;
    }

    public String getGroup() {
        return mGroup;
    }

    public ArrayList<Question> getQuestions() {
        return mQuestions;
    }

    public int getExamNumber() {
        return mExamNumber;
    }

    @Override
    public String toString(){
        return "Examen #" + getExamNumber() + " - Grupo: " + getGroup();
    }
}

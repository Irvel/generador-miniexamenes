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

    public Exam(@JsonProperty("subjectName") String mSubject,
                @JsonProperty("group") String mGroup,
                @JsonProperty("questions") ArrayList<Question> mQuestions) {
        this.mSubject = mSubject;
        this.mGroup = mGroup;
        this.mQuestions = mQuestions;
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

    public void setGroup(String group) {
        mGroup = group;
    }

    public ArrayList<Question> getQuestions() {
        return mQuestions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        mQuestions = questions;
    }
}

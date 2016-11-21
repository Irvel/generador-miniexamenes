package GeneradorMiniexamenes.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by Irvel on 11/20/16.
 */
public class Group {
    private String mGroupName;
    private ArrayList<Exam> mExams;
    private int mHighestExamNumber;

    // This is for Jackson to know how to serialize this object. Use the constructor below instead
    public Group(@JsonProperty("groupName") String groupName,
                 @JsonProperty("exams") ArrayList<Exam> exams,
                 @JsonProperty("highestExamNumber") int highestExamNumber) {
        mGroupName = groupName;
        mExams = exams;
        mHighestExamNumber = highestExamNumber;
    }

    public Group(String groupName, ArrayList<Exam> exams) {
        mGroupName = groupName;
        mExams = exams;
        setHighestExamNumber();
    }

    public String getGroupName() {
        return mGroupName;
    }

    public void setGroupName(String groupName) {
        mGroupName = groupName;
    }

    public ArrayList<Exam> getExams() {
        return mExams;
    }

    public void setExams(ArrayList<Exam> exams) {
        mExams = exams;
        setHighestExamNumber();
    }

    public int getHighestExamNumber() {
        return mHighestExamNumber;
    }

    /**
     * setHighestExamNumber
     *
     * Find the exam in this group with the highest exam number and set it to mHighestExamNumber.
     * This is done to help generate exams with an ever increasing an exam number to ensure they
     * are distinguishable.
     *
     */
    private void setHighestExamNumber() {
        // All exam numbers should start with at least 1
        int highestNumber = 1;
        for (Exam exam : mExams) {
            if (exam.getExamNumber() > highestNumber) {
                mHighestExamNumber = exam.getExamNumber();
            }
        }
        if (highestNumber > mHighestExamNumber) {
            mHighestExamNumber = highestNumber;
        }
    }

    public void addExams(ArrayList<Exam> exams) {
        mExams.addAll(exams);
        setHighestExamNumber();
    }
}

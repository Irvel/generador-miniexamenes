package GeneradorMiniexamenes.model;

import GeneradorMiniexamenes.controllers.AppState;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Irvel on 11/12/16.
 */
public class ExamBank {
    // Each subject matched by a list of generated exams
    private HashMap<String, ArrayList<Exam>> mExams;

    public ExamBank(@JsonProperty("exams") HashMap<String, ArrayList<Exam>> exams) {
        mExams = exams;
    }

    public ExamBank() {
        mExams = new HashMap<>();
    }

    public HashMap<String, ArrayList<Exam>> getExams() {
        return mExams;
    }

    public void setExams(HashMap<String, ArrayList<Exam>> mExams) {
        this.mExams = mExams;
    }


    public void appendExamBank(ExamBank examBank) {
        this.mExams.putAll(examBank.getExams());
        // Save the modified ExamBank to storage
        AppState.saveExamBank(this);
    }

    public ArrayList<Exam> getExams(String subject) {
        if (mExams.containsKey(subject)) {
            return mExams.get(subject);
        }
        return null;
    }

    public void addExam(String subject, Exam exam) {
        if (mExams.containsKey(subject)) {
            mExams.get(subject).add(exam);
        }
        else {
            mExams.put(subject, new ArrayList<>());
            mExams.get(subject).add(exam);
        }
    }

    public void clearSubject(String subject) {
        if (mExams.containsKey(subject)) {
            mExams.remove(subject);
        }
    }

    public Set<String> getSubjects() {
        return mExams.keySet();
    }
}

package GeneradorMiniexamenes.model;

import java.util.ArrayList;

/**
 * The set of subjects and their respective questions stored in an object in
 * memory.
 */
public class QuestionBank {
    ArrayList<Subject> mSubjects;

    public QuestionBank() {
        this.mSubjects = new ArrayList<>();
    }

    public QuestionBank(ArrayList<Subject> subjects) {
        this.mSubjects = subjects;
    }

    public ArrayList<Subject> getSubjects() {
        return mSubjects;
    }

    public void addSubject(Subject mSubject){
        if (mSubjects == null) {
            mSubjects = new ArrayList<>();
        }
        mSubjects.add(mSubject);
    }
}

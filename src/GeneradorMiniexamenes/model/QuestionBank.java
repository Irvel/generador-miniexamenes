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

    /**
     * getSubjectByName
     *
     * Given the name of a Subject, returns the object instance that matches it.
     * In case the name wasn't found, the method returns null.
     *
     * @param name The name of the which instance will be returned if found
     * @return subject The subject instance with a matching name
     */
    public Subject getSubjectByName(String name) {
        for (Subject subject : mSubjects) {
            if (name.equals(subject.getSubjectName())) {
                return subject;
            }
        }
        return null;
    }
}

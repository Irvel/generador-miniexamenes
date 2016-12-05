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

    public ArrayList<Subject> getSubjects() {
        return mSubjects;
    }

    /**
     * addSubject
     *
     * Adds a new subject to the QuestionBank. If the subject exists, replace it.
     * @param subject The subject to add
     *
     */
    public void addSubject(Subject subject) {
        if (mSubjects == null) {
            mSubjects = new ArrayList<>();
        }
        else {
            // Remove a previous version of that subject
            for (int i = 0; i < mSubjects.size(); i++) {
                if (mSubjects.get(i)
                             .getSubjectName()
                             .equalsIgnoreCase(subject.getSubjectName())) {
                    mSubjects.remove(i);
                    break;
                }
            }
        }
        mSubjects.add(subject);
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

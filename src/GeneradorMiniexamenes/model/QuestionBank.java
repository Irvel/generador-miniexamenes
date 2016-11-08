package GeneradorMiniexamenes.model;

import java.util.ArrayList;

/**
 * The set of subjects and their respective questions stored in an object in
 * memory.
 */
public class QuestionBank {
    ArrayList<Subject> mSubjects;

    public int getSubjectQuantity(){ return mSubjects.size(); }

    public void addSubject(Subject mSubject){ this.mSubjects.add(mSubject); }
}

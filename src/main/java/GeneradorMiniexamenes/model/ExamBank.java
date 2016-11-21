package GeneradorMiniexamenes.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;

import static GeneradorMiniexamenes.controllers.AppState.saveExamBank;

/**
 * Created by Irvel on 11/12/16.
 */
public class ExamBank {
    // Each subject matched by a list of Groups with the generated exams
    private HashMap<String, ArrayList<Group>> mGroups;

    public ExamBank(@JsonProperty("groups") HashMap<String, ArrayList<Group>> groups) {
        mGroups = groups;
    }

    public ExamBank() {
        mGroups = new HashMap<>();
    }

    public HashMap<String, ArrayList<Group>> getGroups() {
        return mGroups;
    }

    public void setGroups(HashMap<String, ArrayList<Group>> mExams) {
        this.mGroups = mExams;
    }

    public ArrayList<Group> getGroups(String subject) {
        if (mGroups.containsKey(subject)) {
            return mGroups.get(subject);
        }
        return null;
    }

    public ArrayList<Exam> getExams(String subject, String groupName) {
        if (mGroups.containsKey(subject)) {
            for (Group group : mGroups.get(subject)) {
                if (group.getGroupName().equalsIgnoreCase(groupName)) {
                    return group.getExams();
                }
            }
        }
        return null;
    }

    public Exam getExam(String subject, String groupName, int examNumber) {
        if (mGroups.containsKey(subject)) {
            for (Group group : mGroups.get(subject)) {
                if (group.getGroupName().equalsIgnoreCase(groupName)) {
                    for (Exam exam : group.getExams()) {
                        if (exam.getExamNumber() == examNumber) {
                            return exam;
                        }
                    }
                }
            }
        }
        return null;
    }

    public void addGroup(String subject, Group group) {
        if (mGroups.containsKey(subject)) {
            mGroups.get(subject).add(group);
        }
        else {
            mGroups.put(subject, new ArrayList<>());
            mGroups.get(subject).add(group);
        }
        saveExamBank(this);
    }

    public void clearSubject(String subject) {
        if (mGroups.containsKey(subject)) {
            mGroups.remove(subject);
        }
    }

    /**
     * getHighestExamNumber
     *
     * Find the exam with the highest examNumber in the exam bank that has the provided group and
     * belongs to the provided subject. In case that the group hasn't been added before, return
     * the initial exam number of 1.
     * @param subjectName The name of the subject to which the group belongs
     * @param groupName The name of the group to search the highest exam number in
     * @return The highest exam number in the provided group and subject
     */
    public int getHighestExamNumber(String subjectName, String groupName) {
        if (mGroups.containsKey(subjectName)) {
            for (Group group : mGroups.get(subjectName)) {
                if (group.getGroupName().equalsIgnoreCase(groupName)) {
                    return group.getHighestExamNumber();
                }
            }
        }
        return 1;
    }
}

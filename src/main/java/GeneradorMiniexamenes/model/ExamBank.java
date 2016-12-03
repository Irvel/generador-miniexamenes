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

    /**
     * getGroups
     *
     * Given a subject return the list of groups that are linked to that subject. In case the
     * subject is not in the exam bank, return null.
     * @param subject The name of the subject that holds the groups to get
     * @return The list of groups that are associated with the given subject, null if not found
     */
    public ArrayList<Group> getGroups(String subject) {
        if (mGroups.containsKey(subject)) {
            return mGroups.get(subject);
        }
        return null;
    }

    /**
     * getGroup
     *
     * Given a subject and a group name return that group object instance
     * subject is not in the exam bank, return null.
     * @param subject The name of the subject that holds the group to get
     * @param groupName The name of the group to get
     * @return The group instance
     */
    public Group getGroup(String subject, String groupName) {
        if (mGroups.containsKey(subject)) {
            for (Group group : mGroups.get(subject)) {
                if (group.getGroupName().equalsIgnoreCase(groupName)) {
                    return group;
                }
            }
        }
        return null;
    }

    /**
     * getExams
     *
     * Given a subject and a group name, return the list of exams they contain.
     * @param subject The name of the subject that holds the exams
     * @param groupName The name of the group that holds the exams
     * @return The list of exams that are in groupName and subject
     */
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

    /**
     * getExam
     *
     * Given a subject, a group name and an exam number, return the exam corresponding to these
     * variables from the exambank. In case the exam was not found, return null.
     * @param subject The name of the subject from which the exam belongs to
     * @param groupName The name of the group from which the exam belongs to
     * @param examNumber The examNumber to get
     */
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

    /**
     * addGroup
     *
     * Add a group to the exam bank. In case the subject being added already exists, only append
     * the group to the existing groups for that subject. If it doesn't exist, then set the
     * received group as the first group in the groups list from that subject.
     * Given a subject name, and a
     * @param subject The name of the subject from the group to be added
     * @param newGroup The group to be added
     */
    public void addGroup(String subject, Group newGroup) {
        boolean groupFound = false;
        if (mGroups.containsKey(subject)) {
            // Check if the group already exists, so that only the exams are added
            for (Group group : mGroups.get(subject)) {
                if (group.getGroupName().equalsIgnoreCase(newGroup.getGroupName())) {
                    group.addExams(newGroup.getExams());
                    groupFound = true;
                }
            }
            if (!groupFound) {
                mGroups.get(subject).add(newGroup);
            }
        }
        else {
            mGroups.put(subject, new ArrayList<>());
            mGroups.get(subject).add(newGroup);
        }
        saveExamBank(this);
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
        return 0;
    }

    /**
     * deleteSubject
     *
     * Given a subject name, delete it from the exam bank
     * @param subject The name of the subject to be deleted
     */
    private void deleteSubject(String subject) {
        if (mGroups.containsKey(subject)) {
            mGroups.remove(subject);
        }
    }

    /**
     * deleteExam
     *
     * Search for the specified exam within the ExamBank and delete it. In case the deleted exam
     * was the last exam from the group, also delete the group. In case that subsequent group was
     * the last group in that subject, also delete the subject.
     * @param subject The name of the subject from which the exam belongs to
     * @param groupName The name of the group from which the exam belongs to
     * @param examNumber The examNumber to delete
     */
    public void deleteExam(String subject, String groupName, int examNumber) {
        Exam examToRemove = null;
        if (mGroups.containsKey(subject)) {
            for (Group group : mGroups.get(subject)) {
                if (group.getGroupName().equalsIgnoreCase(groupName)) {
                    for (Exam exam : group.getExams()) {
                        if (exam.getExamNumber() == examNumber) {
                            examToRemove = exam;
                        }
                    }
                }
            }
        }
        if (examToRemove != null) {
            ArrayList<Exam> exams = getExams(subject, groupName);
            exams.remove(examToRemove);
            // If this was the last exam in this group to be removed, remove the group
            if (exams.isEmpty()) {
                Group groupToRemove = null;
                ArrayList<Group> groups = getGroups(subject);
                for (Group group : groups) {
                    if (group.getGroupName().equalsIgnoreCase(groupName)) {
                        groupToRemove = group;
                    }
                }
                if (groupToRemove != null) {
                    groups.remove(groupToRemove);
                    // If this was also the last group associated to that subject, remove the
                    // subject
                    if (groups.isEmpty()) {
                        deleteSubject(subject);
                    }
                }
            }
            saveExamBank(this);
        }
    }
}

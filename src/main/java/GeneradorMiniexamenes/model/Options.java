package GeneradorMiniexamenes.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Set;

import static GeneradorMiniexamenes.controllers.AppState.saveOptions;

/**
 * Options
 *
 * General options to be used across the application. This includes the set of groups to be
 * displayed to the user and generated exam class title.
 * TODO: Add the option to delete a group
 */
public class Options {
    private HashSet<String> mGroupNames;
    private String mClassTitle;

    public Options(@JsonProperty("groupNames") HashSet<String> groupNames,
                   @JsonProperty("classTitle") String classTitle) {
        mGroupNames = groupNames;
        mClassTitle = classTitle;
    }

    public Options() {
        mGroupNames = new HashSet<>();
        mGroupNames.add("1");
        mGroupNames.add("2");
        mGroupNames.add("3");
        mGroupNames.add("4");
        mClassTitle = "Miniexamen Teoría de la Computación";
    }

    public Set<String> getGroupNames() {
        return mGroupNames;
    }

    public void addGroup(String groupName) {
        mGroupNames.add(groupName);
        saveOptions(this);
    }

    public String getClassTitle() {
        return mClassTitle;
    }

    public void setClassTitle(String classTitle) {
        mClassTitle = classTitle;
        saveOptions(this);
    }
}

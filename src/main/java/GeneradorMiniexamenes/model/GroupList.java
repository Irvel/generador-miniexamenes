package GeneradorMiniexamenes.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Irvel on 12/4/16.
 * The set of groups to be displayed to the user
 */
public class GroupList {
    HashSet<String> mGroupNames;

    public GroupList() {
        mGroupNames = new HashSet<>();
        mGroupNames.add("1");
        mGroupNames.add("2");
        mGroupNames.add("3");
        mGroupNames.add("4");
    }

    public Set<String> getGroupNames() {
        return mGroupNames;
    }

    public void addGroup(String groupName) {
        mGroupNames.add(groupName);
    }
}

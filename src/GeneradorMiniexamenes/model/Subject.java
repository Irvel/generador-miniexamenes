package GeneradorMiniexamenes.model;

import java.util.ArrayList;

public class Subject {
    private ArrayList<Block> mBlocks;
    private String mSubjectName;

    public Subject(ArrayList<Block> mBlocks, String subject) {
        this.mBlocks = mBlocks;
        this.mSubjectName = subject;
    }

    public ArrayList<Block> getBlocks() {
        return mBlocks;
    }

    public void setBlocks(ArrayList<Block> mBlocks) {
        this.mBlocks = mBlocks;
    }

    public String getSubjectName() {
        return mSubjectName;
    }

    public void setSubjectName(String subject) {
        this.mSubjectName = subject;
    }
}

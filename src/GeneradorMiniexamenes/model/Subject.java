package GeneradorMiniexamenes.model;

import java.util.ArrayList;

public class Subject {
    private ArrayList<Block> mBlocks;
    private String mSubject;

    public Subject(ArrayList<Block> mBlocks, String mSubject) {
        this.mBlocks = mBlocks;
        this.mSubject = mSubject;
    }

    public ArrayList<Block> getmBlocks() {
        return mBlocks;
    }

    public void setmBlocks(ArrayList<Block> mBlocks) {
        this.mBlocks = mBlocks;
    }

    public String getmSubject() {
        return mSubject;
    }

    public void setmSubject(String mSubject) {
        this.mSubject = mSubject;
    }
}

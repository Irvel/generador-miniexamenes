package GeneradorMiniexamenes.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Subject {
    private ArrayList<Block> mBlocks;
    private String mSubjectName;

    public Subject(@JsonProperty("blocks") ArrayList<Block> blocks,
                   @JsonProperty("subjectName") String subject) {
        this.mBlocks = blocks;
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

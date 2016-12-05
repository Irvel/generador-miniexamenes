package GeneradorMiniexamenes.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Subject {
    private ArrayList<Block> mBlocks;
    private String mSubjectName;

    public Subject(@JsonProperty("subjectName") String subject,
                   @JsonProperty("blocks") ArrayList<Block> blocks) {
        this.mSubjectName = subject;
        this.mBlocks = blocks;
    }

    public String getSubjectName() {
        return mSubjectName;
    }

    public void setSubjectName(String subject) {
        this.mSubjectName = subject;
    }

    public ArrayList<Block> getBlocks() {
        return mBlocks;
    }
}

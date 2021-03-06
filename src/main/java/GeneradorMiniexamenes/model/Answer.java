package GeneradorMiniexamenes.model;

import com.fasterxml.jackson.annotation.JsonProperty;

// Assumes that the text in each answer is unique
public class Answer {
    private String mAnswer;
    private int mWeight;

    public Answer(@JsonProperty("answer") String answer,
                  @JsonProperty("weight") int weight){
        this.mAnswer = answer;
        this.mWeight = weight;
    }

    public String getAnswer() {
        return mAnswer;
    }

    public int getWeight() {
        return mWeight;
    }
}

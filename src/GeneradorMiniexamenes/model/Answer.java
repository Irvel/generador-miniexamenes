package GeneradorMiniexamenes.model;

public class Answer {
    private String mAnswer;
    private int mWeight;

    public Answer(String answer, int weight) {
        this.mAnswer = answer;
        this.mWeight = weight;
    }

    public String getAnswer() {
        return mAnswer;
    }

    public void setAnswer(String answer) {
        mAnswer = answer;
    }

    public int getWeight() {
        return mWeight;
    }

    public void setWeight(int weight) {
        this.mWeight = weight;
    }
}

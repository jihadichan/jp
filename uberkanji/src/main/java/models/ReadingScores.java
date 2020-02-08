package models;

public class ReadingScores {

    private Integer score = 0;
    private Integer fbKJ = 0; // followed by kanji
    private Integer fbKN = 0; // followed by kana

    public ReadingScores(int score) {
        this.score = score;
    }

    public void incrementScoreBy(int valueToAdd) {
        this.score += valueToAdd;
    }

    public void decrementScore() {
        this.score--;
    }

    public void incrementFbKJ() {
        this.fbKJ++;
    }

    public void incrementFbKN() {
        this.fbKN++;
    }

    public Integer getScore() {
        return this.score;
    }

    public Integer getFbKJ() {
        return this.fbKJ;
    }

    public Integer getFbKN() {
        return this.fbKN;
    }
}

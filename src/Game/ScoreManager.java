package src.Game;

public class ScoreManager {
    private int score;

    public ScoreManager() {
        this.score = 0;
    }

    public int getScore() {
        return score;
    }

    public void resetScore() {
        this.score = 0;
    }

    public void addScore(int points) {
        if(points < 0){
            throw new IllegalArgumentException("Points cannot be negative");
        }
        this.score += points;
    }

    public void subtractScore(int points) {
        if(points < 0){
            throw new IllegalArgumentException("Points cannot be negative");
        }
        this.score -= points;
        if(this.score < 0){
            this.score = 0; // skor tidak boleh minus
        }
    }
}

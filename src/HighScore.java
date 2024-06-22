import java.io.Serializable;

public class HighScore implements Serializable {
    static final long serialVersionUID = 1L;
    String name;
    int score;

    public HighScore(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return name + ": " + score;
    }
}

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HighScoreManager {
    private List<HighScore> highScores;
    private static final String HIGH_SCORES_FILE = "highscores.dat";

    public HighScoreManager() {
        loadHighScores();
    }

    public void addHighScore(String name, int score) {
        highScores.add(new HighScore(name, score));
        highScores.sort((hs1, hs2) -> Integer.compare(hs2.getScore(), hs1.getScore())); // Sort in descending order
        saveHighScores();
    }

    public List<HighScore> getHighScores() {
        return highScores;
    }

    @SuppressWarnings("unchecked")
    private void loadHighScores() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HIGH_SCORES_FILE))) {
            highScores = (List<HighScore>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            highScores = new ArrayList<>();
        }
    }

    private void saveHighScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HIGH_SCORES_FILE))) {
            oos.writeObject(highScores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

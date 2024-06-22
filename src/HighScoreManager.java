import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScoreManager implements Serializable{
    List<HighScore> highScores;
    static final String HIGH_SCORES_FILE = "highscores.txt";

    public HighScoreManager() {
        loadHighScores();
    }

    public void addHighScore(String name, int score) {
        highScores.add(new HighScore(name, score));
        Collections.sort(highScores, (hs1, hs2) -> Integer.compare(hs2.getScore(), hs1.getScore())); // Sort in descending order
        saveHighScores();
    }

    public List<HighScore> getHighScores() {
        return highScores;
    }

    private void loadHighScores() {
        highScores = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(HIGH_SCORES_FILE), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    int score = Integer.parseInt(parts[1].trim());
                    highScores.add(new HighScore(name, score));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading high scores: " + e.getMessage());
        }
    }

    private void saveHighScores() {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(HIGH_SCORES_FILE), StandardCharsets.UTF_8))) {
            for (HighScore highScore : highScores) {
                bw.write(highScore.getName() + ": " + highScore.getScore());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving high scores: " + e.getMessage());
        }
    }
}

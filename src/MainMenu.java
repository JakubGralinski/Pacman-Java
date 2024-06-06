import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu {
    private JFrame frame;

    public MainMenu() {
        frame = new JFrame("Pacman Main Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null); // Center the window

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // New Game Button
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newGame();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(newGameButton, gbc);

        // High Scores Button
        JButton highScoresButton = new JButton("High Scores");
        highScoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewHighScores();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(highScoresButton, gbc);

        // Exit Button
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(exitButton, gbc);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void newGame() {
        // Close the main menu
        frame.dispose();

        // Prompt for board size using custom dialog
        String[] boardSizes = {"Small", "Medium", "Large", "Extra Large", "Giant"};
        JComboBox<String> comboBox = new JComboBox<>(boardSizes);

        int result = JOptionPane.showConfirmDialog(null, comboBox, "Select Board Size",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String size = (String) comboBox.getSelectedItem();
            if (size != null) {
                // Initialize and start the game with the selected board size
                PacmanGame game = new PacmanGame(size);
                game.startGame();
            }
        }
    }

    private void viewHighScores() {
        // Display high scores in a new window
        JFrame highScoresFrame = new JFrame("High Scores");
        highScoresFrame.setSize(300, 200);
        highScoresFrame.setLocationRelativeTo(null);

        DefaultListModel<String> highScoresModel = new DefaultListModel<>();
        // Load high scores from file or other storage
        // For now, let's add some dummy data
        highScoresModel.addElement("1. Player1 - 1000");
        highScoresModel.addElement("2. Player2 - 800");
        highScoresModel.addElement("3. Player3 - 600");

        JList<String> highScoresList = new JList<>(highScoresModel);
        JScrollPane scrollPane = new JScrollPane(highScoresList);
        highScoresFrame.add(scrollPane);

        highScoresFrame.setVisible(true);
    }

    // Main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu());
    }
}

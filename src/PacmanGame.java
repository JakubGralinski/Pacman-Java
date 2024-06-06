import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class PacmanGame {
    private Player player;
    private List<Enemy> enemies;
    private Board board;
    private BoardPanel boardPanel;
    private Image pacmanImage;
    private Image enemyImage;

    public PacmanGame(String boardSize) {
        // Load images
        try {
            pacmanImage = ImageIO.read(new File("src/sprites/pacman.png"));
            enemyImage = ImageIO.read(new File("src/sprites/sprite_red.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        displayMainMenu();
    }

    public void displayMainMenu() {
        JFrame mainMenuFrame = new JFrame("Pacman Game - Main Menu");
        mainMenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainMenuFrame.setSize(400, 300);
        mainMenuFrame.setLayout(new GridLayout(3, 1));

        JButton newGameButton = new JButton("New Game");
        JButton highScoresButton = new JButton("High Scores");
        JButton exitButton = new JButton("Exit");

        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainMenuFrame.dispose();
                startNewGame();
            }
        });

        highScoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement display of high scores
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        mainMenuFrame.add(newGameButton);
        mainMenuFrame.add(highScoresButton);
        mainMenuFrame.add(exitButton);

        mainMenuFrame.setLocationRelativeTo(null); // Center the window
        mainMenuFrame.setVisible(true);
    }

    private void startNewGame() {
        String[] boardSizes = {"Small", "Medium", "Large", "Extra Large", "Giant"};
        String selectedBoardSize = (String) JOptionPane.showInputDialog(null, "Choose board size:",
                "Board Size Selection", JOptionPane.QUESTION_MESSAGE, null, boardSizes, boardSizes[0]);

        if (selectedBoardSize != null) {
            initializeGame(selectedBoardSize);
            startGame();
        }
    }

    private void initializeGame(String boardSize) {
        this.player = new Player(1, 1, 1, 3); // Initialize player with 3 lives
        this.enemies = new ArrayList<>();
        this.enemies.add(new Enemy(5, 5, 1));
        this.enemies.add(new Enemy(10, 10, 1));
        this.board = new Board(boardSize);
        this.boardPanel = new BoardPanel(board, player, enemies, pacmanImage, enemyImage);
    }

    public void startGame() {
        JFrame gameFrame = new JFrame("Pacman Game");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.add(boardPanel);
        gameFrame.pack();
        gameFrame.setLocationRelativeTo(null); // Center the window
        gameFrame.setVisible(true);

        boardPanel.addKeyListener(new PlayerMovementListener());
        boardPanel.setFocusable(true);
        boardPanel.requestFocusInWindow();

        new Thread(() -> {
            while (player.getLives() > 0) { // Check if player has lives remaining
                try {
                    Thread.sleep(500); // Game loop sleep duration
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (Enemy enemy : enemies) {
                    enemy.move(board);
                    if (enemy.checkCollision(player)) {
                        player.loseLife(); // Subtract a life on collision
                        if (player.getLives() <= 0) {
                            gameOver(); // End game if no lives left
                            return;
                        }
                    }
                }
                boardPanel.repaint();
            }
        }).start();
    }

    private void gameOver() {
        JOptionPane.showMessageDialog(null, "Game Over! You have no more lives left.");
        System.exit(0);
    }

    private class PlayerMovementListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            int dx = 0, dy = 0;

            switch (keyCode) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    dy = -1;
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    dy = 1;
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    dx = -1;
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    dx = 1;
                    break;
            }

            if (dx != 0 || dy != 0) {
                player.move(dx, dy, board);
                boardPanel.repaint();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PacmanGame("Giant"));
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
        mainMenuFrame.setSize(800, 600);
        mainMenuFrame.setLayout(new GridLayout(3, 1));

        JButton newGameButton = new JButton("New Game");
        JButton highScoresButton = new JButton("High Scores");
        JButton exitButton = new JButton("Exit");

        newGameButton.addActionListener(e -> {
            mainMenuFrame.dispose();
            startNewGame();
        });

        highScoresButton.addActionListener(e -> {
            // Implement display of high scores
        });

        exitButton.addActionListener(e -> System.exit(0));

        mainMenuFrame.add(newGameButton);
        mainMenuFrame.add(highScoresButton);
        mainMenuFrame.add(exitButton);

        mainMenuFrame.setLocationRelativeTo(null);
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
        this.board = new Board(boardSize);

        // Ensure player and enemies spawn in different positions
        int playerX, playerY, enemyX1, enemyY1, enemyX2, enemyY2;
        int initialSpeed = 1;
        int playerLives = 3;

        do {
            playerX = (int) (Math.random() * board.getWidth());
            playerY = (int) (Math.random() * board.getHeight());
            enemyX1 = (int) (Math.random() * board.getWidth());
            enemyY1 = (int) (Math.random() * board.getHeight());
            enemyX2 = (int) (Math.random() * board.getWidth());
            enemyY2 = (int) (Math.random() * board.getHeight());
        } while ((playerX == enemyX1 && playerY == enemyY1) || (playerX == enemyX2 && playerY == enemyY2) || (enemyX1 == enemyX2 && enemyY1 == enemyY2));

        this.player = new Player(playerX, playerY, initialSpeed, playerLives);
        this.enemies = new ArrayList<>();
        this.enemies.add(new Enemy(enemyX1, enemyY1, initialSpeed));
        this.enemies.add(new Enemy(enemyX2, enemyY2, initialSpeed));
        this.boardPanel = new BoardPanel(board, player, enemies, pacmanImage, enemyImage);
    }

    public void startGame() {
        JFrame gameFrame = new JFrame("Pacman Game");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.add(boardPanel);
        gameFrame.pack();
        gameFrame.setSize(board.getWidth() * 40, board.getHeight() * 40);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
        gameFrame.setResizable(true);

        gameFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                boardPanel.setSize(gameFrame.getSize());
                boardPanel.repaint();
            }
        });

        boardPanel.addKeyListener(new PlayerMovementListener());
        boardPanel.setFocusable(true);
        boardPanel.requestFocusInWindow();

        new Thread(() -> {
            while (player.getLives() > 0) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (Enemy enemy : enemies) {
                    enemy.move(board);
                    if (enemy.checkCollision(player)) {
                        player.loseLife();
                        if (player.getLives() <= 0) {
                            gameOver();
                            return;
                        }
                    }
                }
                boardPanel.render();
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
                boardPanel.render();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PacmanGame("Giant"));
    }
}

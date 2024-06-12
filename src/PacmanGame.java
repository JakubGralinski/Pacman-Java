import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

public class PacmanGame {
    private Player player;
    private List<Enemy> enemies;
    private Board board;
    private BoardPanel boardPanel;
    private Image pacmanImage;
    private Image enemyImage;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Random random = new Random();
    private boolean[] keysHeld = new boolean[4];
    private JFrame gameFrame;
    private JFrame mainMenuFrame;

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
        if (mainMenuFrame != null) {
            mainMenuFrame.dispose();
        }

        mainMenuFrame = new JFrame("Pacman Game - Main Menu");
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
        List<int[]> validPositions = board.getValidSpawnPositions();

        // Ensure player and enemies spawn in different positions
        int[] playerPos, enemyPos1, enemyPos2;
        int initialSpeed = 1;
        int playerLives = 3;

        do {
            playerPos = validPositions.get(random.nextInt(validPositions.size()));
            enemyPos1 = validPositions.get(random.nextInt(validPositions.size()));
            enemyPos2 = validPositions.get(random.nextInt(validPositions.size()));
        } while ((playerPos[0] == enemyPos1[0] && playerPos[1] == enemyPos1[1]) ||
                (playerPos[0] == enemyPos2[0] && playerPos[1] == enemyPos2[1]) ||
                (enemyPos1[0] == enemyPos2[0] && enemyPos1[1] == enemyPos2[1]));

        this.player = new Player(playerPos[0], playerPos[1], initialSpeed, playerLives);
        this.enemies = new ArrayList<>();
        this.enemies.add(new Enemy(enemyPos1[0], enemyPos1[1], initialSpeed));
        this.enemies.add(new Enemy(enemyPos2[0], enemyPos2[1], initialSpeed));
        this.boardPanel = new BoardPanel(board, player, enemies, pacmanImage, enemyImage);
    }

    public void startGame() {
        if (gameFrame != null) {
            gameFrame.dispose();
        }

        gameFrame = new JFrame("Pacman Game");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.add(boardPanel);
        gameFrame.pack();
        gameFrame.setSize(new Dimension(boardPanel.getPreferredSize()));
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);

        gameFrame.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                boardPanel.setPreferredSize(new Dimension(gameFrame.getWidth(), gameFrame.getHeight()));
                boardPanel.revalidate();
                boardPanel.repaint();
            }
        });

        boardPanel.addKeyListener(new PlayerMovementListener());
        boardPanel.setFocusable(true);
        boardPanel.requestFocusInWindow();

        new Thread(() -> {
            while (player.getLives() > 0) {
                try {
                    Thread.sleep(100); // Check for collisions more frequently
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (keysHeld[Player.UP]) player.setDirection(Player.UP);
                if (keysHeld[Player.DOWN]) player.setDirection(Player.DOWN);
                if (keysHeld[Player.LEFT]) player.setDirection(Player.LEFT);
                if (keysHeld[Player.RIGHT]) player.setDirection(Player.RIGHT);

                player.move(board, enemies);

                // Check for collisions and subtract lives accordingly
                for (Enemy enemy : enemies) {
                    enemy.move(board, enemies);
                    if (enemy.checkCollision(player)) {
                        player.loseLife();
                        if (player.getLives() <= 0) {
                            gameOver();
                            return;
                        }
                    }
                }

                // Check if all pellets are collected
                if (!board.hasPellets()) {
                    winGame();
                    return;
                }

                boardPanel.render();
            }
        }).start();

        scheduler.scheduleAtFixedRate(this::spawnUpgrades, 0, 5, TimeUnit.SECONDS);
    }

    private void spawnUpgrades() {
        for (Enemy enemy : enemies) {
            enemy.tryToSpawnUpgrade(board);
        }
    }

    private void gameOver() {
        JOptionPane.showMessageDialog(null, "Game Over! You have no more lives left.");
        gameFrame.dispose();
        displayMainMenu();
    }

    private void winGame() {
        JOptionPane.showMessageDialog(null, "Congratulations! You collected all the pellets!");
        gameFrame.dispose();
        displayMainMenu();
    }

    private class PlayerMovementListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    keysHeld[Player.UP] = true;
                    player.setDirection(Player.UP);
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    keysHeld[Player.DOWN] = true;
                    player.setDirection(Player.DOWN);
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    keysHeld[Player.LEFT] = true;
                    player.setDirection(Player.LEFT);
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    keysHeld[Player.RIGHT] = true;
                    player.setDirection(Player.RIGHT);
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    keysHeld[Player.UP] = false;
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    keysHeld[Player.DOWN] = false;
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    keysHeld[Player.LEFT] = false;
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    keysHeld[Player.RIGHT] = false;
                    break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PacmanGame("Giant"));
    }
}

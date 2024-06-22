package controller;

import model.Board;
import model.Enemy;
import util.HighScore;
import util.HighScoreManager;
import model.Player;
import view.BoardPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PacmanGame {
    private Player player;
    private List<Enemy> enemies;
    private Board board;
    private BoardPanel boardPanel;
    private Image pacmanOpenMouthImage;
    private Image pacmanClosedMouthImage;
    private List<Image> enemyImages;
    private Random random = new Random();
    private boolean[] keysHeld = new boolean[4];
    private JFrame gameFrame;
    private JFrame mainMenuFrame;
    private HighScoreManager highScoreManager = new HighScoreManager();
    private final Object gameLock = new Object(); // Synchronization lock for game state
    boolean running;

    public PacmanGame(String boardSize) {
        try {
            pacmanOpenMouthImage = ImageIO.read(new File("src/sprites/pacman_open.png"));
            pacmanClosedMouthImage = ImageIO.read(new File("src/sprites/pacman_closed.png"));
            enemyImages = loadEnemyImages();
        } catch (IOException e) {
            e.printStackTrace();
        }
        displayMainMenu();
    }

    private List<Image> loadEnemyImages() throws IOException {
        List<Image> images = new ArrayList<>();
        images.add(ImageIO.read(new File("src/sprites/sprite_blue.png")));
        images.add(ImageIO.read(new File("src/sprites/sprite_pink.png")));
        images.add(ImageIO.read(new File("src/sprites/sprite_red.png")));
        images.add(ImageIO.read(new File("src/sprites/sprite_purple.png")));
        return images;
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

        highScoresButton.addActionListener(e -> displayHighScores());

        exitButton.addActionListener(e -> System.exit(0));

        mainMenuFrame.add(newGameButton);
        mainMenuFrame.add(highScoresButton);
        mainMenuFrame.add(exitButton);

        mainMenuFrame.setLocationRelativeTo(null);
        mainMenuFrame.setVisible(true);
    }

    private void startNewGame() {
        String[] boardSizes = {"Map1", "Map2", "Map3", "Map4", "Map5"};
        String selectedBoardSize = (String) JOptionPane.showInputDialog(null, "Choose your map: ",
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
        float playerSpeed = 1f;
        float enemySpeed = 2f;
        int playerLives = 3;

        do {
            playerPos = validPositions.get(random.nextInt(validPositions.size()));
            enemyPos1 = validPositions.get(random.nextInt(validPositions.size()));
            enemyPos2 = validPositions.get(random.nextInt(validPositions.size()));
        } while ((playerPos[0] == enemyPos1[0] && playerPos[1] == enemyPos1[1]) ||
                (playerPos[0] == enemyPos2[0] && playerPos[1] == enemyPos2[1]) ||
                (enemyPos1[0] == enemyPos2[0] && enemyPos1[1] == enemyPos2[1]));

        this.player = new Player(playerPos[0], playerPos[1], playerSpeed, playerLives);
        this.enemies = new ArrayList<>();

        int numberOfEnemies = 4;
        for (int i = 0; i < numberOfEnemies; i++) {
            int[] enemyPos = validPositions.get(random.nextInt(validPositions.size()));
            this.enemies.add(new Enemy(enemyPos[0], enemyPos[1], enemySpeed, enemyImages.get(random.nextInt(enemyImages.size()))));
        }
        this.boardPanel = new BoardPanel(board, player, enemies, pacmanOpenMouthImage, pacmanClosedMouthImage);
    }

    public void startGame() {
        if (gameFrame != null) {
            gameFrame.dispose();
        }

        gameFrame = new JFrame("Pacman Game");
        gameFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        gameFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gameFrame.dispose();
                displayMainMenu();
            }
        });
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

        new Thread(this::gameLoop).start();
        new Thread(this::enemyMovementLoop).start();
        new Thread(this::spawnUpgrades).start();
    }

    private void resetGameState() {
        player.resetSpeed();
        for (Enemy enemy : enemies) {
            enemy.resetSpeed();
        }
    }

    private void gameLoop() {
        while (player.getLives() > 0) {
            try {
                Thread.sleep(100); // Check for collisions more frequently
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (gameLock) {
                if (keysHeld[Player.UP]) player.setDirection(Player.UP);
                if (keysHeld[Player.DOWN]) player.setDirection(Player.DOWN);
                if (keysHeld[Player.LEFT]) player.setDirection(Player.LEFT);
                if (keysHeld[Player.RIGHT]) player.setDirection(Player.RIGHT);

                player.move(board, enemies);

                // Check for collisions and subtract lives accordingly
                for (Enemy enemy : enemies) {
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

                boardPanel.repaint();
            }
        }
    }

    private void enemyMovementLoop() {
        while (player.getLives() > 0) {
            try {
                Thread.sleep((int) (500 / enemies.get(0).getSpeed())); // Adjust this value for enemy movement speed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (gameLock) {
                for (Enemy enemy : enemies) {
                    enemy.move(board, enemies);
                }
                boardPanel.repaint();
            }
        }
    }

    private void spawnUpgrades() {
        while (true) {
            try {
                Thread.sleep(5000); // Check every 5 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (gameLock) {
                for (Enemy enemy : enemies) {
                    enemy.tryToSpawnUpgrade(board);
                }
                boardPanel.repaint();
            }
        }
    }

    private void gameOver() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, "Game Over! You have no more lives left.");
            gameFrame.dispose();
            resetGameState();
            displayMainMenu();
        });
    }

    private void winGame() {
        SwingUtilities.invokeLater(() -> {
            String name = JOptionPane.showInputDialog(null, "Congratulations! You collected all the pellets! Enter your name:");
            if (name != null && !name.trim().isEmpty()) {
                highScoreManager.addHighScore(name, player.getScore());
            }
            gameFrame.dispose();
            resetGameState();
            displayMainMenu();
        });
    }

    private void displayHighScores() {
        SwingUtilities.invokeLater(() -> {
            JFrame highScoreFrame = new JFrame("High Scores");
            highScoreFrame.setSize(400, 600);
            highScoreFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            List<HighScore> highScores = highScoreManager.getHighScores();
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (HighScore highScore : highScores) {
                listModel.addElement(highScore.toString());
            }

            JList<String> highScoreList = new JList<>(listModel);
            JScrollPane scrollPane = new JScrollPane(highScoreList);
            highScoreFrame.add(scrollPane);

            highScoreFrame.setLocationRelativeTo(null);
            highScoreFrame.setVisible(true);
        });
    }

    private class PlayerMovementListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            synchronized (gameLock) {
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
        }

        @Override
        public void keyReleased(KeyEvent e) {
            synchronized (gameLock) {
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
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PacmanGame("Giant"));
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

public class BoardPanel extends JPanel {
    Board board;
    Player player;
    List<Enemy> enemies;
    Image pacmanImage;
    Image enemyImage;
    Image speedBoostImage;
    Image extraLifeImage;
    Image doublePointsImage;
    Image passThroughWallsImage;
    Image slowEnemiesImage;
    Image pelletImage;
    private BufferedImage offscreenImage;
    private Graphics2D offscreenGraphics;

    public BoardPanel(Board board, Player player, List<Enemy> enemies, Image pacmanImage, Image enemyImage) {
        this.board = board;
        this.player = player;
        this.enemies = enemies;
        this.pacmanImage = pacmanImage;
        this.enemyImage = enemyImage;

        this.speedBoostImage = loadImage("src/sprites/apple.png", 40, 40);
        this.extraLifeImage = loadImage("src/sprites/cherry.png", 40, 40);
        this.doublePointsImage = loadImage("src/sprites/green_apple.png", 40, 40);
        this.passThroughWallsImage = loadImage("src/sprites/strawberry.png", 40, 40);
        this.slowEnemiesImage = loadImage("src/sprites/rod.png", 40, 40);
        this.pelletImage = loadImage("src/sprites/pellet.png", 20, 20); // Load and resize your pellet image here

        setPreferredSize(new Dimension(board.getWidth() * 40, board.getHeight() * 40));
    }

    private Image loadImage(String path, int width, int height) {
        try {
            Image img = ImageIO.read(new File(path));
            return img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            System.err.println("Error loading image: " + path);
            e.printStackTrace();
            return null;
        }
    }

    public void render() {
        if (offscreenImage == null || offscreenImage.getWidth() != getWidth() || offscreenImage.getHeight() != getHeight()) {
            offscreenImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            offscreenGraphics = offscreenImage.createGraphics();
        }

        offscreenGraphics.setColor(Color.BLACK);
        offscreenGraphics.fillRect(0, 0, getWidth(), getHeight());

        int cellWidth = getWidth() / board.getWidth();
        int cellHeight = getHeight() / board.getHeight();
        int cellSize = Math.min(cellWidth, cellHeight);

        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                if (board.isWall(x, y)) {
                    offscreenGraphics.setColor(Color.BLUE);
                    offscreenGraphics.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                } else if (board.hasPellet(x, y)) {
                    offscreenGraphics.drawImage(pelletImage, x * cellSize + (cellSize - pelletImage.getWidth(null)) / 2, y * cellSize + (cellSize - pelletImage.getHeight(null)) / 2, null);
                }
            }
        }

        for (Upgrade upgrade : board.getUpgrades()) {
            Image upgradeImage = null;
            switch (upgrade.getType()) {
                case Upgrade.SPEED_BOOST:
                    upgradeImage = speedBoostImage;
                    break;
                case Upgrade.EXTRA_LIFE:
                    upgradeImage = extraLifeImage;
                    break;
                case Upgrade.DOUBLE_POINTS:
                    upgradeImage = doublePointsImage;
                    break;
                case Upgrade.PASS_THROUGH_WALLS:
                    upgradeImage = passThroughWallsImage;
                    break;
                case Upgrade.SLOW_ENEMIES:
                    upgradeImage = slowEnemiesImage;
                    break;
            }
            if (upgradeImage != null) {
                offscreenGraphics.drawImage(upgradeImage, upgrade.getX() * cellSize, upgrade.getY() * cellSize, cellSize, cellSize, null);
            }
        }

        int playerX = player.getX() * cellSize;
        int playerY = player.getY() * cellSize;
        int direction = player.getDirection();

        Graphics2D g2d = (Graphics2D) offscreenGraphics.create();

        switch (direction) {
            case Player.RIGHT:
                g2d.drawImage(pacmanImage, playerX, playerY, cellSize, cellSize, null);
                break;
            case Player.LEFT:
                g2d.rotate(Math.toRadians(180), playerX + cellSize / 2.0, playerY + cellSize / 2.0);
                g2d.drawImage(pacmanImage, playerX, playerY, cellSize, cellSize, null);
                g2d.rotate(Math.toRadians(-180), playerX + cellSize / 2.0, playerY + cellSize / 2.0);
                break;
            case Player.UP:
                g2d.rotate(Math.toRadians(-90), playerX + cellSize / 2.0, playerY + cellSize / 2.0);
                g2d.drawImage(pacmanImage, playerX, playerY, cellSize, cellSize, null);
                g2d.rotate(Math.toRadians(90), playerX + cellSize / 2.0, playerY + cellSize / 2.0);
                break;
            case Player.DOWN:
                g2d.rotate(Math.toRadians(90), playerX + cellSize / 2.0, playerY + cellSize / 2.0);
                g2d.drawImage(pacmanImage, playerX, playerY, cellSize, cellSize, null);
                g2d.rotate(Math.toRadians(-90), playerX + cellSize / 2.0, playerY + cellSize / 2.0);
                break;
        }
        g2d.dispose();

        for (Enemy enemy : enemies) {
            offscreenGraphics.drawImage(enemyImage, enemy.getX() * cellSize, enemy.getY() * cellSize, cellSize, cellSize, null);
        }

        offscreenGraphics.setColor(Color.WHITE);
        offscreenGraphics.setFont(new Font("Arial", Font.BOLD, 20));
        offscreenGraphics.drawString("Score: " + player.getScore(), 10, 25);
        offscreenGraphics.drawString("Lives: " + player.getLives(), 10, 50);

        Graphics g2dPanel = getGraphics();
        g2dPanel.drawImage(offscreenImage, 0, 0, this);
        g2dPanel.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(board.getWidth() * 40, board.getHeight() * 40);
    }
}

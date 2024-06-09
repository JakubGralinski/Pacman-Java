import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class BoardPanel extends JPanel {
    private final Board board;
    private final Player player;
    private final List<Enemy> enemies;
    private final Image pacmanImage;
    private final Image enemyImage;
    private BufferedImage offscreenImage;
    private Graphics2D offscreenGraphics;

    public BoardPanel(Board board, Player player, List<Enemy> enemies, Image pacmanImage, Image enemyImage) {
        this.board = board;
        this.player = player;
        this.enemies = enemies;
        this.pacmanImage = pacmanImage;
        this.enemyImage = enemyImage;
        setPreferredSize(new Dimension(board.getWidth() * 40, board.getHeight() * 40));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

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
                    offscreenGraphics.setColor(Color.YELLOW);
                    int pelletSize = cellSize / 3;
                    offscreenGraphics.fillOval(x * cellSize + (cellSize - pelletSize) / 2, y * cellSize + (cellSize - pelletSize) / 2, pelletSize, pelletSize);
                }
            }
        }

        int playerX = player.getX() * cellSize;
        int playerY = player.getY() * cellSize;
        int direction = player.getDirection();

        switch (direction) {
            case Player.RIGHT:
                offscreenGraphics.drawImage(pacmanImage, playerX, playerY, cellSize, cellSize, null);
                break;
            case Player.LEFT:
                offscreenGraphics.rotate(Math.toRadians(180), playerX + cellSize / 2.0, playerY + cellSize / 2.0);
                offscreenGraphics.drawImage(pacmanImage, playerX, playerY, cellSize, cellSize, null);
                offscreenGraphics.rotate(-Math.toRadians(180), playerX + cellSize / 2.0, playerY + cellSize / 2.0);
                break;
            case Player.UP:
                offscreenGraphics.rotate(Math.toRadians(-90), playerX + cellSize / 2.0, playerY + cellSize / 2.0);
                offscreenGraphics.drawImage(pacmanImage, playerX, playerY, cellSize, cellSize, null);
                offscreenGraphics.rotate(-Math.toRadians(-90), playerX + cellSize / 2.0, playerY + cellSize / 2.0);
                break;
            case Player.DOWN:
                offscreenGraphics.rotate(Math.toRadians(90), playerX + cellSize / 2.0, playerY + cellSize / 2.0);
                offscreenGraphics.drawImage(pacmanImage, playerX, playerY, cellSize, cellSize, null);
                offscreenGraphics.rotate(-Math.toRadians(90), playerX + cellSize / 2.0, playerY + cellSize / 2.0);
                break;
        }

        for (Enemy enemy : enemies) {
            offscreenGraphics.drawImage(enemyImage, enemy.getX() * cellSize, enemy.getY() * cellSize, cellSize, cellSize, null);
        }

        offscreenGraphics.setColor(Color.WHITE);
        offscreenGraphics.setFont(new Font("Arial", Font.BOLD, 20));
        offscreenGraphics.drawString("Score: " + player.getScore(), 10, 25);
        offscreenGraphics.drawString("Lives: " + player.getLives(), 10, 50);

        g.drawImage(offscreenImage, 0, 0, this);
    }

    public void render() {
        repaint();
    }
}

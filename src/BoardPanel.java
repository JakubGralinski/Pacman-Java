import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BoardPanel extends JPanel {
    private final Board board;
    private final Player player;
    private final List<Enemy> enemies;
    private final Image pacmanImage;
    private final Image enemyImage;

    public BoardPanel(Board board, Player player, List<Enemy> enemies, Image pacmanImage, Image enemyImage) {
        this.board = board;
        this.player = player;
        this.enemies = enemies;
        this.pacmanImage = pacmanImage;
        this.enemyImage = enemyImage;
        setPreferredSize(new Dimension(board.getWidth() * 20, board.getHeight() * 20));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Set background color to black
        setBackground(Color.BLACK);

        // Calculate scaling factors
        int cellWidth = getWidth() / board.getWidth();
        int cellHeight = getHeight() / board.getHeight();

        // Draw the board
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                if (board.isWall(x, y)) {
                    g.setColor(Color.BLUE);
                    g.fillRect(x * cellWidth, y * cellHeight, cellWidth, cellHeight);
                } else if (board.hasPellet(x, y)) {
                    g.setColor(Color.YELLOW);
                    int pelletSize = Math.min(cellWidth, cellHeight) / 3;
                    g.fillOval(x * cellWidth + (cellWidth - pelletSize) / 2, y * cellHeight + (cellHeight - pelletSize) / 2, pelletSize, pelletSize);
                }
            }
        }

        // Draw the player with rotation based on direction
        Graphics2D g2d = (Graphics2D) g.create();
        int playerX = player.getX() * cellWidth;
        int playerY = player.getY() * cellHeight;
        int direction = player.getDirection();

        // Rotate the image based on direction
        switch (direction) {
            case Player.RIGHT:
                g2d.drawImage(pacmanImage, playerX, playerY, cellWidth, cellHeight, this);
                break;
            case Player.LEFT:
                g2d.rotate(Math.toRadians(180), playerX + cellWidth / 2.0, playerY + cellHeight / 2.0);
                g2d.drawImage(pacmanImage, playerX, playerY, cellWidth, cellHeight, this);
                g2d.rotate(-Math.toRadians(180), playerX + cellWidth / 2.0, playerY + cellHeight / 2.0);
                break;
            case Player.UP:
                g2d.rotate(Math.toRadians(-90), playerX + cellWidth / 2.0, playerY + cellHeight / 2.0);
                g2d.drawImage(pacmanImage, playerX, playerY, cellWidth, cellHeight, this);
                g2d.rotate(-Math.toRadians(-90), playerX + cellWidth / 2.0, playerY + cellHeight / 2.0);
                break;
            case Player.DOWN:
                g2d.rotate(Math.toRadians(90), playerX + cellWidth / 2.0, playerY + cellHeight / 2.0);
                g2d.drawImage(pacmanImage, playerX, playerY, cellWidth, cellHeight, this);
                g2d.rotate(-Math.toRadians(90), playerX + cellWidth / 2.0, playerY + cellHeight / 2.0);
                break;
        }

        g2d.dispose();

        // Draw the enemies
        for (Enemy enemy : enemies) {
            g.drawImage(enemyImage, enemy.getX() * cellWidth, enemy.getY() * cellHeight, cellWidth, cellHeight, this);
        }

        // Draw the score and lives as an overlay
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + player.getScore(), 10, 25);
        g.drawString("Lives: " + player.getLives(), 10, 50);
    }
}

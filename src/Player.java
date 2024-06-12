import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Player extends Character {
    private int score;
    private int direction;
    private int lives;
    private boolean doublePointsActive;
    private boolean passThroughWalls;
    private int normalSpeed;
    private int pendingDirection;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // Constants for directions
    public static final int RIGHT = 0;
    public static final int LEFT = 1;
    public static final int DOWN = 2;
    public static final int UP = 3;

    public Player(int initialX, int initialY, int initialSpeed, int lives) {
        super(initialX, initialY, initialSpeed);
        this.score = 0;
        this.direction = RIGHT; // Initial direction facing right
        this.lives = lives;
        this.doublePointsActive = false;
        this.passThroughWalls = false;
        this.normalSpeed = initialSpeed;
        this.pendingDirection = -1;
    }

    public int getScore() {
        return score;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.pendingDirection = direction;
    }

    public int getLives() {
        return lives;
    }

    public void loseLife() {
        if (lives > 0) {
            lives--;
        }
    }

    public void gainLife() {
        lives++;
    }

    public void activateDoublePoints() {
        doublePointsActive = true;
        scheduler.schedule(() -> doublePointsActive = false, 5, TimeUnit.SECONDS);
    }

    public void enablePassThroughWalls() {
        passThroughWalls = true;
        scheduler.schedule(() -> passThroughWalls = false, 5, TimeUnit.SECONDS);
    }

    public void activateSpeedBoost(int boostAmount, int durationSeconds) {
        this.speed += boostAmount;
        scheduler.schedule(() -> this.speed = normalSpeed, durationSeconds, TimeUnit.SECONDS);
    }

    @Override
    public void move(Board board, List<Enemy> enemies) {
        if (pendingDirection != -1) {
            int dx = 0, dy = 0;
            switch (pendingDirection) {
                case RIGHT: dx = 1; break;
                case LEFT: dx = -1; break;
                case DOWN: dy = 1; break;
                case UP: dy = -1; break;
            }
            int newX = x + dx;
            int newY = y + dy;

            if (newX >= 0 && newX < board.getWidth() && newY >= 0 && newY < board.getHeight() &&
                    (!board.isWall(newX, newY) || passThroughWalls)) {
                direction = pendingDirection;
                pendingDirection = -1;
            }
        }

        int dx = 0, dy = 0;
        switch (direction) {
            case RIGHT: dx = 1; break;
            case LEFT: dx = -1; break;
            case DOWN: dy = 1; break;
            case UP: dy = -1; break;
        }
        move(dx, dy, board, enemies);
    }

    public void move(int dx, int dy, Board board, List<Enemy> enemies) {
        int newX = x + dx;
        int newY = y + dy;

        if (newX >= 0 && newX < board.getWidth() && newY >= 0 && newY < board.getHeight() &&
                (!board.isWall(newX, newY) || passThroughWalls)) {
            x = newX;
            y = newY;

            if (board.hasPellet(x, y)) {
                board.removePellet(x, y);
                score += doublePointsActive ? 2 : 1;
            }

            for (Upgrade upgrade : board.getUpgrades()) {
                if (upgrade.getX() == x && upgrade.getY() == y) {
                    upgrade.applyEffect(this, enemies);
                    board.removeUpgrade(upgrade);
                    break;
                }
            }
        }
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}

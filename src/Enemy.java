import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Enemy extends Character {
    private Random random;
    private int lastDirection;
    private int originalSpeed;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // Constants for directions
    public static final int RIGHT = 0;
    public static final int LEFT = 1;
    public static final int DOWN = 2;
    public static final int UP = 3;

    public Enemy(int initialX, int initialY, int initialSpeed) {
        super(initialX, initialY, initialSpeed);
        this.random = new Random();
        this.lastDirection = -1; // No direction initially
        this.originalSpeed = initialSpeed;
    }

    @Override
    public void move(Board board, List<Enemy> enemies) {
        int dx = 0, dy = 0;
        boolean moved = false;

        while (!moved) {
            int direction = random.nextInt(4);

            // Ensure the enemy doesn't move back to the previous position
            if (direction == getOppositeDirection(lastDirection)) {
                continue;
            }

            switch (direction) {
                case RIGHT: dx = 1; dy = 0; break;
                case LEFT: dx = -1; dy = 0; break;
                case DOWN: dx = 0; dy = 1; break;
                case UP: dx = 0; dy = -1; break;
            }

            int newX = this.x + dx;
            int newY = this.y + dy;

            if (newX >= 0 && newY >= 0 && newX < board.getWidth() && newY < board.getHeight() && !board.isWall(newX, newY)) {
                this.x = newX;
                this.y = newY;
                moved = true;
                lastDirection = direction;
            }
        }
    }

    private int getOppositeDirection(int direction) {
        switch (direction) {
            case RIGHT: return LEFT;
            case LEFT: return RIGHT;
            case DOWN: return UP;
            case UP: return DOWN;
            default: return -1; // No direction
        }
    }

    public boolean checkCollision(Player player) {
        return this.x == player.getX() && this.y == player.getY();
    }

    public void tryToSpawnUpgrade(Board board) {
        if (random.nextInt(100) < 25) { // 25% chance
            String[] upgradeTypes = { Upgrade.SPEED_BOOST, Upgrade.EXTRA_LIFE, Upgrade.DOUBLE_POINTS, Upgrade.PASS_THROUGH_WALLS, Upgrade.SLOW_ENEMIES, Upgrade.STOP_ENEMIES };
            String type = upgradeTypes[random.nextInt(upgradeTypes.length)];
            board.addUpgrade(new Upgrade(this.x, this.y, type));
        }
    }

    public void speedBoost(int durationSeconds, int boostAmount) {
        this.speed += boostAmount;
        scheduler.schedule(() -> this.speed = originalSpeed, durationSeconds, TimeUnit.SECONDS);
    }
}

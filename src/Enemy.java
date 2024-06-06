import java.util.Random;

public class Enemy {
    private int x, y;
    private int speed;
    private Random random;
    private int lastDirection;

    // Constants for directions
    private final int RIGHT = 0;
    private final int LEFT = 1;
    private final int DOWN = 2;
    private final int UP = 3;

    public Enemy(int initialX, int initialY, int initialSpeed) {
        this.x = initialX;
        this.y = initialY;
        this.speed = initialSpeed;
        this.random = new Random();
        this.lastDirection = -1; // No direction initially
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void move(Board board) {
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

            int newX = this.x + dx * speed;
            int newY = this.y + dy * speed;

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
}

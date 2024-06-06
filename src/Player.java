public class Player {
    private int x, y;
    private int speed;
    private int score;
    private int direction;
    private int lives;

    // Constants for directions
    public static final int RIGHT = 0;
    public static final int LEFT = 1;
    public static final int DOWN = 2;
    public static final int UP = 3;

    public Player(int initialX, int initialY, int initialSpeed, int lives) {
        this.x = initialX;
        this.y = initialY;
        this.speed = initialSpeed;
        this.score = 0;
        this.direction = RIGHT; // Initial direction facing right
        this.lives = lives;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSpeed() {
        return speed;
    }

    public int getScore() {
        return score;
    }

    public int getDirection() {
        return direction;
    }

    public int getLives() {
        return lives;
    }

    public void loseLife() {
        if (lives > 0) {
            lives--;
        }
    }

    public void move(int dx, int dy, Board board) {
        int newX = x + dx * speed;
        int newY = y + dy * speed;

        if (newX >= 0 && newX < board.getWidth() && newY >= 0 && newY < board.getHeight() && !board.isWall(newX, newY)) {
            x = newX;
            y = newY;

            // Update direction
            if (dx == 1) direction = RIGHT;
            if (dx == -1) direction = LEFT;
            if (dy == 1) direction = DOWN;
            if (dy == -1) direction = UP;

            if (board.hasPellet(x, y)) {
                board.removePellet(x, y);
                score++;
            }
        }
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}

package model;

import javax.swing.JLabel;
import java.util.List;

public class Player extends Character {
    private int score;
    private int direction;
    private int lives;
    private boolean doublePointsActive;
    private boolean passThroughWalls;
    private float normalSpeed;
    private int pendingDirection;
    private Thread speedBoostThread;
    private JLabel playerLabel;

    // Constants for directions
    public static final int RIGHT = 0;
    public static final int LEFT = 1;
    public static final int DOWN = 2;
    public static final int UP = 3;

    public Player(int initialX, int initialY, float initialSpeed, int lives) {
        super(initialX, initialY, initialSpeed);
        this.score = 0;
        this.direction = RIGHT; // Initial direction facing right
        this.lives = lives;
        this.doublePointsActive = false;
        this.passThroughWalls = false;
        this.normalSpeed = initialSpeed;
        this.pendingDirection = -1;
        this.playerLabel = new JLabel(); // Initialize JLabel
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
        new Thread(() -> {
            try {
                Thread.sleep(5000); // Active for 5 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            doublePointsActive = false;
        }).start();
    }

    public void enablePassThroughWalls() {
        passThroughWalls = true;
        new Thread(() -> {
            try {
                Thread.sleep(5000); // Active for 5 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            passThroughWalls = false;
        }).start();
    }

    public void activateSpeedBoost(float boostAmount, int durationSeconds) {
        this.speed += boostAmount;
        if (speedBoostThread != null && speedBoostThread.isAlive()) {
            speedBoostThread.interrupt();
        }
        speedBoostThread = new Thread(() -> {
            try {
                Thread.sleep(durationSeconds * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.speed = normalSpeed;
        });
        speedBoostThread.start();
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

    public void resetSpeed() {
        this.speed = this.normalSpeed;
    }

    public void move(int dx, int dy, Board board, List<Enemy> enemies) {
        int newX = x + (int)(dx * speed);
        int newY = y + (int)(dy * speed);

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
}

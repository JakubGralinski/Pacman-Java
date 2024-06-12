import java.util.List;

public abstract class Character {
    protected int x, y;
    protected int speed;

    public Character(int initialX, int initialY, int initialSpeed) {
        this.x = initialX;
        this.y = initialY;
        this.speed = initialSpeed;
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

    public void setSpeed(int speed) {
        this.speed = speed;
    }


    public abstract void move(Board board, List<Enemy> enemies);
}

package model;

import java.util.List;

public abstract class Character {
    protected int x, y;
    protected float speed;

    public Character(int initialX, int initialY, float initialSpeed) {
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

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public abstract void move(Board board, List<Enemy> enemies);
}

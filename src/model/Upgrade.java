package model;

import java.util.List;

public class Upgrade {
    public static final String SPEED_BOOST = "speed_boost";
    public static final String EXTRA_LIFE = "extra_life";
    public static final String DOUBLE_POINTS = "double_points";
    public static final String PASS_THROUGH_WALLS = "pass_through_walls";
    public static final String SLOW_ENEMIES = "slow_enemies";
    public static final String FAST_ENEMIES = "fast_enemies";

    private int x, y;
    private String type;

    public Upgrade(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getType() {
        return type;
    }

    public void applyEffect(Player player, List<Enemy> enemies) {
        switch (type) {
            case SPEED_BOOST:
                player.activateSpeedBoost(1.5f, 5);
                break;
            case EXTRA_LIFE:
                player.gainLife();
                break;
            case DOUBLE_POINTS:
                player.activateDoublePoints();
                break;
            case PASS_THROUGH_WALLS:
                player.enablePassThroughWalls();
                break;
            case SLOW_ENEMIES:
                for (Enemy enemy : enemies) {
                    enemy.speedBoost(2, -1f);
                }
                break;
            case FAST_ENEMIES:
                for (Enemy enemy : enemies) {
                    enemy.speedBoost(2, -1f);
                }
                break;
        }
    }
}

import java.util.List;

public class Upgrade {
    public static final String SPEED_BOOST = "speed";
    public static final String EXTRA_LIFE = "life";
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
                player.activateSpeedBoost(1.0f, 2); // Increase speed by 1 for 5 seconds
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
                    enemy.speedBoost(2, -1f); // Decrease speed by 0.5 for 5 seconds
                }
                break;
            case FAST_ENEMIES:
                for (Enemy enemy : enemies) {
                    enemy.speedBoost(2, 1f); // Increase speed by 0.5 for 5 seconds
                }
                break;
        }
    }
}

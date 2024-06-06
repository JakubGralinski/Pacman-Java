public class Upgrade {
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

    public void applyEffect(Player player) {
        if (type.equals("speed")) {
            player.setSpeed(player.getSpeed() + 1);
        }
    }
}

package byow.Core;

public class Room {
    private int x;
    private int y;
    private int centerX;
    private int centerY;
    private int width;
    private int height;

    public Room(int x, int y,
                int width, int height) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        centerX = x + width / 2;
        centerY = y + height / 2;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }
}

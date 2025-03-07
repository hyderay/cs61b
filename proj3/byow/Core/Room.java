package byow.Core;

public class Room {
    private int x;
    private int y;
    private int centerX;
    private int centerY;
    private int width;
    private int height;
    private int sx;
    private int sy;

    public Room(int x, int y,
                int width, int height) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        centerX = x + width / 2;
        centerY = y + height / 2;
    }

    public Room(int sx, int sy,
                int x, int y,
                int width, int height) {
        this.sx = sx;
        this.sy = sy;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSx() {
        return sx;
    }

    public int getSy() {
        return sy;
    }
}

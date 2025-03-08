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
    private boolean hasSwitch = false;
    private boolean lightOn = false;

    public Room(int x, int y,
                int width, int height) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        centerX = x + width / 2;
        centerY = y + height / 2;
    }

    public void setHasSwitch(boolean hasSwitch) {
        this.hasSwitch = hasSwitch;
    }

    public boolean hasSwitch() {
        return hasSwitch;
    }

    public boolean isLightOn() {
        return lightOn;
    }

    public void setLight(boolean status) {
        this.lightOn = status;
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

    public void setSx(int sx) {
        this.sx = sx;
    }

    public void setSy(int sy) {
        this.sy = sy;
    }
}

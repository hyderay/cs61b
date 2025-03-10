package byow.Core;

import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class HUD {
    public static void drawHUD(TETile[][] world, double mouseX, double mouseY) {
        int tileX = (int) mouseX;
        int tileY = (int) mouseY;

        String description = "Nothing here";
        if (tileX >= 0 && tileX < Engine.WIDTH
                && tileY >= 0 && tileY < Engine.HEIGHT) {
            description = world[tileX][tileY].description();
        }

        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.PLAIN, 18));
        StdDraw.textLeft(1, Engine.HEIGHT + 1, description);

        String slotLabel = "Slot:" + Engine.getCurrentSlot();
        StdDraw.text(Engine.WIDTH - 3, Engine.HEIGHT + 1, slotLabel);

        //Adjust the size of "#".
        StdDraw.setFont(new Font("Monaco", Font.PLAIN, 14));
    }
}

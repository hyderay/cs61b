package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class HUD {
    public static void displayHUD(TETile[][] world, TERenderer ter) {
        ter.initialize(Engine.WIDTH, Engine.HEIGHT + 2, 0, 0);
        while (true) {
            ter.renderFrame(world);

            double mouseX = StdDraw.mouseX();
            double mouseY = StdDraw.mouseY();
            int tileX = (int) mouseX;
            int tileY = (int) mouseY;

            String tileDescription = "Nothing here";
            if (tileX >= 0 && tileX < Engine.WIDTH
                    && tileY >= 0 && tileY < Engine.HEIGHT) {
                TETile hoveredTile = world[tileX][tileY];
                tileDescription = hoveredTile.description();
            }

            StdDraw.setPenColor(Color.WHITE);
            StdDraw.setFont(new Font("Monaco", Font.PLAIN, 14));
            StdDraw.textLeft(1, Engine.HEIGHT + 1, tileDescription);

            StdDraw.show();
            StdDraw.pause(50);
        }
    }
}

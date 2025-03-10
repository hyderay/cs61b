package byow.Core;

import byow.TileEngine.TETile;
import java.awt.Color;

public class LightControl {

    /** Toggle the light switch at position (sx, sy) in a given room and apply shading. */
    public static void toggleSwitch(Room room, TETile[][] world) {
        room.setLight(!room.isLightOn());
        applyLight(room, world);
    }

    /** Apply shading effect within a room based on distance from the switch. */
    public static void applyLight(Room room, TETile[][] world) {
        int sx = room.getSx();
        int sy = room.getSy();

        int minX = room.getX() + 1;
        int maxX = room.getX() + room.getWidth() - 2;
        int minY = room.getY() + 1;
        int maxY = room.getY() + room.getHeight() - 2;

        int maxLightDistance = Math.max(room.getWidth(), room.getHeight());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                int distance = Math.abs(x - sx) + Math.abs(y - sy);
                if (room.isLightOn()) {
                    world[x][y] = applyBlueLighting(world[x][y], distance, maxLightDistance);
                } else {
                    world[x][y] = resetTileColor(world[x][y]);
                }
            }
        }
    }

    /** Properly apply blue shading based on distance, preserving original look. */
    private static TETile applyBlueLighting(TETile tile, int distance, int maxLightDistance) {
        float shadingFactor = Math.max(0, 1.0f - (distance / (float) maxLightDistance));

        int blueIntensity = (int) (200 * shadingFactor) + 50;
        Color shadedColor = new Color(0, 0, blueIntensity);

        // Keep original character and text color
        return new TETile(tile.character(), tile.getTextColor(), shadedColor, tile.description());
    }

    /** Reset tile shading to original black state when lights off. */
    private static TETile resetTileColor(TETile tile) {
        Color originalColor = Color.BLACK;
        return new TETile(tile.character(), tile.getTextColor(), originalColor, tile.description());
    }
}

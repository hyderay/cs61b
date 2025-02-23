package byow.lab12;

import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class TestAddHexagon {
    @Test
    public void testAddHexagon() {
        int s = 3;

        int worldWidth = 30;
        int worldheight = 30;
        TETile[][] world = new TETile[worldWidth][worldheight];

        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldheight; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        int startX = 5;
        int startY = 10;
        TETile tile = Tileset.FLOWER;

        HexWorld.addHexagon(world, startX, startY, s, tile);

        for (int x = startX + 2; x < startX + 2 + 3; x++) {
            assertEquals("Row 0, col " + x + " should be hex tile", tile, world[x][startY]);
        }

        for (int x = startX + 1; x < startX + 1 + 5; x++) {
            assertEquals("Row 1, col " + x + " should be hex tile", tile, world[x][startY + 1]);
        }

        for (int x = startX; x < startX + 7; x++) {
            assertEquals("Row 2, col " + x + " should be hex tile", tile, world[x][startY + 2]);
        }

        for (int x = startX + 1; x < startX + 1 + 5; x++) {
            assertEquals("Row 4, col " + x + " should be hex tile", tile, world[x][startY + 4]);
        }

        for (int x = startX + 2; x < startX + 2 + 3; x++) {
            assertEquals("Row 5, col " + x + " should be hex tile", tile, world[x][startY + 5]);
        }
    }
}

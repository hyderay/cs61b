package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    private static final int S = 3;
    private static final long SEED = 287312;
    private static final Random RANDOM = new Random(SEED);

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        int[] hexesInColumn = {3, 4, 5, 4, 3};
        int[] startY = {6, 3, 0, 3, 6};

        for (int col = 0; col < 5; col++) {
            int startX = col * (2 * S - 1);
            addHexColumn(world, startX, startY[col],
                    hexesInColumn[col], S);
        }

        ter.renderFrame(world);
     }

    public static void addHexagon(TETile[][] world, int startX, int startY, int s,
                                  TETile tile) {
        int maxWidth = s + 2 * (s - 1);
        for (int row = 0; row < s; row++) {
            int rowWidth = s + 2 * row;
            int offset = (maxWidth - rowWidth) / 2;
            addRow(world, startX + offset, startY + row,
                    rowWidth, tile);
        }

        for (int row = s; row < 2 * s; row++) {
            int rowWidth = maxWidth - (row - s) * 2;
            int offset = (maxWidth - rowWidth) / 2;
            addRow(world, startX + offset, startY + row,
                    rowWidth, tile);
        }
    }

    private static void addRow(TETile[][] world, int startX, int y, int numTiles,
                               TETile tile) {
        for (int i = 0; i < numTiles; i++) {
            world[startX + i][y] = tile;
        }
    }

    private static void addHexColumn(TETile[][] world, int startX, int startY,
                                     int numHex, int s) {
        for (int i = 0; i < numHex; i++) {
            int hexY = startY + i * (2 * s);
            TETile tile = randomTile();
            addHexagon(world, startX, hexY, s, tile);
        }
    }

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.GRASS;
            case 3: return Tileset.TREE;
            default: return Tileset.MOUNTAIN;
        }
    }
}

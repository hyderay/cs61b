package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class StudentTests {

    public static void main(String[] args) {
        int worldWidth = 80;
        int worldHeight = 30;
        TETile[][] world = new TETile[worldWidth][worldHeight];

        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        TERenderer ter = new TERenderer();
        ter.initialize(worldWidth, worldHeight);

        testPlaceRoom(world);

        ter.renderFrame(world);
    }

    public static void testPlaceRoom(TETile[][] world) {
        Random random = new Random(1234);
        int numRooms = 8;
        int minRoomSize = 5;
        int maxRoomSize = 10;

        PlaceRooms.placeRooms(world, random, numRooms,
                minRoomSize, maxRoomSize);
    }
}

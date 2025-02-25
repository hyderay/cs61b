package byow.Core.Test;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.Core.PlaceRooms;

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
        int numRooms = 15;
        int minRoomSize = 5;
        int maxRoomSize = 15;

        PlaceRooms.placeRooms(world, random, numRooms,
                minRoomSize, maxRoomSize);
    }
}

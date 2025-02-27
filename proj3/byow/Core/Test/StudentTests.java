package byow.Core.Test;

import byow.Core.ConnectRooms;
import byow.Core.Room;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.Core.PlaceRooms;

import java.util.List;
import java.util.Random;

public class StudentTests {

    public static void main(String[] args) {
        int worldWidth = 80;
        int worldHeight = 30;
        int seed = (int) (Math.random() * 100000);
        Random random = new Random(seed);
        TETile[][] world = new TETile[worldWidth][worldHeight];

        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        TERenderer ter = new TERenderer();
        ter.initialize(worldWidth, worldHeight);

        /**
         * Tests start here.
         * */
        List<Room> rooms = testPlaceRoom(world, random);
        testConnectRoom(world, rooms);

        ter.renderFrame(world);
    }

    public static List<Room> testPlaceRoom(TETile[][] world, Random random) {
        int numRooms = 16;
        int minRoomSize = 4;
        int maxRoomSize = 14;

        List<Room> rooms = PlaceRooms.placeRooms(world, random, numRooms,
                minRoomSize, maxRoomSize);
        return rooms;
    }

    public static void testConnectRoom(TETile[][] world,
                                       List<Room> rooms) {
        ConnectRooms.connectRooms(world, rooms);
    }
}

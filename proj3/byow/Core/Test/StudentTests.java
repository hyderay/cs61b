package byow.Core.Test;

import byow.Core.*;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.util.List;
import java.util.Random;

public class StudentTests {
    private static int worldWidth = 80;
    private static int worldHeight = 30;
    private static int seed = (int) (Math.random() * 100000);
    private static Random random = new Random(seed);
    private static TETile[][] world = new TETile[worldWidth][worldHeight];
    private static int playerX, playerY;
    private static TETile record;

    public static void main(String[] args) {
        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        TERenderer ter = new TERenderer();
        /**
         * Tests start here.
         * */
        List<Room> rooms = testPlaceRoom();
        testConnectRoom(rooms);
        testAvatar(rooms);

        ter.initialize(worldWidth, worldHeight + 2);
        testHUD(ter);
    }

    public static List<Room> testPlaceRoom() {
        int numRooms = 16;
        int minRoomSize = 4;
        int maxRoomSize = 14;

        List<Room> rooms = PlaceRooms.placeRooms(world, random, numRooms,
                minRoomSize, maxRoomSize);
        return rooms;
    }

    public static void testConnectRoom(List<Room> rooms) {
        ConnectRooms.connectRooms(world, rooms);
    }

    public static void testHUD(TERenderer ter) {
        while (true) {
            ter.renderFrame(world);

            double mouseX = StdDraw.mouseX();
            double mouseY = StdDraw.mouseY();
            HUD.drawHUD(world, mouseX, mouseY);

            StdDraw.show();
            StdDraw.pause(100);
        }
    }

    public static void testAvatar(List<Room> rooms) {
        int randRoom = random.nextInt(rooms.size());
        Room startRoom = rooms.get(randRoom);
        playerX = random.nextInt(startRoom.getWidth() - 2) + startRoom.getX() + 1;
        playerY = random.nextInt(startRoom.getHeight() - 2) + startRoom.getY() + 1;
        record = world[playerX][playerY];
        world[playerX][playerY] = Tileset.AVATAR;
    }
}

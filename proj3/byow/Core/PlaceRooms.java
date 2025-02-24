package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class PlaceRooms {

    /**
     * @param world
     * @param random
     * @param numRooms
     * @param minRoomSize
     * @param maxRoomSize
     * */
    public static void placeRooms(TETile[][] world,
                                  Random random,
                                  int numRooms,
                                  int minRoomSize,
                                  int maxRoomSize) {
        int worldWidth = world.length;
        int worldHeight = world[0].length;

        int placed = 0;
        int attempts = 0;
        int maxAttempts = numRooms * 10;

        while (placed < numRooms && attempts < maxAttempts) {
            attempts++;

            int roomWidth = randomInt(random, minRoomSize, maxRoomSize);
            int roomHeight = randomInt(random, minRoomSize, maxRoomSize);

            int x = random.nextInt(worldWidth - roomWidth);
            int y = random.nextInt(worldHeight - roomHeight);

            if (canPlaceRoom(world, x, y, roomWidth, roomHeight)) {
                placeSingleRoom(world, x, y, roomWidth, roomHeight);
                placed++;
            }
        }
    }

    /**
     * Check if the generated rectangle doesn't overlap with others.
     *
     * @param world
     * @param x         The bottom-left x coordinate of the rectangle.
     * @param y         The bottom-left y coordinate of the rectangle.
     * @param width
     * @param height
     * @return          True if there's no overlap, else false.
     * */
    private static boolean canPlaceRoom(TETile[][] world,
                                        int x, int y,
                                        int width, int height) {
        for (int i = x; i < width + x; i++) {
            for (int j = y; j < height + y; j++) {
                if (world[i][j] == null || world[i][j] != Tileset.NOTHING) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Helper method to generate a random integer in [min, max].
     * */
    private static int randomInt(Random random, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Min should be less than max.");
        }
        return min + random.nextInt(max - min + 1);
    }

    /**
     * Place rooms.
     *
     * @param world
     * @param x
     * @param y
     * @param width
     * @param height
     * */
    private static void placeSingleRoom(TETile[][] world,
                                   int x, int y,
                                   int width, int height) {
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                world[i][j] = Tileset.FLOOR;
            }
        }

        for (int i = x; i < x + width; i++) {
            world[i][y] = Tileset.WALL;
            world[i][y + height - 1] = Tileset.WALL;
        }

        for (int j = y; j < y + height; j++) {
            world[x][j] = Tileset.WALL;
            world[x + width - 1][j] = Tileset.WALL;
        }
    }
}

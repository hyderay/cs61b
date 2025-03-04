package byow.Core.Test;

import byow.Core.*;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.util.List;
import java.util.Random;

public class StudentTest2 {
    static TERenderer ter = new TERenderer();
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private static TETile[][] world;
    private static int playerX, playerY;  // Avatar’s position.
    private long currentSeed;      // The RNG seed for consistent replays.
    private static TETile record;   //Record the element before avatar comes.

    // Use these to handle the “:” command logic when reading character by character.
    private static boolean colonPressed = false;

    public static void main(String[] args) {
        MainMenu.MenuResults menuResult = MainMenu.displayMenu();
        switch (menuResult.action) {
            case NEW:
                long seed = menuResult.seed;  // the typed-in seed
                world = generateNewWorld(seed);
                break;
            case LOAD:
                // Your save/load logic in SaveAndLoad
                world = SaveAndLoad.loadWorld();
                if (world == null) {
                    throw new IllegalArgumentException("No saved worlds");
                }
                break;
            case QUIT:
                System.exit(0);
                break;
            default:
                break;
        }

        ter.initialize(WIDTH, HEIGHT + 2);

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                handleInput(c);
            }

            ter.renderFrame(world);

            double mouseX = StdDraw.mouseX();
            double mouseY = StdDraw.mouseY();
            HUD.drawHUD(world, mouseX, mouseY);

            StdDraw.show();
            StdDraw.pause(100);
        }
    }

    private static TETile[][] generateNewWorld(long seed) {
        Random random = new Random(seed);
        TETile[][] world = new TETile[WIDTH][HEIGHT];

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        int numRooms = 16;
        int minRoomSize = 4;
        int maxRoomSize = 14;

        List<Room> rooms = PlaceRooms.placeRooms(world, random, numRooms,
                minRoomSize, maxRoomSize);
        ConnectRooms.connectRooms(world, rooms);

        int randRoom = random.nextInt(rooms.size());
        Room startRoom = rooms.get(randRoom);
        playerX = random.nextInt(startRoom.getWidth() - 2) + 1 + startRoom.getX();
        playerY = random.nextInt(startRoom.getHeight() - 2) + 1 + startRoom.getY();
        record = world[playerX][playerY];
        world[playerX][playerY] = Tileset.AVATAR;

        return world;
    }

    private static void moveAvatar(int dx, int dy) {
        int newX = playerX + dx;
        int newY = playerY + dy;

        if (!world[newX][newY].equals(Tileset.WALL)) {
            world[playerX][playerY] = record;
            record = world[newX][newY];
            playerX = newX;
            playerY = newY;
            world[playerX][playerY] = Tileset.AVATAR;
        }
    }

    private static void handleInput(char c) {
        c = Character.toLowerCase(c);
        if (colonPressed) {
            if (c == 'q') {
                SaveAndLoad.saveWorld();
                System.exit(0);
            }
            colonPressed = false;
        } else {
            switch (c) {
                case 'w':
                    moveAvatar(0, 1);
                    break;
                case 's':
                    moveAvatar(0, -1);
                    break;
                case 'a':
                    moveAvatar(-1, 0);
                    break;
                case 'd':
                    moveAvatar(1, 0);
                    break;
                case ':':
                    colonPressed = true;
                    break;
                default:
                    break;
            }
        }
    }
}

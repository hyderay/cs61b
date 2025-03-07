package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private static TETile[][] world;
    private static int playerX, playerY;  // Avatar’s position.
    private static TETile record;   //Record the element before avatar comes.

    // Use these to handle the “:” command logic when reading character by character.
    private static boolean colonPressed = false;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        MainMenu.MenuResults menuResult = MainMenu.displayMenu();
        switch (menuResult.getAction()) {
            case NEW:
                long seed = menuResult.getSeed();  // the typed-in seed
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

        displayWorld(ter);
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        input = input.toLowerCase();

        int i = 0;
        while (i < input.length()) {
            char c = input.charAt(i);

            if (c == 'n') {
                int j = i + 1;
                while (j < input.length() && input.charAt(j) != 's') {
                    j++;
                }
                String seedS = input.substring(i + 1, j);
                long seed = Long.parseLong(seedS);
                world = generateNewWorld(seed);
                i = j + 1;
                break;
            } else {
                i++;
            }
        }

        while (i < input.length()) {
            char c = input.charAt(i);
            i++;
            handleInput(c);
        }

        //displayWorld(ter);
        return world;
    }

    /** Generate a world based on seed. */
    private static TETile[][] generateNewWorld(long seed) {
        Random random = new Random(seed);
        world = new TETile[WIDTH][HEIGHT];

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

        int numSwitches = 5 + random.nextInt(6);
        Collections.shuffle(rooms, random);
        List<Room> switchList = new ArrayList<>();

        for (int i = 0; i < numSwitches && i < rooms.size(); i++) {
            Room r = rooms.get(i);
            int sx, sy;
            do {
                sx = r.getX() + 1 + random.nextInt(r.getWidth() - 2);
                sy = r.getY() + 1 + random.nextInt(r.getHeight() - 2);
            } while (sx == playerX && sy == playerY);

            world[sx][sy] = Tileset.LIGHT_SWITCH;
            switchList.add(new Room(sx, sy, r.getX(), r.getY(),
                    r.getWidth(), r.getHeight()));
        }

        return world;
    }

    /** Move the avatar dx in width and dy in height. */
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

    public static void handleInput(char c) {
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
                case 'v':
                    Vision.switchVision();
                    break;
                default:
                    break;
            }
        }
    }

    public static void displayWorld(TERenderer ter) {
        ter.initialize(WIDTH, HEIGHT + 2);

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                String fullInput = SaveAndLoad.getFullInput();
                fullInput += c;
                SaveAndLoad.setFullInput(fullInput);
                handleInput(c);
            }

            TETile[][] displayWorld;
            if (Vision.switchStatus()) {
                displayWorld = Vision.applyVision(world, playerX, playerY);
            } else {
                displayWorld = world;
            }

            ter.renderFrame(displayWorld);

            double mouseX = StdDraw.mouseX();
            double mouseY = StdDraw.mouseY();
            HUD.drawHUD(displayWorld, mouseX, mouseY);

            StdDraw.show();
            StdDraw.pause(100);
        }
    }
}

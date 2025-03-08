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
    private static String fullInput = "";  // This variable is updated as moves are processed.

    // Use these to handle the “:” command logic when reading character by character.
    private static boolean colonPressed = false;
    private static boolean isStringInput = false;
    private static List<Room> rooms;


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
        input = input.toLowerCase();
        isStringInput = true;

        if (input.charAt(0) == 'n') {
            int sIndex = input.indexOf('s');
            if (sIndex == -1) {
                throw new IllegalArgumentException("Input must contain an 's' after the seed.");
            }
            String seedS = input.substring(1, sIndex);
            long seed = Long.parseLong(seedS);
            world = generateNewWorld(seed);
            fullInput = input.substring(0, sIndex + 1);
            input = input.substring(sIndex + 1);
        } else if (input.charAt(0) == 'l') {
            world = SaveAndLoad.loadWorld();
            input = input.substring(1);
        }

        for (char c : input.toCharArray()) {
            handleInput(c);
        }

        isStringInput = false;
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
        int minRoomSize = 5;
        int maxRoomSize = 14;

        rooms = PlaceRooms.placeRooms(world, random, numRooms,
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

        for (int i = 0; i < numSwitches && i < rooms.size(); i++) {
            Room r = rooms.get(i);
            int sx, sy;
            do {
                sx = r.getX() + 1 + random.nextInt(r.getWidth() - 2);
                sy = r.getY() + 1 + random.nextInt(r.getHeight() - 2);
            } while (sx == playerX && sy == playerY);

            r.setHasSwitch(true);
            r.setLight(false);
            r.setSx(sx);
            r.setSy(sy);
            world[sx][sy] = Tileset.LIGHT_SWITCH;
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
        // If a colon was previously pressed, decide based on the new character.
        if (colonPressed) {
            colonPressed = false; // Reset the flag immediately.
            if (c == 'q') {
                // Quit command: do not add ':' or 'q' to fullInput.
                SaveAndLoad.saveWorld();
                if (isStringInput) {
                    return;
                }
                System.exit(0);
            } else {
                // Not quitting: append the colon and current char.
                fullInput = fullInput + ":" + c;
                executeCommand(c);
                return;
            }
        }

        // If no colon was pending.
        if (c == ':') {
            // Wait to see if a 'q' follows.
            colonPressed = true;
        } else {
            fullInput += c;
            executeCommand(c);
        }
    }

    private static void executeCommand(char c) {
        switch (c) {
            case 'w': moveAvatar(0, 1); break;
            case 's': moveAvatar(0, -1); break;
            case 'a': moveAvatar(-1, 0); break;
            case 'd': moveAvatar(1, 0); break;
            case 'v': Vision.switchVision(); break;
            case 't': toggleLightSwitch(); break;
            default: break;
        }
    }

    public static void displayWorld(TERenderer ter) {
        ter.initialize(WIDTH, HEIGHT + 2);

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                handleInput(c);
            }

            updateLightingEffects();

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

    public static TETile[][] getWorld() {
        return world;
    }

    public static String getFullInput() {
        return fullInput;
    }

    public static void setFullInput(String input) {
        fullInput = input;
    }

    private static void toggleLightSwitch() {
        for (Room r : rooms) {
            if (r.hasSwitch() && isAdjacentToSwitch(r, playerX, playerY)) {
                LightControl.toggleSwitch(r, world);
                break;
            }
        }
    }

    /** Helper method to check adjacency to the switch. */
    private static boolean isAdjacentToSwitch(Room room, int px, int py) {
        int sx = room.getSx();
        int sy = room.getSy();

        return Math.abs(playerX - sx) + Math.abs(playerY - sy) <= 1;
    }

    /** Re-applies lighting effects to rooms every frame. */
    private static void updateLightingEffects() {
        for (Room r : rooms) {
            if (r.hasSwitch()) {
                LightControl.applyLight(r, world);
            }
        }
    }
}

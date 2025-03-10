package byow.Core;

import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.util.List;

public class MainMenu {
    private static final int MENU_WIDTH = 40;
    private static final int MENU_HEIGHT = 50;

    public enum MenuActions {
        NEW, LOAD, QUIT, OTHER
    }

    public static class MenuResults {
        private final MenuActions action;
        private final long seed;
        public MenuResults(MenuActions action, long seed) {
            this.action = action;
            this.seed = seed;
        }

        public MenuActions getAction() {
            return action;
        }

        public long getSeed() {
            return seed;
        }
    }

    public static MenuResults displayMenu() {
        StdDraw.setCanvasSize(MENU_WIDTH * 16, MENU_HEIGHT * 16);
        StdDraw.setXscale(0, MENU_WIDTH);
        StdDraw.setYscale(0, MENU_HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        drawMenu();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toLowerCase(StdDraw.nextKeyTyped());
                switch (c) {
                    case 'n':
                        long seed = promptSeed();
                        return new MenuResults(MenuActions.NEW, seed);
                    case 'l':
                        return new MenuResults(MenuActions.LOAD, 0);
                    case 'q':
                        return new MenuResults(MenuActions.QUIT, 0);
                    case 'o':
                        handleOtherSlotsMenu();
                        drawMenu();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private static void drawMenu() {
        StdDraw.clear(Color.BLACK);

        Font titleFont = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(titleFont);
        StdDraw.setPenColor(Color.WHITE);

        StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.75, "CS61B: THE GAME");

        Font optionFont = new Font("Monaco", Font.PLAIN, 18);
        StdDraw.setFont(optionFont);
        StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.45, "New Game (N)");
        StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.40, "Load Game (L)");
        StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.35, "Quit (Q)");
        StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.30, "Other Slots (O)");

        StdDraw.show();
    }

    private static long promptSeed() {
        String seedString = "";
        while (true) {
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(Color.WHITE);
            Font promptFont = new Font("Monaco", Font.PLAIN, 18);
            StdDraw.setFont(promptFont);

            StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.60,
                    "Enter Seed, then press (S):");
            StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.50, seedString);
            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (Character.isDigit(c)) {
                    seedString += c;
                } else if (Character.toLowerCase(c) == 's') {
                    String fullInput = "n" + seedString + "s";
                    Engine.setFullInput(fullInput);
                    if (seedString.isEmpty()) {
                        return 0;  // default to 0
                    }
                    return Long.parseLong(seedString);
                }
            }
        }
    }

    private static void handleOtherSlotsMenu() {
        while (true) {
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.70, "Other Slots Menu");
            StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.60, "Save to a slot (S)");
            StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.55, "Load from a slot (L)");
            StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.50, "Back to main (B)");
            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toLowerCase(StdDraw.nextKeyTyped());
                switch (c) {
                    case 's':
                        int slot = promptSlot();
                        if (slot == -1) {
                            break;
                        }

                        long seed = promptSeedForSlot();
                        Engine.setCurrentSlot(slot);
                        TETile[][] world = Engine.generateNewWorld(seed);
                        Engine.setFullInput("N" + seed + "S");
                        Engine.displayWorld(world);
                        return;
                    case 'l':
                        handleLoadSlots();
                        break;
                    case 'b':
                        return;
                    default:
                        break;
                }
            }
        }
    }

    private static void handleLoadSlots() {
        // Retrieve existing slots; for example, [1, 2, 4] if saveSlot1/2/4.txt exist
        List<Integer> existing = SaveAndLoad.getExistingSlots();
        if (existing.isEmpty()) {
            displayMessageAndWait("No saved slots found!", "Press any key to continue.");
            return;
        }

        while (true) {
            drawLoadSlotsMenu(existing);

            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                c = Character.toLowerCase(c);
                if (c == 'b') {
                    return; // go back to Other Slots menu
                }
                if (Character.isDigit(c)) {
                    int chosenSlot = c - '0'; // convert e.g. '3' -> 3
                    if (existing.contains(chosenSlot)) {
                        TETile[][] loaded = SaveAndLoad.loadWorldFromSlot(chosenSlot);
                        if (loaded == null) {
                            displayMessageAndWait("Load failed for slot " + chosenSlot,
                                    "Press any key to continue");
                        } else {
                            Engine.setCurrentSlot(chosenSlot);
                            Engine.displayWorld(Engine.getWorld());
                            return;
                        }
                    } else {
                        displayMessageAndWait("No saved world in slot " + chosenSlot,
                                "Press any key to continue.");
                    }
                }
            }
        }
    }

    private static int promptSlot() {
        String slotString = "";
        List<Integer> existing = SaveAndLoad.getExistingSlots();
        while (true) {
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.70,
                    "Enter a slot number [1-5], then press (S). To go back (B)");
            StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.65,
                    "To go back press (B)");
            StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.60, "Existing: " + existing);
            StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.55, slotString);
            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                c = Character.toLowerCase(c);
                if (Character.isDigit(c)) {
                    slotString += c;
                } else if (c == 'b') {
                    return -1;
                } else if (c == 's') {
                    if (slotString.isEmpty()) {
                        return 1; // default to slot 1 if no digit typed
                    }
                    return Integer.parseInt(slotString);
                }
            }
        }
    }

    /** Wait until the user presses any key, then return. */
    private static void waitForAnyKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                StdDraw.nextKeyTyped();
                return;
            }
        }
    }

    private static long promptSeedForSlot() {
        String seedString = "";
        while (true) {
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(Color.WHITE);
            Font promptFont = new Font("Monaco", Font.PLAIN, 18);
            StdDraw.setFont(promptFont);

            StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.60,
                    "Enter a seed for your new game, then press (S):");
            StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.50, seedString);
            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                c = Character.toLowerCase(c);
                if (Character.isDigit(c)) {
                    seedString += c;
                } else if (c == 's') {
                    // If no digits typed, default to 0
                    if (seedString.isEmpty()) {
                        seedString = "0";
                    }
                    long result = Long.parseLong(seedString);
                    return result;
                }
            }
        }
    }

    private static void displayMessageAndWait(String message, String subMessage) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.60, message);
        StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.50, subMessage);
        StdDraw.show();
        waitForAnyKey();
    }

    private static void drawLoadSlotsMenu(List<Integer> existing) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.70, "Load from a slot");
        StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.60, "Existing: " + existing.toString());
        StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.55,
                "Type one of these numbers, or press (B) to go back");
        StdDraw.show();
    }
}

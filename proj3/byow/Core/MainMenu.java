package byow.Core;

import byow.TileEngine.TERenderer;
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
                        long seed = promptSeedForSlot();
                        Engine engine = new Engine();
                        String newGameInput = "n" + seed + "s";
                        engine.interactWithInputString(newGameInput);
                        Engine.setCurrentSlot(slot);
                        SaveAndLoad.saveWorldToSlot(slot);
                        System.out.println("Created a new game with seed " + seed +
                                " and saved it to slot " + slot);
                        Engine.displayWorld(new byow.TileEngine.TERenderer());
                        return;
                    case 'l':
                        handleLoadSlots();
                        return;
                    case 'b':
                        return;
                    default:
                        break;
                }
            }
        }
    }


    private static int promptSlot() {
        String slotString = "";
        while (true) {
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.70,
                    "Enter a slot number [1-5], then press (S):");
            StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.60, slotString);
            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (Character.isDigit(c)) {
                    slotString += c;
                } else if (Character.toLowerCase(c) == 's') {
                    if (slotString.isEmpty()) {
                        return 1; // default to slot 1 if no digit typed
                    }
                    return Integer.parseInt(slotString);
                }
            }
        }
    }


    private static void handleLoadSlots() {
        // This returns something like [1, 2, 4] if saveSlot1/2/4.txt exist
        List<Integer> existing = SaveAndLoad.getExistingSlots();
        if (existing.isEmpty()) {
            // Show a quick message, then return
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.60, "No saved slots found!");
            StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.50, "Press any key to continue.");
            StdDraw.show();
            waitForAnyKey();
            return;
        }

        // If there are slots, let the user pick one from that list:
        // We’ll do a small loop with text.
        while (true) {
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.70, "Load from a slot");
            // Show existing slots, e.g. “Existing slots: [1, 2, 4]”
            StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.60,
                    "Existing: " + existing.toString());
            StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.55,
                    "Type one of these numbers [1..5], or press (B) to go back");
            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == 'b' || c == 'B') {
                    return; // go back to Other Slots menu
                }
                if (Character.isDigit(c)) {
                    int chosenSlot = c - '0'; // convert e.g. '3' -> 3
                    if (existing.contains(chosenSlot)) {
                        TETile[][] loaded = SaveAndLoad.loadWorldFromSlot(chosenSlot);
                        if (loaded == null) {
                            // Means the file was missing or had some error
                            StdDraw.clear(Color.BLACK);
                            StdDraw.setPenColor(Color.WHITE);
                            StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.60,
                                    "Load failed for slot " + chosenSlot);
                            StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.50,
                                    "Press any key to continue");
                            StdDraw.show();
                            waitForAnyKey();
                        } else {
                            Engine.setCurrentSlot(chosenSlot);
                            Engine.displayWorld(new byow.TileEngine.TERenderer());
                            return;
                        }
                    } else {
                        // pressed a digit that is not in existing
                        StdDraw.clear(Color.BLACK);
                        StdDraw.setPenColor(Color.WHITE);
                        StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.60,
                                "No saved world in slot " + chosenSlot);
                        StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.50,
                                "Press any key to continue.");
                        StdDraw.show();
                        waitForAnyKey();
                    }
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
                if (Character.isDigit(c)) {
                    seedString += c;
                } else if (Character.toLowerCase(c) == 's') {
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

}

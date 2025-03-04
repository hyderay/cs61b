package byow.Core;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class MainMenu {
    private static final int MENU_WIDTH = 40;
    private static final int MENU_HEIGHT = 50;

    public enum MenuActions {
        NEW, LOAD, QUIT
    }

    public static class MenuResults {
        public final MenuActions action;
        public final long seed;
        public MenuResults(MenuActions action, long seed) {
            this.action = action;
            this.seed = seed;
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
                    SaveAndLoad.fullInput = "n" + seedString + "s";
                    if (seedString.length() == 0) {
                        return 0;  // default to 0
                    }
                    return Long.parseLong(seedString);
                }
            }
        }
    }
}

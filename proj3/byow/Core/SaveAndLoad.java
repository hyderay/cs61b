package byow.Core;

import byow.TileEngine.TETile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class SaveAndLoad {
    // This variable should be updated by your Engine as moves are processed.
    public static String fullInput = "";
    /**
     * Saves the current game state to a file in the "byow/Core/SavedGames"
     * folder within the current working directory.
     * The full input string (including the seed and moves) is saved.
     */
    public static void saveWorld() {
        try {
            // Create the directory if it doesn't exist.
            File dir = new File("byow/Core/SavedGames");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // Create a file named "save.txt" in the "SavedGames" folder.
            File saveFile = new File(dir, "save.txt");
            PrintWriter writer = new PrintWriter(saveFile);
            writer.println(fullInput);
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error saving game state: " + e.getMessage());
        }
    }

    /**
     * Loads the saved game state from "SavedGames/save.txt" within the current working directory.
     * If no save file exists, the program exits gracefully.
     *
     * @return the 2D TETile[][] representing the saved world state.
     */
    public static TETile[][] loadWorld() {
        File dir = new File("byow/Core/SavedGames");
        File saveFile = new File(dir, "save.txt");
        if (!saveFile.exists()) {
            System.out.println("No saved game found. Exiting.");
            System.exit(0);
        }
        try {
            Scanner scanner = new Scanner(saveFile);
            if (scanner.hasNextLine()) {
                String loadedInput = scanner.nextLine();
                if (loadedInput.endsWith(":q")) {
                    // Chop off the trailing ":q"
                    loadedInput = loadedInput.substring(0, loadedInput.length() - 2);
                }

                // IMPORTANT: Update fullInput to the loaded string before reconstructing
                SaveAndLoad.fullInput = loadedInput;

                Engine engine = new Engine();
                TETile[][] world = engine.interactWithInputString(loadedInput);
                scanner.close();
                return world;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error loading game state: " + e.getMessage());
        }
        return null;
    }
}

package byow.Core;

import byow.TileEngine.TETile;

import java.io.*;
import java.util.Scanner;

public class SaveAndLoad {
    /**
     * Saves the current game state to a file in the "byow/Core/SavedGames"
     * folder within the current working directory.
     * The full input string (including the seed and moves) is saved.
     */
    public static void saveWorld() {
        try {
            // Attempt file I/O
            File dir = new File("byow/SavedGames");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File saveFile = new File(dir, "save.txt");
            FileWriter writer = new FileWriter(saveFile);
            writer.write(Engine.getFullInput());
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Loads the saved game state from "SavedGames/save.txt" within the current working directory.
     * If no save file exists, the program exits gracefully.
     *
     * @return the 2D TETile[][] representing the saved world state.
     */
    public static TETile[][] loadWorld() {
        File dir = new File("byow/SavedGames");
        File saveFile = new File(dir, "save.txt");

        if (!saveFile.exists()) {
            System.out.println("No saved game found. Exiting.");
            System.exit(0);
        }

        try (Scanner scanner = new Scanner(saveFile)) {
            if (scanner.hasNextLine()) {
                String loadedInput = scanner.nextLine();
                // Remove the quit command if present
                if (loadedInput.endsWith(":q")) {
                    loadedInput = loadedInput.substring(0, loadedInput.length() - 2);
                }
                Engine.setFullInput(loadedInput);
                Engine engine = new Engine();
                return engine.interactWithInputString(loadedInput);
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return null;
    }
}

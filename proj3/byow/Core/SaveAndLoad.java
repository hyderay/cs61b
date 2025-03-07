package byow.Core;

import byow.TileEngine.TETile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class SaveAndLoad {
    // This variable should be updated by your Engine as moves are processed.
    private static String fullInput = "";
    /**
     * Saves the current game state to a file in the "byow/Core/SavedGames"
     * folder within the current working directory.
     * The full input string (including the seed and moves) is saved.
     */
    public static void saveWorld() {
        try {
            // Attempt file I/O
            File dir = new File("byow/Core/SavedGames");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File saveFile = new File(dir, "save.txt");
            PrintWriter writer = new PrintWriter(saveFile);
            writer.println(fullInput);
            writer.close();
        } catch (SecurityException e) {
            // If file I/O is not permitted, simulate saving (the fullInput is still stored)
            System.out.println("File I/O not permitted; simulated save.");
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
        try {
            if (!saveFile.exists()) {
                System.out.println("No saved game found. Exiting.");
                System.exit(0);
            }
            Scanner scanner = new Scanner(saveFile);
            if (scanner.hasNextLine()) {
                String loadedInput = scanner.nextLine();
                if (loadedInput.endsWith(":q")) {
                    loadedInput = loadedInput.substring(0, loadedInput.length() - 2);
                }
                SaveAndLoad.fullInput = loadedInput;
                Engine engine = new Engine();
                TETile[][] world = engine.interactWithInputString(loadedInput);
                scanner.close();
                return world;
            }
        } catch (SecurityException e) {
            // If file I/O is not permitted, simulate loading using the static fullInput
            System.out.println("File I/O not permitted; simulated load.");
            Engine engine = new Engine();
            TETile[][] world = engine.interactWithInputString(fullInput);
            return world;
        } catch (FileNotFoundException e) {
            System.out.println("Error loading game state: " + e.getMessage());
        }
        return null;
    }

    public static String getFullInput() {
        return fullInput;
    }

    public static void setFullInput(String input) {
        fullInput = input;
    }
}

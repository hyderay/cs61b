package byow.Core;

import byow.TileEngine.TETile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SaveAndLoad {
    public static void saveWorldToSlot(int slot) {
        try {
            File saveFile = new File("saveSlot" + slot + ".txt");
            FileWriter writer = new FileWriter(saveFile);
            writer.write(Engine.getFullInput());
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred saving slot " + slot);
            e.printStackTrace();
        }
    }

    public static TETile[][] loadWorldFromSlot(int slot) {
        File saveFile = new File("saveSlot" + slot + ".txt");
        if (!saveFile.exists()) {
            System.out.println("No saved game found in slot " + slot);
            return null;
        }
        try (Scanner scanner = new Scanner(saveFile)) {
            if (scanner.hasNextLine()) {
                String loadedInput = scanner.nextLine();
                // Remove trailing :q if present
                if (loadedInput.endsWith(":q")) {
                    loadedInput = loadedInput.substring(0, loadedInput.length() - 2);
                }
                Engine.setFullInput(loadedInput);
                Engine engine = new Engine();
                return engine.interactWithInputString(loadedInput);
            }
        } catch (IOException e) {
            System.out.println("An error occurred loading slot " + slot);
            e.printStackTrace();
        }
        return null;
    }

    public static boolean slotExists(int slot) {
        File f = new File("saveSlot" + slot + ".txt");
        return f.exists();
    }

    /** Returns a list of all slot numbers (1..N) that currently have a saved file. */
    public static List<Integer> getExistingSlots() {
        List<Integer> result = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            if (slotExists(i)) {
                result.add(i);
            }
        }
        return result;
    }

}

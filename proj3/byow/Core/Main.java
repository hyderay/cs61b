package byow.Core;

import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

/** This is the main entry point for the program. This class simply parses
 *  the command line inputs, and lets the byow.Core.Engine class take over
 *  in either keyboard or input string mode.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length > 2) {
            System.out.println("Can only have two arguments - the flag and input string");
            System.exit(0);
        } else if (args.length == 2 && args[0].equals("-s")) {
            Engine engine = new Engine();
            TETile[][] world = engine.interactWithInputString(args[1]);
            System.out.println(engine.toString());

            engine.ter.initialize(Engine.WIDTH, Engine.HEIGHT + 2);
            while (true) {
                double mouseX = StdDraw.mouseX();
                double mouseY = StdDraw.mouseY();

                // Re-draw the world first:
                engine.ter.renderFrame(world);

                // Then draw the HUD text (mouse hover tile description):
                HUD.drawHUD(world, mouseX, mouseY);

                StdDraw.show();
                StdDraw.pause(100);
            }
        // DO NOT CHANGE THESE LINES YET ;)
        } else if (args.length == 2 && args[0].equals("-p")) { System.out.println("Coming soon."); } 
        // DO NOT CHANGE THESE LINES YET ;)
        else {
            Engine engine = new Engine();
            engine.interactWithKeyboard();
        }
    }
}

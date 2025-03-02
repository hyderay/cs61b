package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        String randString = "";
        for (int i = 0; i < n; i++) {
            int randomIndex = rand.nextInt(CHARACTERS.length);
            randString += CHARACTERS[randomIndex];
        }
        return randString;
    }

    public void drawFrame(String s) {
        StdDraw.clear(StdDraw.BLACK);
        Font font = new Font("Arial", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(width / 2.0, height / 2.0, s);
        if (!gameOver) {
            font = new Font("Arial", Font.PLAIN, 20);
            StdDraw.setFont(font);
            StdDraw.textLeft(1, height - 1, "Round: " + round);
            String task;
            if (playerTurn) {
                task = "Type!";
            } else {
                task = "Watch!";
            }
            StdDraw.text(width / 2.0, height - 1, task);
            String phrase = ENCOURAGEMENT[rand.nextInt(ENCOURAGEMENT.length)];
            StdDraw.textRight(width - 1, height - 1, phrase);

            StdDraw.setPenRadius(0.002);
            StdDraw.line(0, height - 2, width, height - 2);
            StdDraw.setPenRadius();
        }
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        for (int i = 0; i < letters.length(); i++) {
            drawFrame(letters.substring(i, i + 1));
            StdDraw.pause(1000);

            drawFrame("");
            StdDraw.pause(500);
        }
    }

    public String solicitNCharsInput(int n) {
        playerTurn = true;
        String input = "";
        drawFrame(input);
        while (input.length() < n) {
            if (StdDraw.hasNextKeyTyped()) {
                char display = StdDraw.nextKeyTyped();
                input += display;
                drawFrame(input);
            }
        }
        StdDraw.pause(1000);
        return input;
    }

    public void startGame() {
        round = 1;
        gameOver = false;

        while (!gameOver) {
            playerTurn = false;
            String mes = "Round: " + round;
            drawFrame(mes);
            String randString = generateRandomString(round);
            flashSequence(randString);

            String userInput = solicitNCharsInput(round);
            if (!userInput.equals(randString)) {
                gameOver = true;
                mes = "Game Over! You made it to round: " + round;
                drawFrame(mes);
            } else {
                round++;
            }
        }
    }
}

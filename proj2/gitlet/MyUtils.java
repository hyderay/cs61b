package gitlet;

public class MyUtils {
    public static void exit(String message) {
        System.out.println(message);
        System.exit(0);
    }

    public static void checkValidity(String[] args, int n) {
        if (args.length != n) {
            exit("Incorrect operands.");
        }
    }
}

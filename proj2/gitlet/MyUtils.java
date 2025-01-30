package gitlet;
import java.io.File;

import static gitlet.Utils.*;

public class MyUtils {

    /** Exit and print a message(String). */
    public static void exit(String message) {
        System.out.println(message);
        System.exit(0);
    }

    /** Check whether args is a legal expression. */
    public static void checkValidity(String[] args, int n) {
        if (args.length != n) {
            exit("Incorrect operands.");
        }
    }

    /** Check whether working dictionary is initialized. */
    public static void checkInit() {
        if (!(Repository.GITLET_DIR.exists())) {
            exit("Not in an initialized Gitlet directory.");
        }
    }

    /** Return the file in the path of .gitlet/commit/commitID. */
    public static File toCommitPath(String commitID) {
        return join(".gitlet", "commit", commitID);
    }

    /** Return the head as a Commit Object. */
    public static Commit getHeadCommit() {
        File headFile = join(".gitlet", "HEAD");
        String headCommitHash = readContentsAsString(headFile);
        File commitFile = join(".gitlet", "commit", headCommitHash);
        return readObject(commitFile, Commit.class);
    }
}

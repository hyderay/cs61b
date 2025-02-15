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
        String headCommitID = Repository.getHeadCommitID();
        File commitFile = join(".gitlet", "commit", headCommitID);
        return readObject(commitFile, Commit.class);
    }

    /** Check whether the checkout command is valid. */
    public static void handleCheckout(String[] args) {
        if (args.length == 3 && args[1].equals("--")) {
            Repository.checkout(null, args[2], null, 1);
        } else if (args.length == 4 && args[2].equals("--")) {
            Repository.checkout(args[1], args[3], null, 2);
        } else if (args.length == 2) {
            Repository.checkout(null, null, args[1], 3);
        } else {
            exit("Incorrect operands.");
        }
    }

    /** Get the branch file from .gitlet/refs. */
    public static File getBranchFile(String branchName) {
        return Utils.join(Repository.getRefsDir(), branchName);
    }

    public static String getCurrentBranchName() {
        if (Repository.isHeadDetached()) {
            return null;
        }
        return readContentsAsString(Repository.getHeadFile()).trim();
    }
}

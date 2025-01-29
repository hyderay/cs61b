package gitlet;

import java.io.File;

import static gitlet.MyUtils.*;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Sean
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The head file for storing heads. */
    private static final File HEAD_FILE = join(GITLET_DIR, "HEAD");
    /** The refs dictionary for storing branches. */
    private static final File REFS_DIR = join(GITLET_DIR, "refs");
    /** The objects dictionary for storing commits. */
    private static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    /** The commit dictionary for storing commit. */
    private static final File COMMIT_DIR = join(GITLET_DIR, "commit");




    public static void init() {
        if (GITLET_DIR.exists()) {
            exit("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        REFS_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        COMMIT_DIR.mkdir();
        /** lack of branch and commit */
        //TODO
    }

    public static void add(String fileName) {
        File f = new File(fileName);
        checkInit();
        Staging.add(f);
    }

    public static File getHeadFile() {
        return HEAD_FILE;
    }
}

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
    /** The head file */
    private static final File HEAD_DIR = join(GITLET_DIR, "HEAD");
    /** The refs dictionary */
    private static final File REFS_DIR = join(GITLET_DIR, "refs");
    /** The objects dictionary */
    private static final File OBJECTS_DIR = join(GITLET_DIR, "objects");



    public static void init() {
        if (GITLET_DIR.exists()) {
            exit("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        HEAD_DIR.mkdir();
        REFS_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        /** lack of branch and commit */
        //TODO
    }

    public static void add(String fileName) {
        File f = new File(fileName);
        /** check if the file exists
         * TODO
         */
        //checkWorkingDir();
        Staging.add(f);
    }
}

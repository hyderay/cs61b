package gitlet;

import java.io.File;
import java.util.HashMap;

import static gitlet.MyUtils.*;
import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *
 *  @author Sean
 */

public class Repository {
    /**
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

        // Initialize the initial commit
        Commit initialCommit = new Commit();
        writeObject(join(COMMIT_DIR, initialCommit.getCommitID()), initialCommit);

        // Initialize empty staging area
        Staging staging = new Staging();
        writeObject(staging.getStagingFile(), staging);
    }

    public static void add(String fileName) {
        File f = new File(fileName);
        Staging sta = new Staging();
        sta.add(f);
    }

    public static void commit(String message) {
        if (message == null || message.trim().isEmpty()) {
            exit("Please enter a commit message.");
        }

        Staging staging = new Staging();

        if (staging.getStagedFiles().isEmpty() && staging.getRemovedFiles().isEmpty()) {
            exit("No changes added to the commit.");
        }

        Commit parentCommit = getHeadCommit();
        HashMap<String, String> newBlobFiles = new HashMap<>(parentCommit.getBlobFiles());

        // Process staged files (avoid redundant blob creation)
        for (String fileName : staging.getStagedFiles().keySet()) {
            File file = new File(fileName);
            Blobs blob = new Blobs(file);
            newBlobFiles.put(fileName, blob.getID());
        }

        // Process removed files
        for (String fileName : staging.getRemovedFiles().keySet()) {
            newBlobFiles.remove(fileName); // Remove file from commit if staged for deletion
        }

        // Create new commit
        new Commit(message, parentCommit.getCommitID(), newBlobFiles);

        staging.clear();
    }

    public static File getHeadFile() {
        return HEAD_FILE;
    }

    public static File getObjectsDir() {
        return OBJECTS_DIR;
    }

    public static void rm(String fileName) {
        File f = new File(fileName);
        Staging sta = new Staging();
        sta.remove(f);
    }

    public static void log() {
        Log.execute();
    }

    public static void checkout(String commitID, String fileName, String branchName, int index) {
        switch(index) {
            case 1:
                Checkout.checkoutFile(fileName);
                break;
            case 2:
                Checkout.checkoutFileFromCommit(commitID, fileName);
                break;
            case 3:
                Checkout.checkoutBranch(branchName);
                break;
        }
    }

    public static File getRefsDir() {
        return REFS_DIR;
    }

    public static void createBranch(String name) {
        Branch.createBranch(name);
    }

    public static void status() {
        Log.status();
    }

    public static void rmBranch(String name) {
        Branch.removeBranch(name);
    }

    public static void reset(String commitID) {
        Checkout.reset(commitID);
    }

    public static void merge(String branchName) {
        Merge.merge(branchName);
    }
}

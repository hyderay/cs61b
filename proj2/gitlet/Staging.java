package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

import static gitlet.MyUtils.*;
import static gitlet.Utils.*;

public class Staging implements Serializable {
    /** File that stores the staging area */
    private static final File STAGING_FILE = join(".gitlet", "staging");
    /** Map to store staged files */
    private HashMap<String, String> stagedFiles;
    /** Map to store removed files */
    private HashMap<String, String> removedFiles;

    public Staging() {
        if (STAGING_FILE.exists()) {
            Staging loaded = readObject(STAGING_FILE, Staging.class);
            this.stagedFiles = loaded.stagedFiles;
            this.removedFiles = loaded.removedFiles;
        } else {
            stagedFiles = new HashMap<>();
            removedFiles = new HashMap<>();
        }
    }

    /** Add a file to the staging area. */
    public void add(File f) {
        if (!f.exists()) {
            exit("File does not exist.");
        }
        String content = readContentsAsString(f);
        String fileHash = sha1(content);

        Commit head = getHeadCommit();

        /** If file is unchanged compared to the head commit, remove from staging area. */
        if (head.getFileHash(f.getName()) != null
                && fileHash.equals(head.getFileHash(f.getName()))) {
            stagedFiles.remove(f.getName());
        } else {
            stagedFiles.put(f.getName(), fileHash);
        }

        removedFiles.remove(f.getName());
        save();
    }

    /** Remove a file from staging area. */
    public void remove(File f) {
        Commit head = getHeadCommit();

        if (stagedFiles.containsKey(f.getName())) {
            stagedFiles.remove(f.getName());
        } else if (head.getFileHash(f.getName()) != null) {
            removedFiles.put(f.getName(), head.getFileHash(f.getName()));
        } else {
            exit("No reason to remove the file.");
        }

        save();
    }

    public void clear() {
        stagedFiles.clear();
        removedFiles.clear();
        save();
    }

    /** Save the staging area to disk. */
    private void save() {
        writeObject(STAGING_FILE, this);
    }

    public HashMap<String, String> getStagedFiles() {
        return stagedFiles;
    }

    public HashMap<String, String> getRemovedFiles() {
        return removedFiles;
    }

    public File getStagingFile() {
        return STAGING_FILE;
    }
}

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

    public Staging() {
        if (STAGING_FILE.exists()) {
            Staging loaded = readObject(STAGING_FILE, Staging.class);
            this.stagedFiles = loaded.stagedFiles;
        } else {
            stagedFiles = new HashMap<>();
        }
    }

    /** Add a file to the staging area. */
    public static void add(File f) {
        if (!(f.exists())) {
            exit("File does not exist.");
        }
        String content = readContentsAsString(f);
        String fileHash = sha1(content);
        /** To be continued */
    }
}

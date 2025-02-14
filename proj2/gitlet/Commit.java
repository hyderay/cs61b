package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import static gitlet.Utils.*;
import static gitlet.MyUtils.*;

/** Represents a gitlet commit object.
 *  Each commit records a snapshot of the working dictionary's tracked files.
 *  A commit keeps track of:
 *  - The commit message
 *  - The timestamp of when the commit was created
 *  - A reference to parent commit
 *  - A mapping of file names to their blob reference (sha1 ID)
 *  - A Sha1 ID of current commit
 *
 *  A commit snapshot is created based on:
 *  - Files from previous commit
 *  - Files staged for addition
 *  - Files staged for removal
 *
 *  @author Sean
 */

public class Commit implements Serializable {
    /** The message of this Commit. */
    private String message;
    /** The timestamp of this commit. */
    private Date timestamp;
    /** The sha1 of the parent commit. */
    private String parent;
    /** A Map from current file name to its sha1 blob id. */
    private HashMap<String, String> blobFiles;
    /** Current commit ID. */
    private String commitID;

    /** Constructor for initial commit. */
    public Commit() {
        message = "initial commit";
        timestamp = new Date(0);
        parent = null;
        blobFiles = new HashMap<>();
        generateCommitID();
        saveCommit();
        updateHEAD();
    }

    /** Constructor for a new commit. */
    public Commit(String message, String parent, HashMap<String, String> blobFiles) {
        this.message = message;
        this.parent = parent;
        this.blobFiles = blobFiles;
        this.timestamp = new Date();

        generateCommitID();
        saveCommit();
        updateHEAD();
    }

    /** Update HEAD to point to the new commit. */
    private void updateHEAD() {
        File headFile = Repository.getHeadFile();
        String headContent = readContentsAsString(headFile);

        if (Repository.isHeadDetached()) {
            // If HEAD is detached, store commit ID directly
            writeContents(headFile, commitID);
        } else {
            // If HEAD points to a branch, update that branch reference
            File branchFile = new File(Repository.GITLET_DIR, headContent);
            writeContents(branchFile, commitID);
        }
    }

    /** Generate commit ID. */
    private void generateCommitID() {
        String parentID = parent;
        if (parent == null) {
            parentID = "";
        }
        this.commitID = sha1(message, timestamp.toString(), parentID);
    }

    /** Save the commit. */
    private void saveCommit() {
        File commitFile = toCommitPath(commitID);
        writeObject(commitFile, this);
    }

    /** Return the sha1 of the file as a string. */
    public String getFileHash(String name) {
        return blobFiles.getOrDefault(name, null);
    }

    public String getCommitID() {
        return commitID;
    }

    public String getParent() {
        return parent;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public HashMap<String, String> getBlobFiles() {
        return new HashMap<>(blobFiles);
    }
}

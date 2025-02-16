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
    private String secondParent;

    /** Constructor for initial commit. */
    public Commit() {
        message = "initial commit";
        timestamp = new Date(0);
        parent = null;
        blobFiles = new HashMap<>();
        secondParent = null;
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
        this.secondParent = null;

        generateCommitID();
        saveCommit();
        updateHEAD();
    }

    /** Constructor for a merge commit. */
    public Commit(String message, String parent,
                  String secondParent, HashMap<String, String> blobFiles) {
        this.message = message;
        this.parent = parent;
        this.secondParent = secondParent;
        this.blobFiles = blobFiles;
        this.timestamp = new Date();
        generateCommitID();
        saveCommit();
        updateHEAD();
    }

    /** Update HEAD to point to the new commit. */
    private void updateHEAD() {
        File headFile = Repository.getHeadFile();
        String headContent = readContentsAsString(headFile).trim();

        if (Repository.isHeadDetached()) {
            // If HEAD is detached, store commit ID directly
            writeContents(headFile, commitID);
        } else {
            // If HEAD points to a branch, update that branch file in refs
            File branchFile = getBranchFile(headContent);
            writeContents(branchFile, commitID);
        }
    }

    /** Generate commit ID. */
    private void generateCommitID() {
        String parentID;
        if (parent == null) {
            parentID = "";
        } else {
            parentID = parent;
        }

        String mergePart;
        if (secondParent == null) {
            mergePart = "";
        } else {
            mergePart = secondParent;
        }

        this.commitID = sha1(message, timestamp.toString(), parentID, mergePart);
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

    public String getSecondParent() {
        return secondParent;
    }
}

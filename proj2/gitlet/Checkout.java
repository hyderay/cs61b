package gitlet;

import java.io.File;
import java.util.List;

import static gitlet.Utils.*;
import static gitlet.MyUtils.*;

public class Checkout {

    public static void checkoutFile(String fileName) {
        Commit headCommit = getHeadCommit();
        String fileHash = headCommit.getFileHash(fileName);
        if (fileHash == null) {
            exit("File does not exist in that commit.");
        }
        File blobFile = join(Repository.getObjectsDir(), fileHash);
        Blobs blob = readObject(blobFile, Blobs.class);
        writeContents(new File(fileName), blob.getContent());
    }

    public static void checkoutFileFromCommit(String commitID, String fileName) {
        File commitFile = toCommitPath(commitID);
        if (commitFile == null) {
            exit("No commit with that id exists.");
        }

        Commit commit = readObject(commitFile, Commit.class);
        String fileHash = commit.getFileHash(fileName);
        if (fileHash == null) {
            exit("File does not exist in that commit.");
        }
        File blobFile = join(Repository.getObjectsDir(), fileHash);
        Blobs blob = readObject(blobFile, Blobs.class);
        writeContents(new File(fileName), blob.getContent());
    }

    public static void checkoutBranch(String branchName) {
        File branchFile = getBranchFile(branchName);
        if (!branchFile.exists()) {
            exit("No such branch exists.");
        }
        Commit currentCommit = getHeadCommit();
        String commitID = readContentsAsString(branchFile);
        if (commitID.equals(currentCommit.getCommitID())) {
            exit("No need to checkout the current branch.");
        }
        Commit newCommit = readObject(toCommitPath(commitID), Commit.class);
        List<String> cwdFiles = plainFilenamesIn(Repository.CWD);
        for (String file : cwdFiles) {
            if (!currentCommit.getBlobFiles().containsKey(file) && newCommit.getBlobFiles().containsKey(file)) {
                exit("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }
        for (String file : newCommit.getBlobFiles().keySet()) {
            File blobFile = join(Repository.getObjectsDir(), newCommit.getBlobFiles().get(file));
            Blobs blob = readObject(blobFile, Blobs.class);
            writeContents(new File(file), blob.getContent());
        }
        for (String file : currentCommit.getBlobFiles().keySet()) {
            if (!newCommit.getBlobFiles().containsKey(file)) {
                restrictedDelete(new File(file));
            }
        }
        writeContents(Repository.getHeadFile(), commitID);
        Staging staging = new Staging();
        staging.clear();
    }
}

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
        File file = Utils.join(Repository.CWD, fileName);
        writeContents(file, blob.getContent());
    }

    public static void checkoutFileFromCommit(String commitID, String fileName) {
        File commitFile = toCommitPath(commitID);
        if (!commitFile.exists()) {   // Check if the file actually exists on disk
            exit("No commit with that id exists.");
        }

        Commit commit = readObject(commitFile, Commit.class);
        String fileHash = commit.getFileHash(fileName);
        if (fileHash == null) {
            exit("File does not exist in that commit.");
        }
        File blobFile = join(Repository.getObjectsDir(), fileHash);
        Blobs blob = readObject(blobFile, Blobs.class);
        File file = Utils.join(Repository.CWD, fileName);
        writeContents(file, blob.getContent());
    }

    public static void checkoutBranch(String branchName) {
        File branchFile = getBranchFile(branchName);
        if (!branchFile.exists()) {
            exit("No such branch exists.");
        }
        String newCommitID = readContentsAsString(branchFile);
        String currentBranch = getCurrentBranchName();
        if (branchName.equals(currentBranch)) {
            exit("No need to checkout the current branch.");
        }
        Commit newCommit = readObject(toCommitPath(newCommitID), Commit.class);
        Commit currentCommit = getHeadCommit();

        untrackedFile(currentCommit, newCommit);
        checkoutCommit(currentCommit, newCommit);

        // Update HEAD with just the branch name.
        writeContents(Repository.getHeadFile(), branchName);

        new Staging().clear();
    }

    private static void untrackedFile(Commit currentCommit, Commit newCommit) {
        List<String> cwdFiles = plainFilenamesIn(Repository.CWD);
        for (String file : cwdFiles) {
            boolean currentContain = currentCommit.getBlobFiles().containsKey(file);
            boolean newContain = newCommit.getBlobFiles().containsKey(file);
            if (!currentContain && newContain) {
                exit("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
            }
        }
    }

    private static void checkoutCommit(Commit currentCommit, Commit newCommit) {
        for (String fileName : newCommit.getBlobFiles().keySet()) {
            String blobID = newCommit.getBlobFiles().get(fileName);
            Blobs blob = readObject(join(Repository.getObjectsDir(), blobID), Blobs.class);
            writeContents(join(Repository.CWD, fileName), blob.getContent());
        }

        for (String fileName : currentCommit.getBlobFiles().keySet()) {
            if (!newCommit.getBlobFiles().containsKey(fileName)) {
                restrictedDelete(join(Repository.CWD, fileName));
            }
        }
    }

    public static void reset(String commitID) {
        File commitFile = toCommitPath(commitID);
        if (!commitFile.exists()) {
            exit("No commit with that id exists.");
        }
        Commit targetCommit = readObject(commitFile, Commit.class);
        Commit currentCommit = getHeadCommit();

        untrackedFile(currentCommit, targetCommit);
        checkoutCommit(currentCommit, targetCommit);

        writeContents(Repository.getHeadFile(), commitID);

        Staging staging = new Staging();
        staging.clear();
    }
}

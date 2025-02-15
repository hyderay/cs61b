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
        File targetFile = join(Repository.CWD, fileName);
        writeContents(targetFile, blob.getContent());
    }

    public static void checkoutFileFromCommit(String commitID, String fileName) {
        File commitFile = MyUtils.toCommitPath(commitID);
        if (!commitFile.exists()) {
            exit("No commit with that id exists.");
        }
        Commit commit = readObject(commitFile, Commit.class);
        String fileHash = commit.getFileHash(fileName);
        if (fileHash == null) {
            exit("File does not exist in that commit.");
        }
        File blobFile = join(Repository.getObjectsDir(), fileHash);
        Blobs blob = readObject(blobFile, Blobs.class);
        File targetFile = join(Repository.CWD, fileName);
        writeContents(targetFile, blob.getContent());
    }

    public static void checkoutBranch(String branchName) {
        File branchFile = MyUtils.getBranchFile(branchName);
        if (!branchFile.exists()) {
            exit("No such branch exists.");
        }
        // Read the commit ID that the branch points to.
        String newCommitID = readContentsAsString(branchFile);
        String currentBranch = getCurrentBranchName();
        if (branchName.equals(currentBranch)) {
            exit("No need to checkout the current branch.");
        }
        Commit newCommit = readObject(MyUtils.toCommitPath(newCommitID), Commit.class);
        Commit currentCommit = getHeadCommit();
        // Check for untracked files that would be overwritten.
        checkUntrackedFiles(currentCommit, newCommit);
        // Update the working directory to reflect the new commit.
        updateWorkingDirectory(currentCommit, newCommit);
        // Update HEAD to point to the branch name.
        writeContents(Repository.getHeadFile(), branchName);
        // Clear the staging area.
        new Staging().clear();
    }

    private static void checkUntrackedFiles(Commit currentCommit, Commit newCommit) {
        List<String> cwdFiles = plainFilenamesIn(Repository.CWD);
        for (String file : cwdFiles) {
            // A file is considered untracked if it is not in the current commit,
            // but it is tracked in the new commit.
            boolean isTrackedInCurrent = currentCommit.getBlobFiles().containsKey(file);
            boolean existsInNew = newCommit.getBlobFiles().containsKey(file);
            if (!isTrackedInCurrent && existsInNew) {
                exit("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
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

        checkUntrackedFiles(currentCommit, targetCommit);
        updateWorkingDirectory(currentCommit, targetCommit);

        writeContents(Repository.getHeadFile(), commitID);

        Staging staging = new Staging();
        staging.clear();
    }

    private static void updateWorkingDirectory(Commit currentCommit, Commit newCommit) {
        // Write new/modified files from the new commit.
        for (String fileName : newCommit.getBlobFiles().keySet()) {
            String blobID = newCommit.getBlobFiles().get(fileName);
            File blobFile = join(Repository.getObjectsDir(), blobID);
            Blobs blob = readObject(blobFile, Blobs.class);
            File targetFile = join(Repository.CWD, fileName);
            writeContents(targetFile, blob.getContent());
        }
        // Remove files that exist in the current commit but not in the new commit.
        for (String fileName : currentCommit.getBlobFiles().keySet()) {
            if (!newCommit.getBlobFiles().containsKey(fileName)) {
                restrictedDelete(join(Repository.CWD, fileName));
            }
        }
    }
}

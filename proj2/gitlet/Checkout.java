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
        commitID = getFullCommitID(commitID);
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

    public static void reset(String commitID) {
        commitID = getFullCommitID(commitID);
        File commitFile = toCommitPath(commitID);
        if (!commitFile.exists()) {
            exit("No commit with that id exists.");
        }
        Commit targetCommit = readObject(commitFile, Commit.class);
        Commit currentCommit = getHeadCommit();

        checkUntrackedFiles(currentCommit, targetCommit);
        updateWorkingDirectory(currentCommit, targetCommit);

        if (Repository.isHeadDetached()) {
            writeContents(Repository.getHeadFile(), commitID);
        } else {
            String branchName = readContentsAsString(Repository.getHeadFile());
            File branchFile = MyUtils.getBranchFile(branchName);
            writeContents(branchFile, commitID);
        }

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

    private static String getFullCommitID(String abbreviated) {
        // If a commit file with the abbreviated name exists exactly, return it.
        File commitFile = MyUtils.toCommitPath(abbreviated);
        if (commitFile.exists()) {
            return abbreviated;
        }
        // Otherwise, search for commit files that start with the abbreviation.
        List<String> commits = plainFilenamesIn(Repository.getCommitDir());
        String fullID = null;
        for (String id : commits) {
            if (id.startsWith(abbreviated)) {
                if (fullID != null) {  // Ambiguous abbreviation: more than one match.
                    exit("No commit with that id exists.");
                }
                fullID = id;
            }
        }
        if (fullID == null) {  // No match found.
            exit("No commit with that id exists.");
        }
        return fullID;
    }
}

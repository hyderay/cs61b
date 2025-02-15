package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.MyUtils.*;
import static gitlet.Utils.*;

public class Branch implements Serializable {
    public static void createBranch(String branchName) {
        File branchFile = getBranchFile(branchName);

        if (branchFile.exists()) {
            exit("A branch with that name already exists.");
        }

        // Use Repository.getHeadCommitID() to get the actual commit ID, not the branch reference.
        String headCommitID = Repository.getHeadCommitID();

        writeContents(branchFile, headCommitID);
    }

    public static void removeBranch(String branchName) {
        File branchFile = getBranchFile(branchName);
        if (!branchFile.exists()) {
            exit("A branch with that name does not exist.");
        }
        // Use the current branch name from MyUtils to check if we're on the branch.
        String currentBranch = getCurrentBranchName();
        if (branchName.equals(currentBranch)) {
            exit("Cannot remove the current branch.");
        }
        MyUtils.safeDelete(branchFile);
    }
}

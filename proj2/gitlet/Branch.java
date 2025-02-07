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

        String headCommitID = readContentsAsString(Repository.getHeadFile());

        writeContents(branchFile, headCommitID);
    }

    public static void removeBranch(String branchName) {
        File branchFile = getBranchFile(branchName);

        if (!branchFile.exists()) {
            exit("A branch with that name does not exist.");
        }

        String headCommitID = readContentsAsString(Repository.getHeadFile());
        if (headCommitID.equals(readContentsAsString(branchFile))) {
            exit("Cannot remove the current branch.");
        }

        restrictedDelete(branchFile);
    }
}

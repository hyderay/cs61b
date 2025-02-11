package gitlet;

import java.io.File;

import static gitlet.Utils.*;
import static gitlet.MyUtils.*;

public class Merge {
    public static void merge(String branchName) {
        /** Handle uncommited changes. */
        Staging stageArea = new Staging();
        if (!stageArea.getStagedFiles().isEmpty() || !stageArea.getRemovedFiles().isEmpty()) {
            exit("You have uncommitted changes.");
        }

        /** Handle branch name doesn't exist. */
        File branchFile = getBranchFile(branchName);
        if (!branchFile.exists()) {
            exit("A branch with that name does not exist.");
        }

        /** Handle merge current branch. */
        String headCommitID = readContentsAsString(Repository.getHeadFile());
        if (headCommitID.equals(readContentsAsString(branchFile))) {
            exit("Cannot merge a branch with itself.");
        }

        
    }
}

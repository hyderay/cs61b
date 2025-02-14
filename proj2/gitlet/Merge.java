package gitlet;

import java.io.File;
import java.util.HashMap;

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
        String givenCommitID = readContentsAsString(branchFile);
        if (headCommitID.equals(givenCommitID)) {
            exit("Cannot merge a branch with itself.");
        }

        Commit headCommit = readObject(toCommitPath(headCommitID), Commit.class);
        Commit givenCommit = readObject(toCommitPath(givenCommitID), Commit.class);
        Commit splitPointCommit = findSplitPoint(headCommit, givenCommit);

        /** Split point is the same commit as the given branch. */
        if (splitPointCommit.getCommitID().equals(givenCommitID)) {
            exit("Given branch is an ancestor of the current branch.");
        }

        /** Split point is the current branch. */
        if (splitPointCommit.getCommitID().equals(headCommitID)) {
            Checkout.checkoutBranch(branchName);
            exit("Current branch fast-forwarded.");
        }

        HashMap<String, String> headMap = headCommit.getBlobFiles();
        HashMap<String, String> givenMap = givenCommit.getBlobFiles();
        HashMap<String, String> splitMap = splitPointCommit.getBlobFiles();
        boolean hasConflicts = false;

        /** Files that are only in given branch but don't exist in split point. */
        for (String file : givenMap.keySet()) {
            if (!splitMap.containsKey(file) && !headMap.containsKey(file)) {
                Checkout.checkoutFileFromCommit(givenCommitID, file);
                stageArea.add(new File(file));
            }
        }

        /** Files presented in split point to determined whether being modified. */
        for (String file : splitMap.keySet()) {
            boolean modifiedCurrent = !headMap.getOrDefault(file, "").equals(splitMap.get(file));
            boolean modifiedGiven = !givenMap.getOrDefault(file, "").equals(splitMap.get(file));
            if (!modifiedCurrent && givenMap.get(file) == null) {
                stageArea.remove(new File(file));
            } else if (!modifiedCurrent && modifiedGiven) {
                Checkout.checkoutFileFromCommit(givenCommitID, file);
                stageArea.add(new File(file));
            } else if (modifiedCurrent && modifiedGiven) {
                if (!givenMap.getOrDefault(file, "").equals(headMap.getOrDefault(file, ""))) {
                    handleConflicts(file, headMap, givenMap);
                    hasConflicts = true;
                }
            } else if (modifiedCurrent && givenMap.get(file) == null) {
                handleConflicts(file, headMap, givenMap);
                hasConflicts = true;
            } else if (modifiedGiven && headMap.get(file) == null) {
                handleConflicts(file, headMap, givenMap);
                hasConflicts = true;
            }
        }

        String splitPointCommitID = splitPointCommit.getCommitID();
        if (!splitPointCommitID.equals(headCommitID) || !splitPointCommitID.equals(givenCommitID)) {
            commitMerge(headCommitID, givenCommitID, branchName);
        }

        if (hasConflicts) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    private static Commit findSplitPoint(Commit current, Commit given) {
        while (current != null) {
            String currentID = current.getCommitID();
            Commit givenReplicate = given;
            while (givenReplicate != null) {
                String givenID = givenReplicate.getCommitID();
                if (currentID.equals(givenID)) {
                    return current;
                }
                if (givenReplicate.getParent() == null) {
                    givenReplicate = null;
                } else {
                    File file = toCommitPath(givenReplicate.getParent());
                    givenReplicate = readObject(file, Commit.class);
                }
            }
            if (current.getParent() == null) {
                current = null;
            } else {
                current = readObject(toCommitPath(current.getParent()), Commit.class);
            }
        }
        return null;
    }

    private static void handleConflicts(String file, HashMap<String, String> current,
                                        HashMap<String, String> given) {
        String content = "<<<<<<< HEAD\n" + current.getOrDefault(file, "")
                + "\n=======\n" + given.getOrDefault(file, "") + "\n>>>>>>\n";
        writeContents(new File(file), content);
        Staging sta = new Staging();
        sta.add(new File(file));
    }

    private static void commitMerge(String headCommitID, String givenCommitID, String branchName) {
        Staging stagingArea = new Staging();
        Commit headCommit = Utils.readObject(MyUtils.toCommitPath(headCommitID), Commit.class);
        HashMap<String, String> blobFile = new HashMap<>(headCommit.getBlobFiles());

        // Process staged and removed files as before.
        for (String fileName : stagingArea.getStagedFiles().keySet()) {
            File file = new File(fileName);
            Blobs blob = new Blobs(file);
            blobFile.put(fileName, blob.getID());
        }
        for (String fileName : stagingArea.getRemovedFiles().keySet()) {
            blobFile.remove(fileName);
        }

        String message = "Merged " + branchName + " into " + getCurrentBranchName() + ".";
        // Create merge commit with both parents.
        new Commit(message, headCommitID, givenCommitID, blobFile);
        stagingArea.clear();
    }
}

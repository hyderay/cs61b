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
        String headCommitID = Repository.getHeadCommitID();
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

        checkBranch(givenMap, headMap, splitMap, givenCommitID);

        boolean hasConflicts = checkModifiedFiles(splitMap, headMap, givenMap, givenCommitID);

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
        String headContent = "";
        String givenContent = "";
        String headBlobID = current.get(file);
        String givenBlobID = given.get(file);
        if (headBlobID != null) {
            File headBlobFile = Utils.join(Repository.getObjectsDir(), headBlobID);
            Blobs headBlob = Utils.readObject(headBlobFile, Blobs.class);
            headContent = headBlob.getContent();
        }
        if (givenBlobID != null) {
            File givenBlobFile = Utils.join(Repository.getObjectsDir(), givenBlobID);
            Blobs givenBlob = Utils.readObject(givenBlobFile, Blobs.class);
            givenContent = givenBlob.getContent();
        }
        String content = "<<<<<<< HEAD\n" + headContent
                + "\n=======\n" + givenContent
                + "\n>>>>>>>";
        File conflictFile = Utils.join(Repository.CWD, file);
        writeContents(conflictFile, content);
    }

    private static void commitMerge(String headCommitID, String givenCommitID, String branchName) {
        Staging stagingArea = new Staging();
        Commit headCommit = Utils.readObject(MyUtils.toCommitPath(headCommitID), Commit.class);
        HashMap<String, String> blobFile = new HashMap<>(headCommit.getBlobFiles());

        // Process staged and removed files as before.
        for (String fileName : stagingArea.getStagedFiles().keySet()) {
            File file = Utils.join(Repository.CWD, fileName);
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

    /** Files that are only in given branch but don't exist in split point. */
    private static void checkBranch(HashMap<String, String> givenMap,
                                    HashMap<String, String> headMap,
                                    HashMap<String, String> splitMap,
                                    String givenCommitID) {
        Staging stageArea = new Staging();
        for (String file : givenMap.keySet()) {
            if (!splitMap.containsKey(file) && !headMap.containsKey(file)) {
                Checkout.checkoutFileFromCommit(givenCommitID, file);
                File file2 = Utils.join(Repository.CWD, file);
                stageArea.add(file2);
            }
        }
    }

    /** Files presented in split point to determined whether being modified. */
    private static boolean checkModifiedFiles(HashMap<String, String> splitMap,
                                              HashMap<String, String> headMap,
                                              HashMap<String, String> givenMap,
                                              String givenCommitID) {
        Staging stageArea = new Staging();
        boolean hasConflicts = false;

        for (String file : splitMap.keySet()) {
            boolean modifiedCurrent = !headMap.getOrDefault(file, "").equals(splitMap.get(file));
            boolean modifiedGiven = !givenMap.getOrDefault(file, "").equals(splitMap.get(file));

            if (!modifiedCurrent && givenMap.get(file) == null) {
                File file2 = Utils.join(Repository.CWD, file);
                stageArea.remove(file2);
            } else if (!modifiedCurrent && modifiedGiven) {
                Checkout.checkoutFileFromCommit(givenCommitID, file);
                File file2 = Utils.join(Repository.CWD, file);
                stageArea.add(file2);
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
        return hasConflicts;
    }
}

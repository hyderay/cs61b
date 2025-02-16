package gitlet;

import java.io.File;
import java.util.*;

/** Handles the 'merge' command. */
public class Merge {

    /** Merges BRANCHNAME into the current branch. */
    public static void merge(String branchName) {
        // 1) Preliminary checks.
        Staging stage = new Staging();
        if (!stage.getStagedFiles().isEmpty() || !stage.getRemovedFiles().isEmpty()) {
            MyUtils.exit("You have uncommitted changes.");
        }
        File branchFile = MyUtils.getBranchFile(branchName);
        if (!branchFile.exists()) {
            MyUtils.exit("A branch with that name does not exist.");
        }
        String currentBranch = MyUtils.getCurrentBranchName();
        if (branchName.equals(currentBranch)) {
            MyUtils.exit("Cannot merge a branch with itself.");
        }
        // Get commits.
        Commit currCommit = MyUtils.getHeadCommit();
        String givenID = Utils.readContentsAsString(branchFile).trim();
        Commit givenCommit = Utils.readObject(MyUtils.toCommitPath(givenID), Commit.class);

        // Check for untracked files that would be overwritten or deleted by merge.
        MyUtils.checkUntrackedFiles(currCommit, givenCommit);

        // 2) Find split point (the latest common ancestor).
        String splitID = findSplitPoint(currCommit.getCommitID(), givenCommit.getCommitID());
        Commit splitCommit = Utils.readObject(MyUtils.toCommitPath(splitID), Commit.class);

        // 3) Trivial cases.
        if (splitID.equals(givenCommit.getCommitID())) {
            MyUtils.exit("Given branch is an ancestor of the current branch.");
        }
        if (splitID.equals(currCommit.getCommitID())) {
            // Fast-forward: just check out given branch.
            Checkout.checkoutBranch(branchName);
            MyUtils.exit("Current branch fast-forwarded.");
        }

        // 4) Do the merge logic.
        boolean conflict = mergeFiles(splitCommit, currCommit, givenCommit);

        // 5) Commit automatically if not trivial.
        String msg = "Merged " + branchName + " into " + currentBranch + ".";
        HashMap<String, String> mergedBlobs =
                new HashMap<>(MyUtils.getHeadCommit().getBlobFiles());
        // The new commit has two parents: current branch HEAD + given branch HEAD.
        new Commit(msg, currCommit.getCommitID(), givenCommit.getCommitID(), mergedBlobs);

        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /** Returns the latest common ancestor commit ID of COMMIT1 and COMMIT2. */
    private static String findSplitPoint(String commit1, String commit2) {
        // Gather all ancestors (including merge parents) of commit1.
        Set<String> ancestors = new HashSet<>();
        collectAllAncestors(commit1, ancestors);
        // Walk up from commit2 until we find one in ancestors (the "latest" common).
        return firstCommonAncestor(commit2, ancestors);
    }

    /** Recursively adds COMMIT’s ID (and all its ancestors) into SET. */
    private static void collectAllAncestors(String commitID, Set<String> set) {
        if (commitID == null || commitID.isEmpty() || set.contains(commitID)) {
            return;
        }
        set.add(commitID);
        Commit c = Utils.readObject(MyUtils.toCommitPath(commitID), Commit.class);
        collectAllAncestors(c.getParent(), set);
        collectAllAncestors(c.getSecondParent(), set);
    }

    /** Moves upward from COMMITID until finding one in ANCESTORS. */
    private static String firstCommonAncestor(String commitID, Set<String> ancestors) {
        // BFS or simple stack-based approach.
        Queue<String> queue = new LinkedList<>();
        queue.add(commitID);
        while (!queue.isEmpty()) {
            String curr = queue.poll();
            if (ancestors.contains(curr)) {
                return curr;
            }
            Commit c = Utils.readObject(MyUtils.toCommitPath(curr), Commit.class);
            if (c.getParent() != null) queue.add(c.getParent());
            if (c.getSecondParent() != null) queue.add(c.getSecondParent());
        }
        return commitID; // Fallback, should never happen if there's a common root.
    }

    /**
     * Merge the files between SPLIT, CURRENT, and GIVEN commits.
     * Returns true if a conflict occurred.
     */
    private static boolean mergeFiles(Commit split, Commit current, Commit given) {
        boolean conflict = false;
        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(split.getBlobFiles().keySet());
        allFiles.addAll(current.getBlobFiles().keySet());
        allFiles.addAll(given.getBlobFiles().keySet());

        Staging stage = new Staging();  // We'll add to staging as we go.
        for (String file : allFiles) {
            String spHash = split.getFileHash(file);
            String curHash = current.getFileHash(file);
            String givHash = given.getFileHash(file);

            boolean spSameCur = Objects.equals(spHash, curHash);
            boolean spSameGiv = Objects.equals(spHash, givHash);
            boolean curSameGiv = Objects.equals(curHash, givHash);

            // If "modified in given but not in current since split":
            if (spSameCur && !spSameGiv) {
                // Checkout from given
                checkoutAndStage(file, givHash, stage);
            }
            // If "modified in different ways -> conflict"
            else if (!curSameGiv && !spSameCur && !spSameGiv) {
                conflict = true;
                resolveConflict(file, curHash, givHash, stage);
            }
            // If one side removed and the other changed -> conflict
            else if (spSameCur && givHash == null && curHash != null) {
                conflict = true;
                resolveConflict(file, curHash, null, stage);
            } else if (spSameGiv && curHash == null && givHash != null) {
                conflict = true;
                resolveConflict(file, null, givHash, stage);
            }
        }
        stage.save(); // Ensure the updated staging is saved.
        return conflict;
    }

    /** Checks out blobHash into CWD as FILE and stages it. */
    private static void checkoutAndStage(String file, String blobHash, Staging stage) {
        if (blobHash == null) {
            // Means we want to remove the file
            File f = Utils.join(Repository.CWD, file);
            if (f.exists()) {
                Utils.restrictedDelete(f);
            }
            stage.remove(f);
        } else {
            File blobFile = Utils.join(Repository.getObjectsDir(), blobHash);
            Blobs blob = Utils.readObject(blobFile, Blobs.class);
            File target = Utils.join(Repository.CWD, file);
            Utils.writeContents(target, blob.getContent());
            stage.add(target);
        }
    }

    /** Writes conflict markers to FILE and stages the result. */
    private static void resolveConflict(String file,
                                        String curHash, String givHash, Staging stage) {
        String curContent = (curHash == null) ? "" : readContent(curHash);
        String givContent = (givHash == null) ? "" : readContent(givHash);
        String conflictText = "<<<<<<< HEAD\n" + curContent
                + "=======\n" + givContent + ">>>>>>>\n";
        File target = Utils.join(Repository.CWD, file);
        Utils.writeContents(target, conflictText);
        stage.add(target);
    }

    /** Helper to read blob content by BLOBHASH. */
    private static String readContent(String blobHash) {
        File blobFile = Utils.join(Repository.getObjectsDir(), blobHash);
        Blobs blob = Utils.readObject(blobFile, Blobs.class);
        return blob.getContent();
    }
}

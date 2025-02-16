package gitlet;

import java.io.File;
import java.util.*;

public class Merge {

    public static void merge(String branchName) {
        // Preliminary checks, plus find split point...
        // (unchanged code omitted for brevity)
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

        // Perform merges.
        boolean conflict = mergeFiles(splitCommit, currCommit, givenCommit);

        // Commit merge if non-trivial...
        // (unchanged code omitted for brevity)
        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /** Merge the files between SPLIT, CURRENT, and GIVEN commits.
     *  Returns true if a conflict occurred. */
    private static boolean mergeFiles(Commit split, Commit current, Commit given) {
        boolean conflict = false;
        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(split.getBlobFiles().keySet());
        allFiles.addAll(current.getBlobFiles().keySet());
        allFiles.addAll(given.getBlobFiles().keySet());

        Staging stage = new Staging();
        for (String file : allFiles) {
            String spHash = split.getFileHash(file);
            String curHash = current.getFileHash(file);
            String givHash = given.getFileHash(file);

            boolean spSameCur = Objects.equals(spHash, curHash);
            boolean spSameGiv = Objects.equals(spHash, givHash);
            boolean curSameGiv = Objects.equals(curHash, givHash);

            /*
             * According to the project spec:
             *
             * 1) If a file is unchanged in current branch vs. split
             *    and changed in given branch vs. split,
             *    then take the given version (including the case that it's removed).
             *
             * 2) If a file is unchanged in given branch vs. split
             *    and changed in current branch vs. split,
             *    then keep the current version (including if it was removed).
             *
             * 3) If the file is changed in both branches in the same way
             *    (same new hash or both removed), do nothing.
             *
             * 4) Otherwise, if changed in both branches differently => conflict.
             */

            // Case (3): both changed it the same way (or both removed it)
            if (!spSameCur && !spSameGiv && curSameGiv) {
                // They converged on the same new content (or both removed). No action needed.
                continue;
            }

            // Case (1): Unchanged in current, changed in given => adopt given
            if (spSameCur && !spSameGiv) {
                checkoutAndStage(file, givHash, stage);
                continue;
            }

            // Case (2): Unchanged in given, changed in current => keep current
            if (spSameGiv && !spSameCur) {
                // i.e. do nothing to the working file, since current's version is already in place
                // (If the current version is "removed," it stays removed.)
                continue;
            }

            // If we get here, the file must have been changed in both branches differently => conflict
            // This includes either different new contents or (one side changed file content,
            // the other side removed it) etc.
            conflict = true;
            resolveConflict(file, curHash, givHash, stage);
        }

        stage.save();
        return conflict;
    }

    /** Checks out blobHash (or removes file if null) and stages. */
    private static void checkoutAndStage(String file, String blobHash, Staging stage) {
        if (blobHash == null) {
            // Remove the file if present, stage for removal
            File f = Utils.join(Repository.CWD, file);
            if (f.exists()) {
                Utils.restrictedDelete(f);
            }
            stage.remove(f);
        } else {
            // Write the given branch's blob to the working directory
            File blobFile = Utils.join(Repository.getObjectsDir(), blobHash);
            Blobs blob = Utils.readObject(blobFile, Blobs.class);
            File target = Utils.join(Repository.CWD, file);
            Utils.writeContents(target, blob.getContent());
            stage.add(target);
        }
    }

    /** Writes conflict markers and stages the result. */
    private static void resolveConflict(String file,
                                        String curHash, String givHash, Staging stage) {
        String curContent = (curHash == null) ? "" : readContent(curHash);
        String givContent = (givHash == null) ? "" : readContent(givHash);
        String conflictText = "<<<<<<< HEAD\n"
                + curContent
                + "=======\n"
                + givContent
                + ">>>>>>>\n";
        File target = Utils.join(Repository.CWD, file);
        Utils.writeContents(target, conflictText);
        stage.add(target);
    }

    /** Reads the contents from a blob hash. */
    private static String readContent(String blobHash) {
        File blobFile = Utils.join(Repository.getObjectsDir(), blobHash);
        Blobs blob = Utils.readObject(blobFile, Blobs.class);
        return blob.getContent();
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
}

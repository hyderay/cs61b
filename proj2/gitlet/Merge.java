package gitlet;

import java.io.File;
import java.util.*;

public class Merge {

    public static void merge(String branchName) {
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

        Commit currCommit = MyUtils.getHeadCommit();
        String givenID = Utils.readContentsAsString(branchFile);
        Commit givenCommit = Utils.readObject(MyUtils.toCommitPath(givenID), Commit.class);
        MyUtils.checkUntrackedFiles(currCommit, givenCommit);

        String splitID = findSplitPoint(currCommit.getCommitID(), givenCommit.getCommitID());
        Commit splitCommit = Utils.readObject(MyUtils.toCommitPath(splitID), Commit.class);

        if (splitID.equals(givenCommit.getCommitID())) {
            MyUtils.exit("Given branch is an ancestor of the current branch.");
        }
        if (splitID.equals(currCommit.getCommitID())) {
            Checkout.checkoutBranch(branchName);
            MyUtils.exit("Current branch fast-forwarded.");
        }

        boolean conflict = mergeFiles(splitCommit, currCommit, givenCommit, stage);

        HashMap<String, String> newBlobs = new HashMap<>(currCommit.getBlobFiles());
        for (String f : stage.getStagedFiles().keySet()) {
            newBlobs.put(f, stage.getStagedFiles().get(f));
        }
        for (String f : stage.getRemovedFiles().keySet()) {
            newBlobs.remove(f);
        }

        if (stage.getStagedFiles().isEmpty() && stage.getRemovedFiles().isEmpty()) {
            MyUtils.exit("No changes added to the commit.");
        }

        String msg = "Merged " + branchName + " into " + currentBranch + ".";
        new Commit(msg, currCommit.getCommitID(), givenCommit.getCommitID(), newBlobs);

        stage.clear();
        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /** Merge all file differences into 'stage'. */
    private static boolean mergeFiles(Commit split, Commit current,
                                      Commit given, Staging stage) {
        boolean conflict = false;
        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(split.getBlobFiles().keySet());
        allFiles.addAll(current.getBlobFiles().keySet());
        allFiles.addAll(given.getBlobFiles().keySet());

        for (String file : allFiles) {
            String spHash = split.getFileHash(file);
            String curHash = current.getFileHash(file);
            String givHash = given.getFileHash(file);

            boolean spSameCur = Objects.equals(spHash, curHash);
            boolean spSameGiv = Objects.equals(spHash, givHash);
            boolean curSameGiv = Objects.equals(curHash, givHash);

            // (1) If unchanged in current, changed in given => adopt given
            if (spSameCur && !spSameGiv) {
                checkoutAndStage(file, givHash, stage);
                continue;
            }

            // (2) If unchanged in given, changed in current => keep current
            if (spSameGiv && !spSameCur) {
                // do nothing
                continue;
            }

            // (3) If changed in both the same way => do nothing
            if (!spSameCur && !spSameGiv && curSameGiv) {
                // both changed it but ended up the same
                continue;
            }

            // (4) Otherwise => conflict
            if (!curSameGiv) {
                conflict = true;
                resolveConflict(file, curHash, givHash, stage);
            }
        }
        stage.save(); // persist
        return conflict;
    }

    private static void checkoutAndStage(String file, String blobHash, Staging stage) {
        if (blobHash == null) {
            File f = Utils.join(Repository.CWD, file);
            if (f.exists()) {
                Utils.restrictedDelete(f);
            }
            stage.remove(f);       // Stage removal
        } else {
            File blobFile = Utils.join(Repository.getObjectsDir(), blobHash);
            Blobs blob = Utils.readObject(blobFile, Blobs.class);
            File target = Utils.join(Repository.CWD, file);
            Utils.writeContents(target, blob.getContent());
            stage.add(target);     // Stage addition
        }
    }

    private static void resolveConflict(String file,
                                        String curHash, String givHash, Staging stage) {
        String curContent;
        if (curHash == null) {
            curContent = "";
        } else {
            curContent = readContent(curHash);
        }

        String givContent;
        if (givHash == null) {
            givContent = "";
        } else {
            givContent = readContent(givHash);
        }

        String conflictText = "<<<<<<< HEAD\n"
                + curContent
                + "=======\n"
                + givContent
                + ">>>>>>>\n";
        File outFile = Utils.join(Repository.CWD, file);
        Utils.writeContents(outFile, conflictText);
        stage.add(outFile); // Stage the conflict
    }

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
            if (c.getParent() != null) {
                queue.add(c.getParent());
            }
            if (c.getSecondParent() != null) {
                queue.add(c.getSecondParent());
            }
        }
        return commitID; // Fallback, should never happen if there's a common root.
    }
}

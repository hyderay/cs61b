package gitlet;

import java.io.File;
import java.util.Set;
import java.util.HashSet;
import java.util.Stack;
import java.util.HashMap;

import static gitlet.Utils.*;
import static gitlet.MyUtils.*;

/** The Merge class implements the merge command. */
public class Merge {

    /** Merges the branch with the given name into the current branch. */
    public static void merge(String branchName) {
        // Check for uncommitted changes.
        Staging staging = new Staging();
        if (!staging.getStagedFiles().isEmpty() || !staging.getRemovedFiles().isEmpty()) {
            exit("You have uncommitted changes.");
        }

        // Check that the given branch exists.
        File branchFile = getBranchFile(branchName);
        if (!branchFile.exists()) {
            exit("A branch with that name does not exist.");
        }

        // Check that the user is not attempting to merge the branch with itself.
        String currentBranch = getCurrentBranchName();
        if (branchName.equals(currentBranch)) {
            exit("Cannot merge a branch with itself.");
        }

        // Get the current commit and the commit at the head of the given branch.
        Commit currentCommit = getHeadCommit();
        String givenCommitID = readContentsAsString(branchFile).trim();
        Commit givenCommit = readObject(toCommitPath(givenCommitID), Commit.class);

        // Check for untracked files that would be overwritten.
        checkUntrackedFiles(currentCommit, givenCommit);

        // Compute the split point (latest common ancestor) of the two commits.
        Commit splitCommit = getSplitPoint(currentCommit, givenCommit);

        // If the given branch is an ancestor of the current branch, do nothing.
        if (splitCommit.getCommitID().equals(givenCommit.getCommitID())) {
            exit("Given branch is an ancestor of the current branch.");
        }
        // If the split point is the current commit, fast-forward.
        if (splitCommit.getCommitID().equals(currentCommit.getCommitID())) {
            Checkout.checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        // Process each file in the union of files tracked by the split, current, and given commits.
        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(splitCommit.getBlobFiles().keySet());
        allFiles.addAll(currentCommit.getBlobFiles().keySet());
        allFiles.addAll(givenCommit.getBlobFiles().keySet());

        boolean conflictOccurred = false;

        for (String fileName : allFiles) {
            String sBlob = splitCommit.getFileHash(fileName);    // Blob ID in split point (may be null)
            String cBlob = currentCommit.getFileHash(fileName);    // Blob ID in current branch (may be null)
            String gBlob = givenCommit.getFileHash(fileName);      // Blob ID in given branch (may be null)

            // Case 1: File unchanged in current (cBlob equals split) but modified in given.
            if (sBlob != null && sBlob.equals(cBlob) && gBlob != null && !gBlob.equals(sBlob)) {
                checkoutFileFromBlob(gBlob, fileName);
                staging.add(join(Repository.CWD, fileName));
            }
            // Case 2: File unchanged in given (gBlob equals split) but modified in current:
            // (do nothing; keep current version)
            else if (sBlob != null && sBlob.equals(gBlob) && cBlob != null && !cBlob.equals(sBlob)) {
                // Do nothing.
            }
            // Case 3: File not present in split and present only in given branch.
            else if (sBlob == null && cBlob == null && gBlob != null) {
                checkoutFileFromBlob(gBlob, fileName);
                staging.add(join(Repository.CWD, fileName));
            }
            // Case 4: File present in split, unmodified in current, but removed in given.
            else if (sBlob != null && sBlob.equals(cBlob) && gBlob == null) {
                File target = join(Repository.CWD, fileName);
                if (target.exists()) {
                    restrictedDelete(target);
                }
                staging.getRemovedFiles().put(fileName, sBlob);
            }
            // Case 5: File present in split, unmodified in given, and removed in current.
            else if (sBlob != null && sBlob.equals(gBlob) && cBlob == null) {
                // Do nothing.
            }
            // Case 6: File modified in both branches in the same way.
            else if (cBlob != null && gBlob != null && cBlob.equals(gBlob)) {
                // Do nothing.
            }
            // Case 7: Conflict—files have been modified differently.
            else {
                conflictOccurred = true;
                String currentContent = (cBlob != null) ? getBlobContent(cBlob) : "";
                String givenContent = (gBlob != null) ? getBlobContent(gBlob) : "";
                String conflictContent = "<<<<<<< HEAD\n" + currentContent
                        + "=======\n" + givenContent + ">>>>>>>\n";
                File target = join(Repository.CWD, fileName);
                writeContents(target, conflictContent);
                staging.add(target);
            }
        }

        // Build the blobFiles map for the new merge commit by starting with current commit's blobs,
        // then applying staged additions and removals.
        HashMap<String, String> mergedBlobFiles = new HashMap<>(currentCommit.getBlobFiles());
        for (String fileName : staging.getStagedFiles().keySet()) {
            mergedBlobFiles.put(fileName, staging.getStagedFiles().get(fileName));
        }
        for (String fileName : staging.getRemovedFiles().keySet()) {
            mergedBlobFiles.remove(fileName);
        }

        String mergeMessage = "Merged " + branchName + " into " + currentBranch + ".";
        // Create a merge commit with two parents.
        new Commit(mergeMessage, currentCommit.getCommitID(), givenCommit.getCommitID(), mergedBlobFiles);
        staging.clear();

        if (conflictOccurred) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /** Returns the content stored in the blob with the given ID. */
    private static String getBlobContent(String blobID) {
        File blobFile = join(Repository.getObjectsDir(), blobID);
        Blobs blob = readObject(blobFile, Blobs.class);
        return blob.getContent();
    }

    /** Checks out (writes to the working directory) the file with name FILE_NAME
     *  from the blob identified by BLOB_ID. */
    private static void checkoutFileFromBlob(String blobID, String fileName) {
        File blobFile = join(Repository.getObjectsDir(), blobID);
        Blobs blob = readObject(blobFile, Blobs.class);
        File target = join(Repository.CWD, fileName);
        writeContents(target, blob.getContent());
    }

    /** Computes and returns the split point (latest common ancestor) of COMMITS a and b.
     *  This implementation computes the set of all ancestor commit IDs for each branch,
     *  then picks a candidate that is not an ancestor of any other common commit. */
    private static Commit getSplitPoint(Commit a, Commit b) {
        Set<String> ancestorsA = getAllAncestorIDs(a);
        Set<String> ancestorsB = getAllAncestorIDs(b);
        // Include the commits themselves.
        ancestorsA.add(a.getCommitID());
        ancestorsB.add(b.getCommitID());
        // Find the intersection.
        Set<String> common = new HashSet<>(ancestorsA);
        common.retainAll(ancestorsB);

        // Choose the candidate that is not an ancestor of any other candidate.
        for (String candidateID : common) {
            Commit candidate = readObject(toCommitPath(candidateID), Commit.class);
            boolean isLatest = true;
            for (String otherID : common) {
                if (otherID.equals(candidateID)) {
                    continue;
                }
                Commit other = readObject(toCommitPath(otherID), Commit.class);
                if (isAncestor(candidate, other)) {
                    isLatest = false;
                    break;
                }
            }
            if (isLatest) {
                return candidate;
            }
        }
        // Fallback (should not happen): return the current commit.
        return a;
    }

    /** Returns a set of all ancestor commit IDs (reachable via parent pointers)
     *  from the given commit. */
    private static Set<String> getAllAncestorIDs(Commit commit) {
        Set<String> ancestors = new HashSet<>();
        Stack<Commit> stack = new Stack<>();
        stack.push(commit);
        while (!stack.isEmpty()) {
            Commit current = stack.pop();
            String id = current.getCommitID();
            if (!ancestors.contains(id)) {
                ancestors.add(id);
                if (current.getParent() != null) {
                    Commit parentCommit = readObject(toCommitPath(current.getParent()), Commit.class);
                    stack.push(parentCommit);
                }
                if (current.getSecondParent() != null) {
                    Commit secondParentCommit = readObject(toCommitPath(current.getSecondParent()), Commit.class);
                    stack.push(secondParentCommit);
                }
            }
        }
        return ancestors;
    }

    /** Returns true if ANCESTOR is an ancestor of DESCENDANT. */
    private static boolean isAncestor(Commit ancestor, Commit descendant) {
        Set<String> visited = new HashSet<>();
        Stack<Commit> stack = new Stack<>();
        stack.push(descendant);
        while (!stack.isEmpty()) {
            Commit cur = stack.pop();
            if (cur.getCommitID().equals(ancestor.getCommitID())) {
                return true;
            }
            if (cur.getParent() != null && !visited.contains(cur.getParent())) {
                visited.add(cur.getParent());
                Commit parent = readObject(toCommitPath(cur.getParent()), Commit.class);
                stack.push(parent);
            }
            if (cur.getSecondParent() != null && !visited.contains(cur.getSecondParent())) {
                visited.add(cur.getSecondParent());
                Commit secondParent = readObject(toCommitPath(cur.getSecondParent()), Commit.class);
                stack.push(secondParent);
            }
        }
        return false;
    }
}

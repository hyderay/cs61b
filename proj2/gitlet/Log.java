package gitlet;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import static gitlet.MyUtils.*;
import static gitlet.Utils.*;

public class Log {
    public static void execute() {
        Commit currentCommit = MyUtils.getHeadCommit();

        while (currentCommit != null) {
            printCommit(currentCommit);

            if (currentCommit.getParent() == null) {
                break;
            }
            currentCommit = readObject(toCommitPath(currentCommit.getParent()), Commit.class);
        }
    }

    private static void printCommit(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.getCommitID());

        if (commit.getSecondParent() != null) {
            String firstParentAbbrev = commit.getParent().substring(0, 7);
            String secondParentAbbrev = commit.getSecondParent().substring(0, 7);
            System.out.println("Merge: " + firstParentAbbrev + " " + secondParentAbbrev);
        }

        SimpleDateFormat format = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        String formattedDate = format.format(commit.getTimestamp());
        System.out.println("Date: " + formattedDate);
        System.out.println(commit.getMessage());
        System.out.println();
    }

    public static void status() {

        /** Print branches. */
        System.out.println("=== Branches ===");
        // Read the HEAD file to determine the current branch reference.
        String headRef = readContentsAsString(Repository.getHeadFile());
        // If HEAD is attached, extract the branch name by removing the "refs/heads/" prefix.
        String currentBranch;
        if (headRef.startsWith("refs/heads/")) {
            currentBranch = headRef.substring("refs/heads/".length());
        } else {
            currentBranch = null;
        }
        // List only branch files from .gitlet/refs/heads.
        File headsDir = Utils.join(Repository.getRefsDir(), "heads");
        File[] branches = headsDir.listFiles();

        for (File branch : branches) {
            // Compare the branch file name with the current branch name.
            if (branch.getName().equals(currentBranch)) {
                System.out.println("*" + branch.getName());
            } else {
                System.out.println(branch.getName());
            }
        }

        System.out.println();

        /** Print staged files. */
        System.out.println("=== Staged Files ===");
        Staging stagingArea = new Staging();
        for (String name : stagingArea.getStagedFiles().keySet()) {
            System.out.println(name);
        }
        System.out.println();

        /** Print removed files. */
        System.out.println("=== Removed Files ===");
        for (String name : stagingArea.getRemovedFiles().keySet()) {
            System.out.println(name);
        }
        System.out.println();

        /** Print modifications not staged files. */
        System.out.println("=== Modifications Not Staged For Commit ===");
        Commit headCommit = getHeadCommit();
        for (String fileName : headCommit.getBlobFiles().keySet()) {
            File file = new File(fileName);
            if (!file.exists()) {
                System.out.println(fileName + " (deleted)");
            } else {
                String currentContent = readContentsAsString(file);
                String trackedFile = headCommit.getFileHash(fileName);
                if (!sha1(currentContent).equals(trackedFile)) {
                    System.out.println(fileName + " (modified)");
                }
            }
        }
        System.out.println();

        /** Print untracked files. */
        System.out.println("=== Untracked Files ===");
        List<String> cwdFiles = plainFilenamesIn(Repository.CWD);
        for (String file : cwdFiles) {
            boolean staContain = stagingArea.getStagedFiles().containsKey(file);
            boolean headContain = headCommit.getFileHash(file) == null;
            if (!staContain && headContain) {
                System.out.println(file);
            }
        }
        System.out.println();
    }

    public static void globalLog() {
        List<String> allCommits = plainFilenamesIn(Repository.getCommitDir());
        for (String commitID : allCommits) {
            Commit commit = readObject(join(Repository.getCommitDir(), commitID), Commit.class);
            printCommit(commit);
        }
    }
}

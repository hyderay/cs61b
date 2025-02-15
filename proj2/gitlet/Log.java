package gitlet;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
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
        printBranches();

        printStagedFiles();

        printRemovedFiles();

        printModifiedNotStage();

        printUntrackedFiles();
    }

    public static void globalLog() {
        List<String> allCommits = plainFilenamesIn(Repository.getCommitDir());
        for (String commitID : allCommits) {
            Commit commit = readObject(join(Repository.getCommitDir(), commitID), Commit.class);
            printCommit(commit);
        }
    }

    private static void printBranches() {
        System.out.println("=== Branches ===");
        String currentBranch = getCurrentBranchName();

        File refsDir = Repository.getRefsDir();
        File[] branches = refsDir.listFiles();

        for (File branch : branches) {
            String branchName = branch.getName();
            if (branchName.equals(currentBranch)) {
                System.out.println("*" + branchName);
            } else {
                System.out.println(branchName);
            }
        }

        System.out.println();
    }

    private static void printStagedFiles() {
        System.out.println("=== Staged Files ===");
        Staging stagingArea = new Staging();
        for (String name : stagingArea.getStagedFiles().keySet()) {
            System.out.println(name);
        }
        System.out.println();
    }

    private static void printModifiedNotStage() {
        System.out.println("=== Modifications Not Staged For Commit ===");
        Commit headCommit = getHeadCommit();
        Staging stagingArea = new Staging();
        HashMap<String, String> blobFiles = headCommit.getBlobFiles();
        HashMap<String, String> stagedFiles = stagingArea.getStagedFiles();

        for (String fileName : blobFiles.keySet()) {
            File currentFile = join(Repository.CWD, fileName);
            if (!isStaged(currentFile)) {
                if (!currentFile.exists()) {
                    System.out.println(fileName + " (deleted)");
                    continue;
                }
                String blobID = blobFiles.get(fileName);
                File blobFile = join(Repository.getObjectsDir(), blobID);
                Blobs blob = readObject(blobFile, Blobs.class);
                String commitHash = blob.getContent();
                if (!commitHash.equals(readContentsAsString(currentFile))) {
                    System.out.println(fileName + " (modified)");
                }
            }
        }

        for (String fileName : stagedFiles.keySet()) {
            File currentFile = join(Repository.CWD, fileName);
            if (!currentFile.exists()) {
                System.out.println(fileName + " (deleted)");
                continue;
            }
            String stagedHash = stagedFiles.get(fileName);
            if (!stagedHash.equals(readContentsAsString(currentFile))) {
                System.out.println(fileName + " (modified)");
            }
        }
        System.out.println();
    }

    private static void printRemovedFiles() {
        System.out.println("=== Removed Files ===");
        Staging stagingArea = new Staging();
        for (String name : stagingArea.getRemovedFiles().keySet()) {
            System.out.println(name);
        }
        System.out.println();
    }

    private static void printUntrackedFiles() {
        System.out.println("=== Untracked Files ===");
        List<String> cwdFiles = plainFilenamesIn(Repository.CWD);
        Staging stagingArea = new Staging();
        for (String file : cwdFiles) {
            // A file is untracked if it is neither staged nor present in the current (HEAD) commit.
            boolean stagedInIndex = stagingArea.getStagedFiles().containsKey(file);
            Commit headCommit = getHeadCommit();
            boolean trackedInHead = headCommit.getFileHash(file) != null;
            if (!stagedInIndex && !trackedInHead) {
                System.out.println(file);
            }
        }
        System.out.println();
    }
}

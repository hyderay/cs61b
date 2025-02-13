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

        if (commit.getMessage().startsWith("Merged ")) {
            String[] words = commit.getMessage().split(" ");
            String firstParentID = commit.getParent();
            String secondParentName = words[1];
            File secondParent = getBranchFile(secondParentName);
            String secondParentID = readContentsAsString(secondParent);
            firstParentID = firstParentID.substring(0, 7);
            secondParentID = secondParentID.substring(0, 7);
            System.out.println("Merge: " + firstParentID + " " + secondParentID);
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
        File[] branches = Repository.getRefsDir().listFiles();
        String headContent = readContentsAsString(Repository.getHeadFile());

        for (File branch : branches) {
            if (("refs/" + branch.getName()).equals(headContent)
                    || branch.getName().equals(headContent)) {
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

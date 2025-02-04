package gitlet;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TimeZone;

import static gitlet.MyUtils.*;
import static gitlet.Utils.*;

public class Log {
    public static void execute() {
        Commit CurrentCommit = MyUtils.getHeadCommit();

        while (CurrentCommit != null) {
            printCommit(CurrentCommit);

            if (CurrentCommit.getParent() == null) {
                break;
            }
            CurrentCommit = Utils.readObject(MyUtils.toCommitPath(CurrentCommit.getParent()), Commit.class);
        }
    }

    private static void printCommit(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.getCommitID());

        /** Ignoring the merge at this time.
         * TODO
         * */

        SimpleDateFormat format = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        String formattedDate = format.format(commit.getTimestamp());
        System.out.println("Date: " + formattedDate);
        System.out.println(commit.getMessage());
        System.out.println();
    }

    public static void status() {

        /** Print out branches. */
        System.out.println("=== Branches ===");
        File[] branches = Repository.getRefsDir().listFiles();
        String headBranch = readContentsAsString(Repository.getHeadFile());
        for (File branch : branches) {
            String branchName = branch.getName();
            if (branchName.equals(headBranch)) {
                System.out.println("*" + branchName);
            } else {
                System.out.println(branchName);
            }
        }
        System.out.println();

        /** Print out staged files. */
        System.out.println("=== Staged Files ===");
        Staging stagingArea = new Staging();
        for (String name : stagingArea.getStagedFiles().keySet()) {
            System.out.println(name);
        }
        System.out.println();

        /** Print out removed files. */
        System.out.println("=== Removed Files ===");
        for (String name : stagingArea.getRemovedFiles().keySet()) {
            System.out.println(name);
        }
        System.out.println();
    }
}

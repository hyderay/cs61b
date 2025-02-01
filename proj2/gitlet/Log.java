package gitlet;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

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
}

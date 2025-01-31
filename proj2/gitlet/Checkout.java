package gitlet;

import java.io.File;
import static gitlet.Utils.*;
import static gitlet.MyUtils.*;

public class Checkout {

    public void checkoutFile(String fileName) {
        Commit headCommit = getHeadCommit();
        String fileHash = headCommit.getFileHash(fileName);
        if (fileHash == null) {
            exit("File does not exist in that commit.");
        }
        File blobFile = join(Repository.getObjectsDir(), fileHash);
        Blobs blob = readObject(blobFile, Blobs.class);
        writeContents(new File(fileName), blob.getContent());
    }

    public void checkoutFileFromCommit(String commitID, String fileName) {
        File commitFile = toCommitPath(commitID);
        if (commitFile == null) {
            exit("No commit with that id exists.");
        }

        Commit commit = readObject(commitFile, Commit.class);
        String fileHash = commit.getFileHash(fileName);
        if (fileHash == null) {
            exit("File does not exist in that commit.");
        }
        File blobFile = join(Repository.getObjectsDir(), fileHash);
        Blobs blob = readObject(blobFile, Blobs.class);
        writeContents(new File(fileName), blob.getContent());
    }
}

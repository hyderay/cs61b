package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

public class Blobs implements Serializable {
    /** The sha1 hash of the blob content. */
    private String ID;
    /** The content of the blob. */
    private String content;

    public Blobs (File f) {
        content = readContentsAsString(f);
        ID = sha1(content);
        saveBlob();
    }

    private void saveBlob() {
        File blobFile = toBlobPath(ID);
        writeObject(blobFile, this);
    }

    private static File toBlobPath(String id) {
        return join(Repository.getObjectsDir(), id);
    }

    public String getID() {
        return ID;
    }

    public String getContent() {
        return content;
    }
}

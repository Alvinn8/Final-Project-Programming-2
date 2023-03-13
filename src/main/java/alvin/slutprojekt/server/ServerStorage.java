package alvin.slutprojekt.server;

import alvin.slutprojekt.storage.Storage;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A class used for storing files in the computer relating to the server side game.
 * <p>
 * This is used to save the worlds and player data.
 */
public class ServerStorage extends Storage {
    public ServerStorage() {
        super(findRoot());
    }

    private static Path findRoot() {
        return Paths.get("run-server");
    }
}

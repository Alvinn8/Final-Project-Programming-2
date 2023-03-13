package alvin.slutprojekt.client;

import alvin.slutprojekt.storage.Storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class used for storing files on the user's computer relating to the game.
 * <p>
 * This is used to save settings and the user's worlds.
 */
public class ClientStorage extends Storage {
    public static final String DATA_FOLDER_NAME = "slutprojekt-alvin-prr2";
    public static final String NAME_FILE = "name.txt";

    public ClientStorage() {
        super(findRoot());
    }

    private static Path findRoot() {
        String appdata = System.getenv("APPDATA");
        if (appdata != null) {
            return Paths.get(appdata, DATA_FOLDER_NAME);
        }
        String home = System.getProperty("user.home");
        if (Device.IS_MAC) {
            return Paths.get(home, "Library", "Application Support", DATA_FOLDER_NAME);
        }
        return Paths.get(home, '.' + DATA_FOLDER_NAME);
    }

    /**
     * Check if the storage folder exists and has been created.
     *
     * @return Whether the storage folder exists.
     */
    public boolean exists() {
        return Files.exists(root) && Files.isDirectory(root);
    }

    /**
     * Create the data folder.
     *
     * @throws IOException If an I/O error occurs.
     */
    public void create() throws IOException {
        Files.createDirectories(root);
    }

    /**
     * Check whether the player has set a name.
     *
     * @return Whether the player has set a name.
     */
    public boolean hasName() {
        Path path = root.resolve(NAME_FILE);
        return Files.exists(path);
    }

    /**
     * Get the saved name of the player.
     *
     * @return The name of the player.
     * @throws IOException If an I/O error occurs.
     */
    public String getName() throws IOException {
        Path path = root.resolve(NAME_FILE);
        return String.join(" ", Files.readAllLines(path));
    }

    /**
     * Set the saved name of the player.
     *
     * @param name The name to save.
     * @throws IOException If an I/O error occurs.
     */
    public void setName(String name) throws IOException {
        Path path = root.resolve(NAME_FILE);
        Files.write(path, name.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Get a list of worlds the user has stored.
     *
     * @return The list of world names.
     */
    public List<String> getWorldNames() {
        List<String> worldNames = new ArrayList<>();

        Path worldsFolder = getRoot().resolve(WORLDS_FOLDER_NAME);
        if (!Files.exists(worldsFolder)) {
            return Collections.emptyList();
        }
        try {
            Files.list(worldsFolder)
                .filter(Files::isDirectory)
                .forEach(worldPath -> {
                    Path worldNameFile = worldPath.resolve(WORLD_NAME_FILE);
                    try {
                        String worldName = String.join("", Files.readAllLines(worldNameFile));
                        worldNames.add(worldName);
                    } catch (IOException e) {
                        System.out.println("Failed to read world name for " + worldPath.getFileName());
                    }
                });
        } catch (IOException e) {
            System.err.println("Unable to get a list of worlds.");
            e.printStackTrace();
        }
        return worldNames;
    }

    /**
     * Get the seed used for a world.
     *
     * @param worldName The name of the world.
     * @return The seed.
     */
    public long getSeedFor(String worldName) {
        try {
            Path worldPath = this.getWorldPath(worldName);
            Path seedFilePath = worldPath.resolve(WORLD_SEED_FILE);
            String seedStr = String.join("", Files.readAllLines(seedFilePath));
            return Long.parseLong(seedStr);
        } catch (IOException | NumberFormatException e) {
            System.err.println("Failed to read seed for " + worldName);
            e.printStackTrace();
        }
        return 1234;
    }

    /**
     * Check if there exists a world with the specified name.
     *
     * @param worldName The world name.
     * @return Whether the world exists.
     */
    public boolean worldExists(String worldName) {
        return Files.isDirectory(this.getWorldPath(worldName));
    }
}

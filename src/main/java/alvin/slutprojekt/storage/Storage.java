package alvin.slutprojekt.storage;

import alvin.slutprojekt.client.ClientMain;
import alvin.slutprojekt.client.UserPlayer;
import alvin.slutprojekt.server.ServerPlayer;
import alvin.slutprojekt.world.Environment;
import alvin.slutprojekt.world.World;
import alvin.slutprojekt.world.chunk.Chunk;
import alvin.slutprojekt.world.gameobject.GameObject;
import alvin.slutprojekt.world.gameobject.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Used for storing game-related data as files on the system.
 */
public abstract class Storage implements Runnable {
    public static final Pattern NAME_REGEX = Pattern.compile("[^A-Za-z0-9-]");
    public static final String WORLDS_FOLDER_NAME = "worlds";
    public static final String CHUNKS_FOLDER_NAME = "chunks";
    public static final String LOCAL_PLAYER_FILE_NAME = "local-player.bin.gz";
    public static final String WORLD_NAME_FILE = "world-name.txt";
    public static final String WORLD_SEED_FILE = "seed.txt";

    protected final Path root;
    private final Thread thread;
    private final LinkedBlockingQueue<StorageRequest> queue = new LinkedBlockingQueue<>();

    public Storage(Path root) {
        this.root = root;
        this.thread = new Thread(this, "Storage Thread");
        this.thread.start();
    }

    /**
     * Get a clean name, removing all weird characters. This is what will be stored on
     * disk for the specific thing being saved-
     *
     * @param name The display name.
     * @return The clean name.
     */
    public static String getCleanName(String name) {
        return NAME_REGEX.matcher(name).replaceAll("_");
    }

    /**
     * Get the root of the folder used for storage by this project.
     *
     * @return The path to the folder.
     */
    public Path getRoot() {
        return root;
    }

    protected Path getWorldPath(String worldName) {
        Path worldsFolder = getRoot().resolve(WORLDS_FOLDER_NAME);
        return worldsFolder.resolve(getCleanName(worldName));
    }

    private Path getWorldPath(World world) {
        Path worldsFolder = getRoot().resolve(WORLDS_FOLDER_NAME);
        return worldsFolder.resolve(getCleanName(world.getName()));
    }

    private Path getChunksFolder(World world, Environment environment) {
        Path worldFolder = this.getWorldPath(world);
        Path environmentFolder = worldFolder.resolve(world.getEnvironmentId(environment));
        return environmentFolder.resolve(CHUNKS_FOLDER_NAME);
    }

    private String getChunkFileName(int chunkX, int chunkY) {
        return chunkX + "_" + chunkY + ".bin.gz";
    }

    private Path getLocalPlayerPath(World world) {
        Path worldFolder = this.getWorldPath(world);
        return worldFolder.resolve(LOCAL_PLAYER_FILE_NAME);
    }

    private Path getPlayerPath(Player player) {
        Path worldFolder = this.getWorldPath(player.getEnvironment().getWorld());
        Path playersFolder = worldFolder.resolve("players");
        return playersFolder.resolve(getCleanName(player.getName())+ ".bin.gz");
    }

    /**
     * Load a chunk from storage, if it exists.
     * <p>
     * If the chunk does not exist in storage, or it failed to load,
     * null will be returned.
     *
     * @param world The world the chunk is in.
     * @param environment The environment the chunk is in.
     * @param chunkX The x coordinate of the chunk.
     * @param chunkY The y coordinate of the chunk.
     * @return The loaded chunk, or null.
     */
    public Chunk loadChunk(World world, Environment environment, int chunkX, int chunkY) {
        Path chunksFolder = getChunksFolder(world, environment);
        String fileName = getChunkFileName(chunkX, chunkY);
        Path filePath = chunksFolder.resolve(fileName);

        if (Files.exists(filePath)) {
            try (GZIPInputStream in = new GZIPInputStream(Files.newInputStream(filePath))) {
                Chunk chunk = new Chunk(environment, chunkX, chunkY);
                chunk.read(in);
                return chunk;
            } catch (IOException e) {
                // Print stack trace, and return null (below)
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Save the chunk into storage.
     * <p>
     * It is recommended to get the synchronization lock on the chunk before calling
     * this method.
     *
     * @param world The world the chunk is in.
     * @param environment The environment the chunk is in.
     * @param chunk The chunk.
     * @throws IOException If an I/O error occurs while saving.
     */
    public void saveChunk(World world, Environment environment, Chunk chunk) throws IOException {
        System.out.println("Saving chunk " + chunk.getChunkX() + " " + chunk.getChunkY());

        Path chunksFolder = getChunksFolder(world, environment);
        Files.createDirectories(chunksFolder);

        String fileName = getChunkFileName(chunk.getChunkX(), chunk.getChunkY());
        Path filePath = chunksFolder.resolve(fileName);

        // try to auto close, even in case of an exception
        try (GZIPOutputStream out = new GZIPOutputStream(Files.newOutputStream(filePath))) {
            out.write(chunk.writeToBytes());
            chunk.markAsSaved();
        }
    }

    /**
     * Save all modified chunks in the world.
     *
     * @param world The world to save.
     */
    public void saveWorld(World world) {
        for (Environment environment : world.getEnvironments()) {
            saveEnvironment(world, environment);
        }
        Path worldPath = getWorldPath(world);
        Path worldNamePath = worldPath.resolve(WORLD_NAME_FILE);
        Path worldSeedPath = worldPath.resolve(WORLD_SEED_FILE);
        try {
            Files.write(worldNamePath, world.getName().getBytes(StandardCharsets.UTF_8));
            Files.write(worldSeedPath, Long.toString(world.getSeed()).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("Failed to write world name or seed.");
            e.printStackTrace();
        }
    }

    /**
     * Save all chunks with changes in the environment.
     *
     * @param world The world the environment is in.
     * @param environment The environment.
     */
    public void saveEnvironment(World world, Environment environment) {
        System.out.println("Saving...");
        for (Chunk chunk : environment.getChunkProvider().getLoadedChunks()) {
            if (chunk.hasChanges()) {
                try {
                    saveChunk(world, environment, chunk);
                } catch (IOException e) {
                    System.err.println("Failed to save chunk " + chunk);
                    e.printStackTrace();
                }
            }
        }
        if (environment.getMain().isClientSide()) {
            Path localPlayerPath = getLocalPlayerPath(world);

            UserPlayer userPlayer = ((ClientMain) environment.getMain()).getPlayer();
            try {
                this.savePlayer(userPlayer, localPlayerPath);
            } catch (IOException e) {
                System.err.println("Failed to save local player.");
                e.printStackTrace();
            }
        } else {
            synchronized (environment.getGameObjectLock()) {
                for (GameObject gameObject : environment.getGameObjects()) {
                    if (gameObject instanceof Player) {
                        this.savePlayer((Player) gameObject);
                    }
                }
            }
        }
        System.out.println("Saved!");
    }

    /**
     * Save a player's data to a file.
     *
     * @param player The player whose data to save.
     * @param path The path of the file to save the data.
     * @throws IOException If an I/O error occurs while writing.
     */
    public void savePlayer(Player player, Path path) throws IOException {
        System.out.println("Saving player: " + player.getName());

        Files.createDirectories(path.getParent());

        // try-with-resources to ensure the streams are closed, even in case of error.
        try (DataOutputStream stream = new DataOutputStream(new GZIPOutputStream(Files.newOutputStream(path)))) {
            player.write(stream);
        }
    }

    public void savePlayer(Player player) {
        Path path = this.getPlayerPath(player);
        try {
            this.savePlayer(player, path);
        } catch (IOException e) {
            System.err.println("Failed to save player data for " + player.getName());
            e.printStackTrace();
        }
    }

    public void readPlayer(Player player, Path path) {
        if (!Files.exists(path)) {
            return;
        }

        try (DataInputStream stream = new DataInputStream(new GZIPInputStream(Files.newInputStream(path)))) {
            player.read(stream);
        } catch (IOException e) {
            System.err.println("Failed to read local player data.");
            e.printStackTrace();
        }
    }

    /**
     * Load the local player's data.
     * <p>
     * Will get the data from the world the player currently is in.
     *
     * @param player The player to load the data from.
     */
    public void readLocalPlayer(UserPlayer player) {
        Path localPlayerPath = getLocalPlayerPath(player.getEnvironment().getWorld());
        this.readPlayer(player, localPlayerPath);
    }

    /**
     * Read a player's data.
     *
     * @param player The player.
     */
    public void readPlayer(ServerPlayer player) {
        Path path = this.getPlayerPath(player);
        this.readPlayer(player, path);
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                StorageRequest storageRequest = this.queue.take();
                storageRequest.execute(this);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a {@link StorageRequest} to the queue of requests to execute.
     *
     * @param request The request.
     */
    public void addToQueue(StorageRequest request) {
        this.queue.add(request);
    }

    /**
     * Get the Storage Thread.
     *
     * @return The thread.
     */
    public Thread getThread() {
        return thread;
    }
}

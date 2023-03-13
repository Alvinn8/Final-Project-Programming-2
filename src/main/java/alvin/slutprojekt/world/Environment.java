package alvin.slutprojekt.world;

import alvin.slutprojekt.AbstractMain;
import alvin.slutprojekt.Tickable;
import alvin.slutprojekt.client.screen.PlayingScreen;
import alvin.slutprojekt.networking.packet.ToClientMovePacket;
import alvin.slutprojekt.networking.packet.ToClientRemoveGameObjectPacket;
import alvin.slutprojekt.server.ServerEnvironment;
import alvin.slutprojekt.storage.SaveEnvironmentRequest;
import alvin.slutprojekt.world.chunk.Chunk;
import alvin.slutprojekt.world.chunk.ChunkProvider;
import alvin.slutprojekt.world.gameobject.GameObject;
import alvin.slutprojekt.world.tile.TileType;

import java.util.ArrayList;
import java.util.List;

/**
 * An environment in the {@link World}. Holds {@link Chunk}s which contain two layers.
 * <p>
 * The environment also holds {@link GameObject}s (like players and animals).
 */
public class Environment implements Tickable {
    private final AbstractMain main;
    private final World world;
    private final Object gameObjectLock = new Object();
    private final ChunkProvider chunkProvider;
    protected final List<GameObject> gameObjects;
    private long lastSave;

    public Environment(AbstractMain main, World world, ChunkProvider chunkProvider) {
        this.main = main;
        this.world = world;
        this.chunkProvider = chunkProvider;
        this.chunkProvider.setEnvironment(this);
        this.gameObjects = new ArrayList<>();
        this.lastSave = System.currentTimeMillis();
    }

    public World getWorld() {
        return this.world;
    }

    public ChunkProvider getChunkProvider() {
        return this.chunkProvider;
    }

    /**
     * Get a tile in the world.
     * <p>
     * Will only get the tile if the chunk exists cached.
     *
     * @param layerType The layer the tile is on.
     * @param x The x coordinate of the tile.
     * @param y The y coordinate of the tile.
     * @return The tile type, or null.
     */
    public TileType getTile(LayerType layerType, int x, int y) {
        int chunkX = CoordinateUtil.tileToChunk(x);
        int chunkY = CoordinateUtil.tileToChunk(y);
        Chunk chunk = this.chunkProvider.getChunkIfExists(chunkX, chunkY);
        if (chunk != null) {
            int relativeX = CoordinateUtil.tileToRelative(x);
            int relativeY = CoordinateUtil.tileToRelative(y);
            return chunk.getTileAt(layerType, relativeX, relativeY);

        }
        return null;
    }

    /**
     * Set a tile in the world.
     * <p>
     * Will only set the tile if the chunk exists cached.
     *
     * @param layerType The layer the tile is on.
     * @param x The x coordinate of the tile.
     * @param y The y coordinate of the tile.
     * @param tileType  The tile type to set.
     */
    public void setTile(LayerType layerType, int x, int y, TileType tileType) {
        int chunkX = CoordinateUtil.tileToChunk(x);
        int chunkY = CoordinateUtil.tileToChunk(y);
        Chunk chunk = this.chunkProvider.getChunkIfExists(chunkX, chunkY);
        if (chunk != null) {
            int relativeX = CoordinateUtil.tileToRelative(x);
            int relativeY = CoordinateUtil.tileToRelative(y);
            chunk.setTileAt(layerType, relativeX, relativeY, tileType);
        }
    }

    /**
     * Get the synchronization lock to use for game-object-related operations.
     *
     * @return The object to lock on.
     */
    public Object getGameObjectLock() {
        return gameObjectLock;
    }

    /**
     * Add a game object to this environment.
     * <p>
     * To move an existing game object between environments, use
     * {@link GameObject#moveToEnvironment(Environment, double, double)} instead.
     * <p>
     * The game object must reference this environment.
     *
     * @param gameObject The game object to add.
     * @see GameObject#moveToEnvironment(Environment, double, double)
     * @see GameObject#getEnvironment()
     */
    public void addNewGameObject(GameObject gameObject) {
        if (gameObject.getEnvironment() != this) {
            throw new IllegalArgumentException("The provided GameObject is not in this environment.");
        }
        synchronized (this.gameObjectLock) {
            if (!this.gameObjects.contains(gameObject)) {
                this.gameObjects.add(gameObject);
            }
        }
    }

    public List<GameObject> getGameObjects() {
        synchronized (this.gameObjectLock) {
            return new ArrayList<>(this.gameObjects);
        }
    }

    /**
     * Get the {@link AbstractMain} instance.
     *
     * @return The AbstractMain instance.
     */
    public AbstractMain getMain() {
        return this.main;
    }

    /**
     * Check whether this environment is processed locally on this device or a
     * remote server.
     * <p>
     * If this returns true, the user is connected to a server.
     *
     * @return {@code true} for remote, {@code false} for local.
     */
    public boolean isRemote() {
        // overridden in RemoteEnvironment
        return false;
    }

    /**
     * Tick this environment, which will tick all game objects.
     * <p>
     * This is a logic tick.
     */
    @Override
    public void tick() {
        boolean isServerSide = this.main.isServerSide();
        synchronized (this.gameObjectLock) {
            for (GameObject gameObject : this.gameObjects) {
                gameObject.tick();

                if (isServerSide) {
                    if (gameObject.getTracker().hasChanged()) {
                        // The game object has moved, let's send a packet to update
                        // its position and direction.
                        ((ServerEnvironment) this).sendPacketToAllExcept(new ToClientMovePacket(gameObject), gameObject);
                    }
                    gameObject.getTracker().update();
                }
            }

            this.gameObjects.removeIf(gameObject -> {
                if (gameObject.isRemoved() || gameObject.getEnvironment() != this) {
                    if (isServerSide) {
                        ((ServerEnvironment) this).sendPacketToAllExcept(new ToClientRemoveGameObjectPacket(gameObject.getId()), gameObject);
                    }
                    return true;
                }
                return false;
            });
        }

        if (!this.isRemote()) {
            if (System.currentTimeMillis() - this.lastSave > 60000) {
                this.main.getStorage().addToQueue(new SaveEnvironmentRequest(this));
                this.lastSave = System.currentTimeMillis();
            }
        }
    }

    /**
     * Client tick this environment, which will client tick all game objects.
     * <p>
     * This is a client tick,
     * @see PlayingScreen#clientTick()
     */
    public void clientTick() {
        synchronized (this.gameObjectLock) {
            for (GameObject gameObject : this.gameObjects) {
                gameObject.clientTick();
            }
        }
    }
}

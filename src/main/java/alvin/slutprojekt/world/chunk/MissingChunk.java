package alvin.slutprojekt.world.chunk;

import alvin.slutprojekt.world.Environment;

import java.util.concurrent.CompletableFuture;

/**
 * An object created for a missing chunk that is being provided asynchronously.
 */
public class MissingChunk implements ChunkCoordinate {
    private final Environment environment;
    private final int chunkX;
    private final int chunkY;
    private final CompletableFuture<Chunk> future;
    private boolean wasCreated = false;

    /**
     * Create a new MissingChunk instance.
     *
     * @param environment The environment where the chunk should be.
     * @param chunkX The x coordinate of the chunk.
     * @param chunkY The y coordinate of the chunk.
     * @param future The future that should complete when the chunk is done.
     */
    public MissingChunk(Environment environment, int chunkX, int chunkY, CompletableFuture<Chunk> future) {
        this.environment = environment;
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.future = future;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkY() {
        return chunkY;
    }

    public CompletableFuture<Chunk> getFuture() {
        return future;
    }

    /**
     * Whether this missing chunk has been created and is no longer missing.
     *
     * @return Whether this missing chunk was created.
     */
    public boolean wasCreated() {
        return wasCreated;
    }

    /**
     * Mark this missing chunk as create / no longer missing.
     * @see #wasCreated()
     */
    public void markAsCreated() {
        this.wasCreated = true;
    }
}

package alvin.slutprojekt.world.chunk;

import alvin.slutprojekt.world.Environment;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class ChunkProvider {
    protected Environment environment;
    private final ChunkList<Chunk> chunks = new ChunkList<>();
    private final ChunkList<MissingChunk> missingChunks = new ChunkList<>();

    /**
     * Get the chunk at the chunk coordinates, if it exists, otherwise return null.
     *
     * @param chunkX The chunk x coordinate.
     * @param chunkY The chunk y coordinate.
     * @return The chunk, or null.
     */
    public synchronized Chunk getChunkIfExists(int chunkX, int chunkY) {
        return chunks.get(chunkX, chunkY);
    }

    /**
     * Get the chunk at the chunk coordinates, if the chunk did not exist cached it
     * will be fetched from the server or generated depending on the implementation.
     *
     * @param chunkX The chunk x coordinate.
     * @param chunkY The chunk y coordinate.
     * @return A future that completes with the chunk.
     */
    public CompletableFuture<Chunk> getChunk(int chunkX, int chunkY) {
        Chunk chunk = getChunkIfExists(chunkX, chunkY);
        if (chunk != null) {
            return CompletableFuture.completedFuture(chunk);
        }
        return this.handleMissingChunk(chunkX, chunkY);
    }

    /**
     * Handle a missing chunk, calling {@link #provideMissingChunk(int, int)} and adding the
     * result to the chunks list when done.
     *
     * @param chunkX The chunk x coordinate.
     * @param chunkY The chunk y coordinate.
     * @return A future that completes with the chunk.
     */
    private synchronized CompletableFuture<Chunk> handleMissingChunk(int chunkX, int chunkY) {
        // If the chunk is already being provided, return that future
        MissingChunk missingChunk = this.missingChunks.get(chunkX, chunkY);
        if (missingChunk != null) {
            return missingChunk.getFuture();
        }

        // Otherwise, request it and add to the list of missing chunks.
        CompletableFuture<Chunk> future = this.provideMissingChunk(chunkX, chunkY);
        MissingChunk missingChunk1 = new MissingChunk(environment, chunkX, chunkY, future);
        this.missingChunks.add(missingChunk1);
        future.thenAccept(c -> {
            synchronized (ChunkProvider.this) {
                if (this.chunks.get(c.getChunkX(), c.getChunkY()) != null) {
                    throw new RuntimeException("Chunk already exists! " + c.getChunkX() + " " + c.getChunkY());
                }
                this.chunks.add(c);
                missingChunk1.markAsCreated();
                this.missingChunks.removeIf(MissingChunk::wasCreated);
            }
        });
        return future;
    }

    /**
     * This method is called in {@link #getChunk(int, int)} when a chunk that was not
     * cached was requested. It is the job of this method to provide that chunk.
     *
     * @param chunkX The chunk x coordinate.
     * @param chunkY The chunk y coordinate.
     * @return A future that completes with the chunk.
     */
    protected abstract CompletableFuture<Chunk> provideMissingChunk(int chunkX, int chunkY);

    /**
     * Request that a chunk at the specified coordinates should be provided.
     * <p>
     * This should only be called for chunks that do not exist, and this will
     * not be verified by this method, so it is the caller's responsibility to
     * not call this method for already existing chunks.
     *
     * @param chunkX The chunk x coordinate.
     * @param chunkY The chunk y coordinate.
     */
    public void requestMissingChunk(int chunkX, int chunkY) {
        this.handleMissingChunk(chunkX, chunkY);
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * Get a missing chunk by coordinates.
     * <p>
     * This method is protected and should only be used by subclasses for internal use.
     *
     * @param chunkX The chunk x coordinate.
     * @param chunkY The chunk y coordinate.
     * @return The missing chunk.
     */
    protected MissingChunk getMissingChunk(int chunkX, int chunkY) {
        return this.missingChunks.get(chunkX, chunkY);
    }

    /**
     * Get a list of all loaded chunks.
     *
     * @return The list.
     */
    public synchronized List<Chunk> getLoadedChunks() {
        return this.chunks.getCopy();
    }
}

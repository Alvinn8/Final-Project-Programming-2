package alvin.slutprojekt.world.chunk;

import alvin.slutprojekt.storage.LoadChunkRequest;
import alvin.slutprojekt.storage.Storage;
import alvin.slutprojekt.world.Environment;
import alvin.slutprojekt.world.World;
import alvin.slutprojekt.world.generation.GeneratingChunk;
import alvin.slutprojekt.world.generation.TerrainGenerationThread;
import alvin.slutprojekt.world.generation.TerrainGenerator;

import java.util.concurrent.CompletableFuture;

/**
 * A {@link ChunkProvider} that will first try to load a chunk, and if it didn't
 * exist it will generate it.
 *
 * @see Storage#loadChunk(World, Environment, int, int)
 * @see TerrainGenerator
 */
public class MainChunkProvider extends ChunkProvider {
    private final Storage storage;
    private final TerrainGenerator terrainGenerator;
    private final TerrainGenerationThread terrainGenerationThread;

    public MainChunkProvider(Storage storage, TerrainGenerator terrainGenerator, TerrainGenerationThread terrainGenerationThread) {
        this.storage = storage;
        this.terrainGenerator = terrainGenerator;
        this.terrainGenerationThread = terrainGenerationThread;
    }

    @Override
    protected CompletableFuture<Chunk> provideMissingChunk(int chunkX, int chunkY) {
        CompletableFuture<Chunk> future = new CompletableFuture<>();

        CompletableFuture<Chunk> loadFuture = new CompletableFuture<>();
        storage.addToQueue(new LoadChunkRequest(environment, chunkX, chunkY, loadFuture));
        loadFuture.thenAccept(loadedChunk -> {
            if (loadedChunk != null) {
                future.complete(loadedChunk);
            } else {
                // The chunk did not exist on disk, generate it instead
                CompletableFuture<Chunk> generateFuture = new CompletableFuture<>();
                GeneratingChunk generatingChunk = new GeneratingChunk(this.environment, this.terrainGenerator, chunkX, chunkY, generateFuture);
                this.terrainGenerationThread.addToQueue(generatingChunk);
                generateFuture.thenAccept(future::complete);
            }
        });

        return future;
    }
}

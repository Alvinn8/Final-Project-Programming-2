package alvin.slutprojekt.world.generation;

import alvin.slutprojekt.world.Environment;
import alvin.slutprojekt.world.chunk.Chunk;
import alvin.slutprojekt.world.chunk.MissingChunk;

import java.util.concurrent.CompletableFuture;

/**
 * An object created when a chunk is being generated.
 */
public class GeneratingChunk extends MissingChunk {
    private final TerrainGenerator generator;

    /**
     * Create a new GeneratingChunk instance.
     *
     * @param environment The environment where the chunk should be.
     * @param generator The generator that should generate this chunk.
     * @param chunkX The x coordinate of the chunk.
     * @param chunkY The y coordinate of the chunk.
     * @param future The future that should complete when the chunk is done.
     */
    public GeneratingChunk(Environment environment, TerrainGenerator generator, int chunkX, int chunkY, CompletableFuture<Chunk> future) {
        super(environment, chunkX, chunkY, future);
        this.generator = generator;
    }

    public TerrainGenerator getGenerator() {
        return generator;
    }
}

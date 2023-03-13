package alvin.slutprojekt.world.generation;

import alvin.slutprojekt.world.chunk.Chunk;

/**
 * Generates terrain for an environment.
 * <p>
 * The {@link #generateChunk(GeneratingChunk)} method will be called from the
 * {@link TerrainGenerationThread}, so implementations should avoid accessing
 * lists and maps to avoid a {@link java.util.ConcurrentModificationException}.
 */
public interface TerrainGenerator {
    Chunk generateChunk(GeneratingChunk generatingChunk);
}

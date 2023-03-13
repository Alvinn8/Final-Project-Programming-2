package alvin.slutprojekt.world.chunk;

/**
 * Something that holds chunk coordinates.
 * @see Chunk
 * @see MissingChunk
 */
public interface ChunkCoordinate {

    /**
     * Get the x chunk coordinate.
     *
     * @return The x chunk coordinate.
     */
    int getChunkX();

    /**
     * Get the y chunk coordinate.
     *
     * @return The y chunk coordinate.
     */
    int getChunkY();

}

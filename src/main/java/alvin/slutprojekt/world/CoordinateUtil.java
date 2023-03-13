package alvin.slutprojekt.world;

import alvin.slutprojekt.world.chunk.Chunk;

public class CoordinateUtil {
    private CoordinateUtil() {}

    /**
     * Get the chunk coordinate from a tile coordinate (relative to the world origin).
     * <p>
     * This can be used to get the chunk a tile is located in.
     *
     * @param worldTileCoordinate The tile coordinate relative to the world origin.
     * @return The chunk coordinate.
     */
    public static int tileToChunk(int worldTileCoordinate) {
        // floorDiv is like a normal division (/), but works better with
        // negative numbers for our use case.
        return Math.floorDiv(worldTileCoordinate, Chunk.CHUNK_TILE_SIZE);
    }

    /**
     * Get the coordinate in the chunk for a tile, aka the tile coordinate relative to
     * the chunk.
     * <p>
     * This can be used to get the relative coordinate inside a chunk where the tile is
     * located.
     *
     * @param worldTileCoordinate The tile coordinate relative to the world origin.
     * @return The coordinate relative to the chunk.
     */
    public static int tileToRelative(int worldTileCoordinate) {
        // Like a normal modulo (%), but works better with negative
        // numbers for our use case.
        return Math.floorMod(worldTileCoordinate, Chunk.CHUNK_TILE_SIZE);
    }

    /**
     * Get the coordinate relative to the world from a coordinate relative to a chunk.
     *
     * @param relative The relative coordinate within the chunk.
     * @param chunk The chunk coordinate.
     * @return The tile coordinate relative to the world origin.
     */
    public static int relativeToTile(int relative, int chunk) {
        return relative + chunk * Chunk.CHUNK_TILE_SIZE;
    }
}

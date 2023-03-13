package alvin.slutprojekt.world.chunk;

import alvin.slutprojekt.Grid;
import alvin.slutprojekt.Registry;
import alvin.slutprojekt.world.Environment;
import alvin.slutprojekt.world.LayerType;
import alvin.slutprojekt.world.tile.TileType;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A chunk contains a region of tiles in an {@link Environment}.
 * <p>
 * Each axis holds {@link #CHUNK_TILE_SIZE} tiles.
 */
public class Chunk implements ChunkCoordinate {
    /**
     * The amount of tiles in a chunk for each axis.
     */
    public static final int CHUNK_TILE_SIZE = 16;

    private final Environment environment;
    private final int chunkX;
    private final int chunkY;
    private final Grid<TileType> backgroundLayer = new Grid<>(CHUNK_TILE_SIZE, CHUNK_TILE_SIZE);
    private final Grid<TileType> mainLayer = new Grid<>(CHUNK_TILE_SIZE, CHUNK_TILE_SIZE);
    private boolean hasChanges = false;

    public Chunk(Environment environment, int chunkX, int chunkY) {
        this.environment = environment;
        this.chunkX = chunkX;
        this.chunkY = chunkY;
    }

    /**
     * Get the tile at the specified layer and coordinates.
     *
     * @param layerType The layer to get the tile from.
     * @param x The x coordinate relative to this chunk.
     * @param y The y coordinate relative to this chunk.
     * @return The tile, or null.
     * @see alvin.slutprojekt.world.CoordinateUtil#tileToRelative(int)
     */
    public synchronized TileType getTileAt(LayerType layerType, int x, int y) {
        switch (layerType) {
            case BACKGROUND: return backgroundLayer.get(x, y);
            case MAIN: return mainLayer.get(x, y);
            default: throw new IllegalArgumentException("Invalid layer type " + layerType);
        }
    }

    /**
     * Set the tile at the specified layer and coordinates.
     *
     * @param layerType The layer to set the tile at.
     * @param x The x coordinate relative to this chunk.
     * @param y The y coordinate relative to this chunk.
     * @param tileType The tile to set.
     * @see alvin.slutprojekt.world.CoordinateUtil#tileToRelative(int)
     */
    public synchronized void setTileAt(LayerType layerType, int x, int y, TileType tileType) {
        switch (layerType) {
            case BACKGROUND: backgroundLayer.set(x, y, tileType); break;
            case MAIN: mainLayer.set(x, y, tileType); break;
            default: throw new IllegalArgumentException("Invalid layer type " + layerType);
        }
        this.hasChanges = true;
    }

    /**
     * Get the x tile coordinate that this chunk starts at.
     * <p>
     * Aka get the x tile coordinate that the (0,0) tile in this chunk is at.
     *
     * @return The y coordinate.
     */
    public int getTileStartX() {
        return this.chunkX * CHUNK_TILE_SIZE;
    }

    /**
     * Get the y tile coordinate that this chunk starts at.
     * <p>
     * Aka get the y tile coordinate that the (0,0) tile in this chunk is at.
     *
     * @return The x coordinate.
     */
    public int getTileStartY() {
        return this.chunkY * CHUNK_TILE_SIZE;
    }

    /**
     * Write the tiles in this chunk to a stream.
     *
     * @return The bytes for the tiles in this chunk.
     * @throws IOException If an error occurs while writing.
     */
    public synchronized byte[] writeToBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(out);

        for (int x = 0; x < CHUNK_TILE_SIZE; x++) {
            for (int y = 0; y < CHUNK_TILE_SIZE; y++) {
                TileType backgroundTile = this.backgroundLayer.get(x, y);
                if (backgroundTile == null) {
                    stream.writeUTF("");
                } else {
                    stream.writeUTF(backgroundTile.getId());
                }

                TileType tile = this.mainLayer.get(x, y);
                if (tile == null) {
                    stream.writeUTF("");
                } else {
                    stream.writeUTF(tile.getId());
                }
            }
        }
        return out.toByteArray();
    }

    /**
     * Read chunk data from a stream and put the tiles in this chunk.
     *
     * @param inputStream The input stream containing the serialized bytes.
     * @throws IOException If an error occurs while reading.
     */
    public void read(InputStream inputStream) throws IOException {
        DataInputStream stream = new DataInputStream(inputStream);

        Registry<TileType> registry = this.environment.getMain().getTileTypeRegistry();

        for (int x = 0; x < CHUNK_TILE_SIZE; x++) {
            for (int y = 0; y < CHUNK_TILE_SIZE; y++) {
                String backgroundTileTypeId = stream.readUTF();
                if (!"".equals(backgroundTileTypeId)) {
                    TileType tileType = registry.get(backgroundTileTypeId);
                    if (tileType != null) {
                        this.backgroundLayer.set(x, y, tileType);
                    }
                }

                String tileTypeId = stream.readUTF();
                if (!"".equals(tileTypeId)) {
                    TileType tileType = registry.get(tileTypeId);
                    if (tileType != null) {
                        this.mainLayer.set(x, y, tileType);
                    }
                }
            }
        }
    }

    /**
     * Get the x chunk coordinate.
     *
     * @return The x chunk coordinate.
     */
    public int getChunkX() {
        return this.chunkX;
    }

    /**
     * Get the y chunk coordinate.
     *
     * @return The y chunk coordinate.
     */
    public int getChunkY() {
        return this.chunkY;
    }

    /**
     * Get the background layer where the ground tiles are stored.
     *
     * @return The background layer.
     */
    public synchronized Grid<TileType> getBackgroundLayer() {
        return this.backgroundLayer;
    }

    /**
     * Get the main layer where breakable tiles are stored.
     *
     * @return The main layer.
     */
    public synchronized Grid<TileType> getMainLayer() {
        return this.mainLayer;
    }

    /**
     * Check whether this chunk has had changes to its tiles after its creation.
     *
     * @return Whether there have been changes.
     */
    public boolean hasChanges() {
        return hasChanges;
    }

    /**
     * Mark that this chunk has been saved.
     * <p>
     * This will make {@link #hasChanges()} return false unless further changes
     * are made.
     */
    public void markAsSaved() {
        this.hasChanges = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chunk chunk = (Chunk) o;

        if (chunkX != chunk.chunkX) return false;
        return chunkY == chunk.chunkY;
    }

    @Override
    public int hashCode() {
        int result = chunkX;
        result = 31 * result + chunkY;
        return result;
    }

    @Override
    public String toString() {
        return "Chunk{" +
                "chunkX=" + chunkX +
                ", chunkY=" + chunkY +
                '}';
    }
}

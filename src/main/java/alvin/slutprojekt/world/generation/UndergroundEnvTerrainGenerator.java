package alvin.slutprojekt.world.generation;

import alvin.slutprojekt.noise.Noise;
import alvin.slutprojekt.world.chunk.Chunk;
import alvin.slutprojekt.world.tile.TileType;
import alvin.slutprojekt.world.tile.TileTypes;

public class UndergroundEnvTerrainGenerator implements TerrainGenerator {
    private final Noise noise;

    public UndergroundEnvTerrainGenerator(long seed) {
        this.noise = new Noise(seed);
    }

    @Override
    public Chunk generateChunk(GeneratingChunk generatingChunk) {
        TileTypes tileTypes = generatingChunk.getEnvironment().getMain().getTileTypes();
        Chunk chunk = new Chunk(generatingChunk.getEnvironment(), generatingChunk.getChunkX(), generatingChunk.getChunkY());
        int tileStartX = chunk.getTileStartX();
        int tileStartY = chunk.getTileStartY();
        for (int x = 0; x < Chunk.CHUNK_TILE_SIZE; x++) {
            for (int y = 0; y < Chunk.CHUNK_TILE_SIZE; y++) {
                int tileX = tileStartX + x;
                int tileY = tileStartY + y;
                TileType background = tileTypes.BACKGROUND_STONE;
                TileType main = tileTypes.STONE;

                double random = this.noise.noise(tileX * 2 + 0.5, tileY * 2 + 0.5);
                if      (random < 0.05) main = tileTypes.COAL_ORE;
                else if (random < 0.10) main = tileTypes.IRON_ORE;
                else if (random < 0.15) main = tileTypes.COPPER_ORE;
                else if (random < 0.25) main = tileTypes.BAUXITE;

                chunk.getBackgroundLayer().set(x, y, background);
                chunk.getMainLayer().set(x, y, main);
            }
        }
        return chunk;
    }
}

package alvin.slutprojekt.world.generation;

import alvin.slutprojekt.noise.Noise;
import alvin.slutprojekt.world.chunk.Chunk;
import alvin.slutprojekt.world.tile.TileType;
import alvin.slutprojekt.world.tile.TileTypes;

/**
 * A terrain generator for the main environment.
 */
public class MainEnvTerrainGenerator implements TerrainGenerator {
    private final Noise noise;

    public MainEnvTerrainGenerator(long seed) {
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
                TileType background;
                TileType main = null;

                // River / pond / ocean / water
                double riverNoise = this.noise.noise(tileX * 0.01, tileY * 0.01);
                if (riverNoise < 0.3) {
                    if (riverNoise > 0.28) {
                        double sandOrGravel = this.noise.noise(tileX * 0.005, tileY * 0.05);
                        if (sandOrGravel > 0.5) {
                            background = tileTypes.GRAVEL;
                        } else {
                            background = tileTypes.SAND;
                        }
                    } else {
                        background = tileTypes.WATER;
                    }
                }
                // Forest
                else if (this.noise.noise(tileX * 0.02 + 100, tileY * 0.02 + 200) > 0.5) {
                    background = tileTypes.GRASS;
                    double treeFrequency = this.noise.noise(tileX * 0.01 + 500, tileY * 0.01 + 700);
                    if (this.noise.noise(tileX * 2 + 0.5, tileY * 2 + 0.5) > treeFrequency) {
                        main = tileTypes.TREE;
                    }
                }
                // Flat, grass
                else {
                    if (this.noise.noise(tileX * 0.05, tileY * 0.05) < 0.6) {
                        background = tileTypes.GRASS;
                    } else {
                        background = tileTypes.GRASS_WITH_ROCKS;
                    }
                }

                chunk.getBackgroundLayer().set(x, y, background);
                if (main != null) {
                    chunk.getMainLayer().set(x, y, main);
                }
            }
        }
        return chunk;
    }
}

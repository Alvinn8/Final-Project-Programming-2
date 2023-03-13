package alvin.slutprojekt.storage;

import alvin.slutprojekt.world.Environment;
import alvin.slutprojekt.world.World;
import alvin.slutprojekt.world.chunk.Chunk;

import java.io.IOException;

/**
 * A request to save a chunk.
 */
public class SaveChunkRequest implements StorageRequest {
    private final World world;
    private final Environment environment;
    private final Chunk chunk;

    public SaveChunkRequest(World world, Environment environment, Chunk chunk) {
        this.world = world;
        this.environment = environment;
        this.chunk = chunk;
    }

    @Override
    public void execute(Storage storage) {
        try {
            storage.saveChunk(this.world, this.environment, this.chunk);
        } catch (IOException e) {
            System.err.println("Failed to save chunk " + chunk);
            e.printStackTrace();
        }
    }
}

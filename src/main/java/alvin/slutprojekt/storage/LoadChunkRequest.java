package alvin.slutprojekt.storage;

import alvin.slutprojekt.world.Environment;
import alvin.slutprojekt.world.chunk.Chunk;

import java.util.concurrent.CompletableFuture;

public class LoadChunkRequest implements StorageRequest {
    private final Environment environment;
    private final int chunkX;
    private final int chunkY;
    private final CompletableFuture<Chunk> future;

    public LoadChunkRequest(Environment environment, int chunkX, int chunkY, CompletableFuture<Chunk> future) {
        this.environment = environment;
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.future = future;
    }

    @Override
    public void execute(Storage storage) {
        Chunk chunk = storage.loadChunk(environment.getWorld(), environment, chunkX, chunkY);
        future.complete(chunk);
    }
}

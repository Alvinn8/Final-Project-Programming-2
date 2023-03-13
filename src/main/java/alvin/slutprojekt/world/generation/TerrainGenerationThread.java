package alvin.slutprojekt.world.generation;

import alvin.slutprojekt.world.chunk.Chunk;

import java.util.concurrent.LinkedBlockingQueue;

public class TerrainGenerationThread implements Runnable {
    private final Thread thread;
    private final LinkedBlockingQueue<GeneratingChunk> queue = new LinkedBlockingQueue<>();

    public TerrainGenerationThread() {
        this.thread = new Thread(this, "Terrain Generation Thread");
        this.thread.start();
    }

    /**
     * Add a chunk to generate.
     *
     * @param generatingChunk The {@link GeneratingChunk} instance.
     */
    public void addToQueue(GeneratingChunk generatingChunk) {
        this.queue.add(generatingChunk);
    }

    public Thread getThread() {
        return thread;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                GeneratingChunk generatingChunk = this.queue.take();
                // Thread.sleep(5000);
                Chunk chunk = generatingChunk.getGenerator().generateChunk(generatingChunk);
                generatingChunk.markAsCreated();
                generatingChunk.getFuture().complete(chunk);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

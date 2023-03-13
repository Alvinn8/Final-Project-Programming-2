package alvin.slutprojekt.client.server;

import alvin.slutprojekt.networking.packet.ToClientChunkPacket;
import alvin.slutprojekt.networking.packet.ToServerRequestChunkPacket;
import alvin.slutprojekt.world.chunk.Chunk;
import alvin.slutprojekt.world.chunk.ChunkProvider;
import alvin.slutprojekt.world.chunk.MissingChunk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;

/**
 * A {@link ChunkProvider} that asks the remote server for missing chunks.
 */
public class RemoteChunkProvider extends ChunkProvider {
    private ServerConnection serverConnection;

    public void setServerConnection(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    @Override
    protected CompletableFuture<Chunk> provideMissingChunk(int chunkX, int chunkY) {
        CompletableFuture<Chunk> future = new CompletableFuture<>();
        this.serverConnection.sendPacket(new ToServerRequestChunkPacket(chunkX, chunkY));
        return future;
    }

    /**
     * Handle a packet with a chunk.
     *
     * @param packet The chunk packet.
     */
    public void handleChunkPacket(ToClientChunkPacket packet) {
        MissingChunk missingChunk = this.getMissingChunk(packet.getChunkX(), packet.getChunkY());
        Chunk chunk;
        if (missingChunk != null) {
            chunk = new Chunk(this.environment, packet.getChunkX(), packet.getChunkY());
            missingChunk.markAsCreated();
        } else {
            chunk = this.getChunkIfExists(packet.getChunkX(), packet.getChunkY());
            if (chunk == null) {
                return;
            }
        }
        try {
            chunk.read(new ByteArrayInputStream(packet.getData()));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read chunk data", e);
        }
        if (missingChunk != null) {
            missingChunk.getFuture().complete(chunk);
        }
    }
}

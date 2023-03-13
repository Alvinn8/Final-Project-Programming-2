package alvin.slutprojekt.networking.packet;

import alvin.slutprojekt.networking.PacketType;
import alvin.slutprojekt.networking.ToServerPacket;
import alvin.slutprojekt.server.ServerSidePacketHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A packet sent to the server to request a chunk.
 * <p>
 * The server should reply with a {@link ToClientChunkPacket}.
 */
public class ToServerRequestChunkPacket implements ToServerPacket {
    public static final PacketType<ToServerPacket> TYPE = new PacketType<>("request_chunk", ToServerRequestChunkPacket::new);

    private final int chunkX;
    private final int chunkY;

    public ToServerRequestChunkPacket(int chunkX, int chunkY) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
    }

    public ToServerRequestChunkPacket(DataInputStream stream) throws IOException {
        this.chunkX = stream.readInt();
        this.chunkY = stream.readInt();
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeInt(this.chunkX);
        stream.writeInt(this.chunkY);
    }

    @Override
    public PacketType<ToServerPacket> getType() {
        return TYPE;
    }

    @Override
    public void handle(ServerSidePacketHandler handler) {
        handler.handleChunkRequest(this);
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkY() {
        return this.chunkY;
    }
}

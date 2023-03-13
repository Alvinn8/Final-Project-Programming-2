package alvin.slutprojekt.networking.packet;

import alvin.slutprojekt.client.server.ClientSidePacketHandler;
import alvin.slutprojekt.networking.PacketType;
import alvin.slutprojekt.networking.ToClientPacket;
import alvin.slutprojekt.world.chunk.Chunk;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A packet sent to the client with chunk data of tiles.
 */
public class ToClientChunkPacket implements ToClientPacket {
    public static final PacketType<ToClientPacket> TYPE = new PacketType<>("chunks", ToClientChunkPacket::new);

    private final int chunkX;
    private final int chunkY;
    private final byte[] data;

    public ToClientChunkPacket(Chunk chunk) throws IOException {
        this.chunkX = chunk.getChunkX();
        this.chunkY = chunk.getChunkY();
        this.data = chunk.writeToBytes();
    }

    public ToClientChunkPacket(DataInputStream stream) throws IOException {
        this.chunkX = stream.readInt();
        this.chunkY = stream.readInt();

        int size = stream.readInt();
        this.data = new byte[size];
        stream.readFully(this.data);
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeInt(this.chunkX);
        stream.writeInt(this.chunkY);

        stream.writeInt(this.data.length);
        stream.write(this.data);
    }

    @Override
    public PacketType<ToClientPacket> getType() {
        return TYPE;
    }

    @Override
    public void handle(ClientSidePacketHandler handler) {
        handler.handleChunk(this);
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkY() {
        return this.chunkY;
    }

    public byte[] getData() {
        return this.data;
    }
}

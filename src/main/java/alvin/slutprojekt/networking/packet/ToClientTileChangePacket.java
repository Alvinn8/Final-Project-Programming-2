package alvin.slutprojekt.networking.packet;

import alvin.slutprojekt.client.server.ClientSidePacketHandler;
import alvin.slutprojekt.networking.PacketType;
import alvin.slutprojekt.networking.ToClientPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ToClientTileChangePacket implements ToClientPacket {
    public static final PacketType<ToClientPacket> TYPE = new PacketType<>("tile_change", ToClientTileChangePacket::new);

    private final int x;
    private final int y;
    private final String tileId;

    public ToClientTileChangePacket(int x, int y, String tileId) {
        this.x = x;
        this.y = y;
        this.tileId = tileId;
    }

    public ToClientTileChangePacket(DataInputStream stream) throws IOException {
        this.x = stream.readInt();
        this.y = stream.readInt();
        this.tileId = stream.readUTF();
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeInt(this.x);
        stream.writeInt(this.y);
        stream.writeUTF(this.tileId);
    }

    @Override
    public PacketType<ToClientPacket> getType() {
        return TYPE;
    }

    @Override
    public void handle(ClientSidePacketHandler handler) {
        handler.handleTileChange(this);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getTileId() {
        return tileId;
    }
}

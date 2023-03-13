package alvin.slutprojekt.networking.packet;

import alvin.slutprojekt.networking.PacketType;
import alvin.slutprojekt.networking.ToServerPacket;
import alvin.slutprojekt.server.ServerSidePacketHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Notify the server that a tile was broken by the user.
 */
public class ToServerBreakPacket implements ToServerPacket {
    public static final PacketType<ToServerPacket> TYPE = new PacketType<>("break", ToServerBreakPacket::new);

    private final int targetTileX;
    private final int targetTileY;

    public ToServerBreakPacket(int targetTileX, int targetTileY) {
        this.targetTileX = targetTileX;
        this.targetTileY = targetTileY;
    }

    public ToServerBreakPacket(DataInputStream stream) throws IOException {
        this.targetTileX = stream.readInt();
        this.targetTileY = stream.readInt();
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeInt(this.targetTileX);
        stream.writeInt(this.targetTileY);
    }

    @Override
    public PacketType<ToServerPacket> getType() {
        return TYPE;
    }

    @Override
    public void handle(ServerSidePacketHandler handler) {
        handler.handleBreak(this);
    }

    public int getTargetTileX() {
        return targetTileX;
    }

    public int getTargetTileY() {
        return targetTileY;
    }
}

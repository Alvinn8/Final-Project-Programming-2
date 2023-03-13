package alvin.slutprojekt.networking.packet;

import alvin.slutprojekt.networking.PacketType;
import alvin.slutprojekt.networking.ToServerPacket;
import alvin.slutprojekt.server.ServerSidePacketHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Notify the server that a tile was placed by the user.
 */
public class ToServerPlacePacket implements ToServerPacket {
    public static final PacketType<ToServerPacket> TYPE = new PacketType<>("place", ToServerPlacePacket::new);

    private final int targetTileX;
    private final int targetTileY;
    private final String tileTypeId;

    public ToServerPlacePacket(int targetTileX, int targetTileY, String tileTypeId) {
        this.targetTileX = targetTileX;
        this.targetTileY = targetTileY;
        this.tileTypeId = tileTypeId;
    }

    public ToServerPlacePacket(DataInputStream stream) throws IOException {
        this.targetTileX = stream.readInt();
        this.targetTileY = stream.readInt();
        this.tileTypeId = stream.readUTF();
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeInt(this.targetTileX);
        stream.writeInt(this.targetTileY);
        stream.writeUTF(this.tileTypeId);
    }

    @Override
    public PacketType<ToServerPacket> getType() {
        return TYPE;
    }

    @Override
    public void handle(ServerSidePacketHandler handler) {
        handler.handlePlace(this);
    }

    public int getTargetTileX() {
        return targetTileX;
    }

    public int getTargetTileY() {
        return targetTileY;
    }

    public String getTileTypeId() {
        return tileTypeId;
    }
}

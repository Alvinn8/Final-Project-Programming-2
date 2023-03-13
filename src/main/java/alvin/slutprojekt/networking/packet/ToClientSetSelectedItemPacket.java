package alvin.slutprojekt.networking.packet;

import alvin.slutprojekt.client.server.ClientSidePacketHandler;
import alvin.slutprojekt.networking.PacketType;
import alvin.slutprojekt.networking.ToClientPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ToClientSetSelectedItemPacket implements ToClientPacket {
    public static final PacketType<ToClientPacket> TYPE = new PacketType<>("set_selected_item", ToClientSetSelectedItemPacket::new);

    private final int playerId;
    private final String tileTypeId;

    public ToClientSetSelectedItemPacket(int playerId, String tileTypeId) {
        this.playerId = playerId;
        this.tileTypeId = tileTypeId;
    }

    public ToClientSetSelectedItemPacket(DataInputStream stream) throws IOException {
        this.playerId = stream.readInt();
        this.tileTypeId = stream.readUTF();
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeInt(this.playerId);
        stream.writeUTF(this.tileTypeId);
    }

    @Override
    public PacketType<ToClientPacket> getType() {
        return TYPE;
    }

    @Override
    public void handle(ClientSidePacketHandler handler) {
        handler.handleSetSelectedItem(this);
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getTileTypeId() {
        return tileTypeId;
    }
}

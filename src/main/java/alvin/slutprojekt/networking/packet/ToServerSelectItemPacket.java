package alvin.slutprojekt.networking.packet;

import alvin.slutprojekt.networking.PacketType;
import alvin.slutprojekt.networking.ToServerPacket;
import alvin.slutprojekt.server.ServerSidePacketHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ToServerSelectItemPacket implements ToServerPacket {
    public static final PacketType<ToServerPacket> TYPE = new PacketType<>("select_item", ToServerSelectItemPacket::new);

    private final int index;

    public ToServerSelectItemPacket(int index) {
        this.index = index;
    }

    public ToServerSelectItemPacket(DataInputStream stream) throws IOException {
        this.index = stream.readInt();
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeInt(this.index);
    }

    @Override
    public PacketType<ToServerPacket> getType() {
        return TYPE;
    }

    @Override
    public void handle(ServerSidePacketHandler handler) {
        handler.handleSelectItem(this);
    }

    public int getIndex() {
        return index;
    }
}

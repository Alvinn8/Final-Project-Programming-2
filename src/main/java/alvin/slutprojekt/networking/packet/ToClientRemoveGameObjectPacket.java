package alvin.slutprojekt.networking.packet;

import alvin.slutprojekt.client.server.ClientSidePacketHandler;
import alvin.slutprojekt.networking.PacketType;
import alvin.slutprojekt.networking.ToClientPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ToClientRemoveGameObjectPacket implements ToClientPacket {
    public static final PacketType<ToClientPacket> TYPE = new PacketType<>("remove_game_object", ToClientRemoveGameObjectPacket::new);

    private final int id;

    public ToClientRemoveGameObjectPacket(int id) {
        this.id = id;
    }

    public ToClientRemoveGameObjectPacket(DataInputStream stream) throws IOException {
        this.id = stream.readInt();
    }


    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeInt(this.id);
    }

    @Override
    public PacketType<ToClientPacket> getType() {
        return TYPE;
    }

    @Override
    public void handle(ClientSidePacketHandler handler) {
        handler.handleRemoveGameObject(this);
    }

    public int getId() {
        return id;
    }
}

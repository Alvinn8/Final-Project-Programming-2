package alvin.slutprojekt.networking.packet;

import alvin.slutprojekt.networking.PacketType;
import alvin.slutprojekt.networking.ToServerPacket;
import alvin.slutprojekt.server.ServerSidePacketHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ToServerCreatePacket implements ToServerPacket {
    public static final PacketType<ToServerPacket> TYPE = new PacketType<>("create", ToServerCreatePacket::new);

    private final String createRecipeId;

    public ToServerCreatePacket(String createRecipeId) {
        this.createRecipeId = createRecipeId;
    }

    public ToServerCreatePacket(DataInputStream stream) throws IOException {
        this.createRecipeId = stream.readUTF();
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeUTF(this.createRecipeId);
    }

    @Override
    public PacketType<ToServerPacket> getType() {
        return TYPE;
    }

    @Override
    public void handle(ServerSidePacketHandler handler) {
        handler.handleCreate(this);
    }

    public String getCreateRecipeId() {
        return createRecipeId;
    }
}

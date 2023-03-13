package alvin.slutprojekt.networking.packet;

import alvin.slutprojekt.client.server.ClientSidePacketHandler;
import alvin.slutprojekt.networking.PacketType;
import alvin.slutprojekt.networking.ToClientPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A packet that tells the client that they are disconnected.
 */
public class ToClientDisconnectPacket implements ToClientPacket {
    public static final PacketType<ToClientPacket> TYPE = new PacketType<>("disconnect", ToClientDisconnectPacket::new);

    private final String message;

    public ToClientDisconnectPacket(String message) {
        this.message = message;
    }

    public ToClientDisconnectPacket(DataInputStream stream) throws IOException {
        this.message = stream.readUTF();
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeUTF(this.message == null ? "null" : this.message);
    }

    @Override
    public void handle(ClientSidePacketHandler handler) {
        handler.handleDisconnect(this);
    }

    @Override
    public PacketType<ToClientPacket> getType() {
        return TYPE;
    }

    public String getMessage() {
        return this.message;
    }
}

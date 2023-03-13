package alvin.slutprojekt.networking.packet;

import alvin.slutprojekt.client.server.ClientSidePacketHandler;
import alvin.slutprojekt.networking.PacketType;
import alvin.slutprojekt.networking.ToClientPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Sent to a client to notify them that they moved to a different environment.
 */
public class ToClientMoveToEnvironmentPacket implements ToClientPacket {
    public static final PacketType<ToClientPacket> TYPE = new PacketType<>("move_to_environment", ToClientMoveToEnvironmentPacket::new);

    private final String environmentId;
    private final double x;
    private final double y;

    public ToClientMoveToEnvironmentPacket(String environmentId, double x, double y) {
        this.environmentId = environmentId;
        this.x = x;
        this.y = y;
    }

    public ToClientMoveToEnvironmentPacket(DataInputStream stream) throws IOException {
        this.environmentId = stream.readUTF();
        this.x = stream.readDouble();
        this.y = stream.readDouble();
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeUTF(this.environmentId);
        stream.writeDouble(this.x);
        stream.writeDouble(this.y);
    }

    @Override
    public PacketType<ToClientPacket> getType() {
        return TYPE;
    }

    @Override
    public void handle(ClientSidePacketHandler handler) {
        handler.handleMoveToEnvironment(this);
    }

    public String getEnvironmentId() {
        return environmentId;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}

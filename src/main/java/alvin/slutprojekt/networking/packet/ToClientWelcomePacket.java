package alvin.slutprojekt.networking.packet;

import alvin.slutprojekt.client.server.ClientSidePacketHandler;
import alvin.slutprojekt.networking.PacketType;
import alvin.slutprojekt.networking.ToClientPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A packet send after the {@link ToServerHelloPacket} to show a successful
 * handshake, welcoming the client to the server.
 */
public class ToClientWelcomePacket implements ToClientPacket {
    public static final PacketType<ToClientPacket> TYPE = new PacketType<>("welcome", ToClientWelcomePacket::new);

    private final int playerId;
    private final double playerX;
    private final double playerY;

    public ToClientWelcomePacket(int playerId, double playerX, double playerY) {
        this.playerId = playerId;
        this.playerX = playerX;
        this.playerY = playerY;
    }

    public ToClientWelcomePacket(DataInputStream stream) throws IOException {
        this.playerId = stream.readInt();
        this.playerX = stream.readDouble();
        this.playerY = stream.readDouble();
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeInt(this.playerId);
        stream.writeDouble(this.playerX);
        stream.writeDouble(this.playerY);
    }

    @Override
    public PacketType<ToClientPacket> getType() {
        return TYPE;
    }

    @Override
    public void handle(ClientSidePacketHandler handler) {
        handler.handleWelcome(this);
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public double getPlayerX() {
        return this.playerX;
    }

    public double getPlayerY() {
        return this.playerY;
    }
}

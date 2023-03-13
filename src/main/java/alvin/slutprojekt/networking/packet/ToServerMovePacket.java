package alvin.slutprojekt.networking.packet;

import alvin.slutprojekt.client.UserPlayer;
import alvin.slutprojekt.networking.PacketType;
import alvin.slutprojekt.networking.ToServerPacket;
import alvin.slutprojekt.server.ServerSidePacketHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A packet from the client to the server notifying the server that the user has
 * moved.
 */
public class ToServerMovePacket implements ToServerPacket {
    public static final PacketType<ToServerPacket> TYPE = new PacketType<>("move", ToServerMovePacket::new);

    private final double x;
    private final double y;
    private final double directionRad;

    public ToServerMovePacket(UserPlayer player) {
        this.x = player.getX();
        this.y = player.getY();
        this.directionRad = player.getDirectionRad();
    }

    public ToServerMovePacket(DataInputStream stream) throws IOException {
        this.x = stream.readDouble();
        this.y = stream.readDouble();
        this.directionRad = stream.readDouble();
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeDouble(this.x);
        stream.writeDouble(this.y);
        stream.writeDouble(this.directionRad);
    }

    @Override
    public PacketType<ToServerPacket> getType() {
        return TYPE;
    }

    @Override
    public void handle(ServerSidePacketHandler handler) {
        handler.handleMove(this);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getDirectionRad() {
        return directionRad;
    }
}

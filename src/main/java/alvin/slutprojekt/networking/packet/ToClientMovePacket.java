package alvin.slutprojekt.networking.packet;

import alvin.slutprojekt.client.server.ClientSidePacketHandler;
import alvin.slutprojekt.networking.PacketType;
import alvin.slutprojekt.networking.ToClientPacket;
import alvin.slutprojekt.world.gameobject.GameObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A packet sent to the client notifying them that a game object has moved.
 */
public class ToClientMovePacket implements ToClientPacket {
    public static final PacketType<ToClientPacket> TYPE = new PacketType<>("mv", ToClientMovePacket::new);

    private final int id;
    private final double x;
    private final double y;
    private final double directionRad;

    public ToClientMovePacket(GameObject gameObject) {
        this.id = gameObject.getId();
        this.x = gameObject.getX();
        this.y = gameObject.getY();
        this.directionRad = gameObject.getDirectionRad();
    }

    public ToClientMovePacket(DataInputStream stream) throws IOException {
        this.id = stream.readInt();
        this.x = stream.readDouble();
        this.y = stream.readDouble();
        this.directionRad = stream.readDouble();
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeInt(this.id);
        stream.writeDouble(this.x);
        stream.writeDouble(this.y);
        stream.writeDouble(this.directionRad);
    }

    @Override
    public PacketType<ToClientPacket> getType() {
        return TYPE;
    }

    @Override
    public void handle(ClientSidePacketHandler handler) {
        handler.handleMove(this);
    }

    public int getId() {
        return id;
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

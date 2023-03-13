package alvin.slutprojekt.networking.packet;

import alvin.slutprojekt.Registry;
import alvin.slutprojekt.client.server.ClientSidePacketHandler;
import alvin.slutprojekt.networking.PacketType;
import alvin.slutprojekt.networking.ToClientPacket;
import alvin.slutprojekt.world.gameobject.GameObject;
import alvin.slutprojekt.world.gameobject.GameObjectType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A packet sent to the client notifying them that a new game object has appeared.
 */
public class ToClientNewGameObjectPacket implements ToClientPacket {
    public static final PacketType<ToClientPacket> TYPE = new PacketType<>("new_game_object", ToClientNewGameObjectPacket::new);

    private final int id;
    private final String typeId;
    private final double x;
    private final double y;

    public ToClientNewGameObjectPacket(GameObject gameObject) {
        this.id = gameObject.getId();
        Registry<GameObjectType<?>> registry = gameObject.getEnvironment().getMain().getGameObjectTypeRegistry();
        this.typeId = registry.getIdFor(gameObject.getType());
        this.x = gameObject.getX();
        this.y = gameObject.getY();
    }

    public ToClientNewGameObjectPacket(DataInputStream stream) throws IOException {
        this.id = stream.readInt();
        this.typeId = stream.readUTF();
        this.x = stream.readDouble();
        this.y = stream.readDouble();
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeInt(this.id);
        stream.writeUTF(this.typeId);
        stream.writeDouble(this.x);
        stream.writeDouble(this.y);
    }

    @Override
    public PacketType<ToClientPacket> getType() {
        return TYPE;
    }

    @Override
    public void handle(ClientSidePacketHandler handler) {
        handler.handleNewGameObject(this);
    }

    public int getId() {
        return id;
    }

    public String getTypeId() {
        return typeId;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}

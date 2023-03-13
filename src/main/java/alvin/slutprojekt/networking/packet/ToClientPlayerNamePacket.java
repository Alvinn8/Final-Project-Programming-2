package alvin.slutprojekt.networking.packet;

import alvin.slutprojekt.client.server.ClientSidePacketHandler;
import alvin.slutprojekt.networking.PacketType;
import alvin.slutprojekt.networking.ToClientPacket;
import alvin.slutprojekt.world.gameobject.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ToClientPlayerNamePacket implements ToClientPacket {
    public static final PacketType<ToClientPacket> TYPE = new PacketType<>("player_name", ToClientPlayerNamePacket::new);

    private final int id;
    private final String name;

    public ToClientPlayerNamePacket(Player player) {
        this.id = player.getId();
        this.name = player.getName();
    }

    public ToClientPlayerNamePacket(DataInputStream stream) throws IOException {
        this.id = stream.readInt();
        this.name = stream.readUTF();
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeInt(this.id);
        stream.writeUTF(this.name);
    }

    @Override
    public PacketType<ToClientPacket> getType() {
        return TYPE;
    }

    @Override
    public void handle(ClientSidePacketHandler handler) {
        handler.handlePlayerName(this);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

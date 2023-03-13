package alvin.slutprojekt.networking.packet;

import alvin.slutprojekt.networking.PacketType;
import alvin.slutprojekt.networking.ToServerPacket;
import alvin.slutprojekt.server.ServerSidePacketHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * The first packet the client sends to the server.
 */
public class ToServerHelloPacket implements ToServerPacket {
    public static final PacketType<ToServerPacket> TYPE = new PacketType<>("hello", ToServerHelloPacket::new);

    private final String name;
    private final String gameVersion;

    public ToServerHelloPacket(String name, String gameVersion) {
        this.name = name;
        this.gameVersion = gameVersion;
    }

    public ToServerHelloPacket(DataInputStream stream) throws IOException {
        this.name = stream.readUTF();
        this.gameVersion = stream.readUTF();
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeUTF(this.name);
        stream.writeUTF(this.gameVersion);
    }

    @Override
    public void handle(ServerSidePacketHandler handler) {
        handler.handleHello(this);
    }

    @Override
    public PacketType<ToServerPacket> getType() {
        return TYPE;
    }

    public String getName() {
        return this.name;
    }

    public String getGameVersion() {
        return gameVersion;
    }
}

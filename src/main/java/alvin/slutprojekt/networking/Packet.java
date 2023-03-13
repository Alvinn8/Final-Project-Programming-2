package alvin.slutprojekt.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A class contacting data that is serialized and deserialized to bytes and sent
 * over the network when a player is connected to a server.
 * <p>
 * Packets should also have a constructor that takes a {@link DataInputStream}
 * which should deserialize the packet.
 */
public interface Packet {
    /**
     * Get the {@link PacketType} for this packet.
     *
     * @return The packet type.
     */
    PacketType<?> getType();

    /**
     * Write this packet to a {@link DataOutputStream}.
     *
     * @param stream The stream to write to.
     * @throws IOException If an I/O error occurs while writing the packet.
     */
    void write(DataOutputStream stream) throws IOException;
}

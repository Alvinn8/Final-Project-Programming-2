package alvin.slutprojekt.networking;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * A {@link java.util.function.Function} for creating packets from a
 * {@link DataInputStream}.
 *
 * @param <T> The type of the packet, {@link ToServerPacket} or {@link ToClientPacket}.
 */
@FunctionalInterface
public interface ReadPacketFunction<T extends Packet> {
    /**
     * Create a packet from a {@link DataInputStream}.
     *
     * @param stream The stream.
     * @return The constructed packet.
     * @throws IOException If an I/O error occurs while reading.
     */
    T read(DataInputStream stream) throws IOException;
}

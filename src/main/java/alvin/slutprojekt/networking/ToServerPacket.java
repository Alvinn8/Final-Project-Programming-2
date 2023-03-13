package alvin.slutprojekt.networking;

import alvin.slutprojekt.server.ServerSidePacketHandler;

/**
 * A packet sent to the server from the client. (client --> server)
 */
public interface ToServerPacket extends Packet {
    /**
     * Get the {@link PacketType} for this packet.
     *
     * @return The packet type.
     */
    PacketType<ToServerPacket> getType();

    /**
     * Forward the handling of this packet to an appropriate method in the server side
     * packet handler.
     *
     * @param handler The handler.
     */
    void handle(ServerSidePacketHandler handler);
}

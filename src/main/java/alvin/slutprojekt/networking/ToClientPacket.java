package alvin.slutprojekt.networking;

import alvin.slutprojekt.client.server.ClientSidePacketHandler;

/**
 * A packet sent to the client from the server. (server --> client)
 */
public interface ToClientPacket extends Packet {
    /**
     * Get the {@link PacketType} for this packet.
     *
     * @return The packet type.
     */
    PacketType<ToClientPacket> getType();

    /**
     * Forward the handling of this packet to an appropriate method in the client side
     * packet handler.
     *
     * @param handler The handler.
     */
    void handle(ClientSidePacketHandler handler);
}

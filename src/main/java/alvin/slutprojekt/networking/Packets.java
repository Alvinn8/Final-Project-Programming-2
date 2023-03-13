package alvin.slutprojekt.networking;

import alvin.slutprojekt.Registry;
import alvin.slutprojekt.networking.packet.ToClientChunkPacket;
import alvin.slutprojekt.networking.packet.ToClientDisconnectPacket;
import alvin.slutprojekt.networking.packet.ToClientMovePacket;
import alvin.slutprojekt.networking.packet.ToClientMoveToEnvironmentPacket;
import alvin.slutprojekt.networking.packet.ToClientNewGameObjectPacket;
import alvin.slutprojekt.networking.packet.ToClientPlayerNamePacket;
import alvin.slutprojekt.networking.packet.ToClientRemoveGameObjectPacket;
import alvin.slutprojekt.networking.packet.ToClientSetSelectedItemPacket;
import alvin.slutprojekt.networking.packet.ToClientStoragePacket;
import alvin.slutprojekt.networking.packet.ToClientTileChangePacket;
import alvin.slutprojekt.networking.packet.ToClientWelcomePacket;
import alvin.slutprojekt.networking.packet.ToServerBreakPacket;
import alvin.slutprojekt.networking.packet.ToServerCreatePacket;
import alvin.slutprojekt.networking.packet.ToServerHelloPacket;
import alvin.slutprojekt.networking.packet.ToServerMovePacket;
import alvin.slutprojekt.networking.packet.ToServerPlacePacket;
import alvin.slutprojekt.networking.packet.ToServerRequestChunkPacket;
import alvin.slutprojekt.networking.packet.ToServerSelectItemPacket;

/**
 * Utility methods for registering all packets.
 */
public class Packets {

    /**
     * Register a packet type to a registry.
     *
     * @param registry The registry to register to.
     * @param packetType The packet type to register.
     * @param <T> The type of packet.
     */
    private static <T extends Packet> void register(Registry<PacketType<T>> registry, PacketType<T> packetType) {
        registry.register(packetType.getId(), packetType);
    }

    /**
     * Register all packets that go to the server.
     */
    public static void registerToServerPackets(Registry<PacketType<ToServerPacket>> registry) {
        register(registry, ToServerBreakPacket.TYPE);
        register(registry, ToServerCreatePacket.TYPE);
        register(registry, ToServerHelloPacket.TYPE);
        register(registry, ToServerMovePacket.TYPE);
        register(registry, ToServerPlacePacket.TYPE);
        register(registry, ToServerRequestChunkPacket.TYPE);
        register(registry, ToServerSelectItemPacket.TYPE);
    }

    /**
     * Register all packets that go to the client.
     */
    public static void registerToClientPackets(Registry<PacketType<ToClientPacket>> registry) {
        register(registry, ToClientChunkPacket.TYPE);
        register(registry, ToClientDisconnectPacket.TYPE);
        register(registry, ToClientMovePacket.TYPE);
        register(registry, ToClientMoveToEnvironmentPacket.TYPE);
        register(registry, ToClientNewGameObjectPacket.TYPE);
        register(registry, ToClientPlayerNamePacket.TYPE);
        register(registry, ToClientRemoveGameObjectPacket.TYPE);
        register(registry, ToClientSetSelectedItemPacket.TYPE);
        register(registry, ToClientStoragePacket.TYPE);
        register(registry, ToClientTileChangePacket.TYPE);
        register(registry, ToClientWelcomePacket.TYPE);
    }
}

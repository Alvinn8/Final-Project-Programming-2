package alvin.slutprojekt.server;

import alvin.slutprojekt.AbstractMain;
import alvin.slutprojekt.networking.ToClientPacket;
import alvin.slutprojekt.networking.packet.ToClientNewGameObjectPacket;
import alvin.slutprojekt.networking.packet.ToClientRemoveGameObjectPacket;
import alvin.slutprojekt.networking.packet.ToClientTileChangePacket;
import alvin.slutprojekt.world.Environment;
import alvin.slutprojekt.world.LayerType;
import alvin.slutprojekt.world.World;
import alvin.slutprojekt.world.chunk.ChunkProvider;
import alvin.slutprojekt.world.gameobject.GameObject;
import alvin.slutprojekt.world.tile.TileType;

import java.util.ArrayList;
import java.util.List;

/**
 * An environment that is running on the server.
 * <p>
 * Provides additional methods for sending packets to players and will send
 * packets when tiles are changed in the world.
 */
public class ServerEnvironment extends Environment {
    protected final List<ServerPlayer> players = new ArrayList<>();

    public ServerEnvironment(AbstractMain main, World world, ChunkProvider chunkProvider) {
        super(main, world, chunkProvider);
    }

    @Override
    public void setTile(LayerType layerType, int x, int y, TileType tileType) {
        super.setTile(layerType, x, y, tileType);
        this.sendPacketToAll(new ToClientTileChangePacket(x, y, tileType != null ? tileType.getId() : ""));
    }

    @Override
    public void addNewGameObject(GameObject gameObject) {
        super.addNewGameObject(gameObject);

        synchronized (this.getGameObjectLock()) {

            if (gameObject instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer) gameObject;
                if (!this.players.contains(player)) {
                    this.players.add(player);
                }
            }

            // Let's inform all players that a new game object was added
            ToClientNewGameObjectPacket packet = new ToClientNewGameObjectPacket(gameObject);
            for (ServerPlayer player : this.players) {
                if (player != gameObject) {
                    ClientConnection connection = player.getConnection();
                    // Send new game object packet
                    connection.sendPacket(packet);
                    // Send other information packets
                    gameObject.sendAdditionalCreatePackets(connection);
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        this.players.removeIf(GameObject::isRemoved);
    }

    /**
     * Get a list of all players in this environment.
     *
     * @return The list.
     */
    public List<ServerPlayer> getPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * Send information packets about this environment to the specified player.
     *
     * @param player The player to send packets to.
     */
    public void sendInfoTo(ServerPlayer player) {
        ClientConnection connection = player.getConnection();

        synchronized (this.getGameObjectLock()) {
            for (GameObject gameObject : this.gameObjects) {
                if (gameObject != player) {
                    connection.sendPacket(new ToClientNewGameObjectPacket(gameObject));
                    gameObject.sendAdditionalCreatePackets(connection);
                }
            }
        }
    }

    /**
     * Send a game object to all players except the specified one.
     *
     * @param packet The packet to send.
     * @param except The game object to not send the packet to.
     */
    public void sendPacketToAllExcept(ToClientPacket packet, GameObject except) {
        synchronized (this.getGameObjectLock()) {
            for (ServerPlayer player : this.players) {
                if (player != except) {
                    ClientConnection connection = player.getConnection();
                    connection.sendPacket(packet);
                }
            }
        }
    }

    /**
     * Send a game object to all players.
     *
     * @param packet The packet to send.
     */
    public void sendPacketToAll(ToClientPacket packet) {
        synchronized (this.getGameObjectLock()) {
            for (ServerPlayer player : this.players) {
                ClientConnection connection = player.getConnection();
                connection.sendPacket(packet);
            }
        }
    }
}

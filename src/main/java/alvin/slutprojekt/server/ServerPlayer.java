package alvin.slutprojekt.server;

import alvin.slutprojekt.networking.packet.ToClientMoveToEnvironmentPacket;
import alvin.slutprojekt.networking.packet.ToClientPlayerNamePacket;
import alvin.slutprojekt.networking.packet.ToClientStoragePacket;
import alvin.slutprojekt.world.Environment;
import alvin.slutprojekt.world.gameobject.Player;

/**
 * A player on the server side.
 */
public class ServerPlayer extends Player {
    private final ClientConnection connection;

    public ServerPlayer(Environment environment, double x, double y, ClientConnection connection) {
        super(environment, x, y);
        this.connection = connection;
    }

    @Override
    public void sendAdditionalCreatePackets(ClientConnection connection) {
        // Players must also notify about their name
        connection.sendPacket(new ToClientPlayerNamePacket(this));
    }

    @Override
    public void moveToEnvironment(Environment environment, double x, double y) {
        super.moveToEnvironment(environment, x, y);

        // Notify this player they changed environment
        String environmentId = environment.getWorld().getEnvironmentId(environment);
        this.connection.sendPacket(new ToClientMoveToEnvironmentPacket(environmentId, x, y));

        // Send information about game objects and stuff on this new environment that
        // the client doesn't know about
        ((ServerEnvironment) environment).sendInfoTo(this);
    }

    /**
     * Synchronize the item storage to the client.
     */
    public void syncStorage() {
        this.connection.sendPacket(new ToClientStoragePacket(this.getStorage()));
    }

    /**
     * Get the connection to the client controlling this player.
     *
     * @return The connection.
     */
    public ClientConnection getConnection() {
        return connection;
    }

}

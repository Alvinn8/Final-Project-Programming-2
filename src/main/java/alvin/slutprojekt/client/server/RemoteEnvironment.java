package alvin.slutprojekt.client.server;

import alvin.slutprojekt.AbstractMain;
import alvin.slutprojekt.world.Environment;
import alvin.slutprojekt.world.World;
import alvin.slutprojekt.world.gameobject.GameObject;

/**
 * An {@link Environment} from a remote server. Holds the {@link ServerConnection}
 * instance.
 */
public class RemoteEnvironment extends Environment {
    private ServerConnection connection;

    public RemoteEnvironment(AbstractMain main, RemoteChunkProvider chunkProvider) {
        // world is null, a remote game does not keep track of environments, just
        // listens to when we are sent to a new one.
        super(main, null, chunkProvider);
    }

    @Override
    public boolean isRemote() {
        return true;
    }

    /**
     * Get the connection to the server controlling this environment.
     *
     * @return The connection.
     */
    public ServerConnection getServerConnection() {
        return connection;
    }

    /**
     * Set the connection being used.
     * <p>
     * This method should only be used once.
     *
     * @param connection The connection-
     */
    public void setServerConnection(ServerConnection connection) {
        this.connection = connection;
    }

    /**
     * Remove a game object with the specified id.
     *
     * @param id The id of the game object to remove.
     * @see GameObject#getId()
     * @see GameObject#remove()
     * @see #isRemote()
     */
    public void removeGameObject(int id) {
        synchronized (this.getGameObjectLock()) {
            this.gameObjects.removeIf(o -> o.getId() == id);
        }
    }

    @Override
    public World getWorld() {
        throw new UnsupportedOperationException("A remote environment does not have a world");
    }
}

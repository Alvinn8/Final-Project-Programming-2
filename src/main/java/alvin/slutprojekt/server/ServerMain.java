package alvin.slutprojekt.server;

import alvin.slutprojekt.AbstractMain;
import alvin.slutprojekt.Tickable;
import alvin.slutprojekt.world.Environment;
import alvin.slutprojekt.world.World;
import alvin.slutprojekt.world.chunk.ChunkProvider;
import alvin.slutprojekt.world.chunk.MainChunkProvider;
import alvin.slutprojekt.world.gameobject.Pig;
import alvin.slutprojekt.world.generation.MainEnvTerrainGenerator;

import java.io.IOException;

/**
 * Main class with the main method for the server side.
 */
public class ServerMain extends AbstractMain implements Tickable {
    private final World world;
    private final ClientManager clientManager;
    private final ServerStorage storage;

    public static void main(String[] args) throws ServerStartException {
        new ServerMain();
    }

    public ServerMain() throws ServerStartException {
        this.storage = new ServerStorage();

        // World and environment
        this.world = this.createWorld("world", 1234, ServerEnvironment::new);

        this.getTickingManager().setTicking(this);

        // TCP server
        try {
            this.clientManager = new ClientManager(ClientManager.DEFAULT_PORT, this);
        } catch (IOException e) {
            throw new ServerStartException("Failed to start the TCP server.", e);
        }

        System.out.println("Server started on port " + ClientManager.DEFAULT_PORT);

        System.out.println();
        System.out.println("You are running the server. If you intended");
        System.out.println("to run the game (the client), please use:");
        System.out.println();
        System.out.println("    alvin.slutprojekt.client.ClientMain");
        System.out.println();
        System.out.println("as the main class instead.");
        System.out.println();
        System.out.println("Stop the server with Ctrl + C");
        System.out.println();

        // Start ticking
        Thread.currentThread().setName("Server Ticking Thread");
        this.getTickingManager().startTickLoop();
    }

    @Override
    public void tick() {
        // Tick all environments with players
        for (Environment environment : this.world.getEnvironments()) {
            if (((ServerEnvironment) environment).getPlayers().size() > 0) {
                environment.tick();
            }
        }

        this.clientManager.flushPackets();
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

    @Override
    public ServerStorage getStorage() {
        return storage;
    }

    @Override
    public void shutdownSave() {
        System.out.println("Server is shutting down, saving the world.");
        this.storage.saveWorld(this.world);
    }

    public World getWorld() {
        return this.world;
    }

    public ClientManager getClientManager() {
        return this.clientManager;
    }
}

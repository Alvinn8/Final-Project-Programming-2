package alvin.slutprojekt.client;

import alvin.slutprojekt.AbstractMain;
import alvin.slutprojekt.client.render.ItemRenderer;
import alvin.slutprojekt.client.render.WorldRenderer;
import alvin.slutprojekt.client.screen.LoadingScreen;
import alvin.slutprojekt.client.screen.PlayingScreen;
import alvin.slutprojekt.client.screen.SetNameScreen;
import alvin.slutprojekt.client.screen.StartScreen;
import alvin.slutprojekt.client.screen.TextScreen;
import alvin.slutprojekt.client.screen.TextScreenWithBack;
import alvin.slutprojekt.client.screen.WelcomeScreen;
import alvin.slutprojekt.client.server.RemoteChunkProvider;
import alvin.slutprojekt.client.server.RemoteEnvironment;
import alvin.slutprojekt.client.server.ServerConnection;
import alvin.slutprojekt.item.Item;
import alvin.slutprojekt.networking.packet.ToServerHelloPacket;
import alvin.slutprojekt.server.ClientManager;
import alvin.slutprojekt.world.Environment;
import alvin.slutprojekt.world.World;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Socket;

/**
 * Main class with the main method for the client side.
 */
public class ClientMain extends AbstractMain {
    private final GameWindow gameWindow;
    private final WorldRenderer worldRenderer;
    private final ItemRenderer itemRenderer;
    private final ClientStorage storage;
    private String name;
    private UserPlayer player;

    public static void main(String[] args) {
        // Create loading screen before constructing ClientMain to ensure it
        // is shows as soon as possible.
        GameWindow gameWindow = new GameWindow();

        LoadingScreen loadingScreen = new LoadingScreen();
        gameWindow.setScreen(loadingScreen);

        new ClientMain(gameWindow, loadingScreen);
    }

    private ClientMain(GameWindow gameWindow, LoadingScreen loadingScreen) {
        super();

        this.gameWindow = gameWindow;
        this.gameWindow.getKeyboardManager().setMain(this);

        loadingScreen.incrementCounter();
        this.worldRenderer = new WorldRenderer();

        this.itemRenderer = new ItemRenderer(this.worldRenderer.getMissingTextureImage());

        this.storage = new ClientStorage();

        // Sometimes the first call to drawString takes a really long time, because
        // fonts are loaded. Run this during startup to avoid lag on the first frame.
        loadingScreen.getProgress().setTransitionFactor(0.001);
        loadingScreen.incrementCounter();
        loadingScreen.incrementCounter();
        BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
        img.getGraphics().drawString("test", 0, 0);

        loadingScreen.getProgress().setTransitionFactor(0.2);
        loadingScreen.stage("Loading textures");
        this.worldRenderer.loadTileTextures(this.getTileTypeRegistry());
        this.itemRenderer.loadItemTextures(this.getItemTypeRegistry());
        this.worldRenderer.registerGameObjectRenderers(this.itemRenderer);

        loadingScreen.stage("Starting game");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!this.getStorage().exists()) {
            // New user
            this.gameWindow.setScreen(new WelcomeScreen(this, this.gameWindow, this.getStorage()));
        } else if (!this.getStorage().hasName()) {
            // New user, has not set a name
            this.gameWindow.setScreen(new SetNameScreen(this, this.gameWindow, this.getStorage()));
        } else {
            // Not first startup, directly to the start screen

            // Read the name
            try {
                this.setName(this.getStorage().getName());
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.gameWindow.setScreen(new StartScreen(this));
        }

        Thread.currentThread().setName("Ticking Thread");
        this.getTickingManager().startTickLoop();
    }

    @Override
    public boolean isClientSide() {
        return true;
    }

    @Override
    public ClientStorage getStorage() {
        return storage;
    }

    @Override
    public void shutdownSave() {
        if (this.player != null && !this.player.getEnvironment().isRemote()) {
            System.out.println("The game is shutting down, saving the world.");
            this.storage.saveWorld(this.player.getEnvironment().getWorld());
        }
    }

    /**
     * Add a runnable that will run on the rendering thread the next frame.
     *
     * @param runnable The runnable to run.
     * @see GameWindow#runOnRenderThread(Runnable)
     */
    public void runOnRenderThread(Runnable runnable) {
        this.gameWindow.runOnRenderThread(runnable);
    }

    /**
     * Start a local game.
     *
     * @param worldName The name of the world to load.
     */
    public void startGame(String worldName) {
        long seed = this.storage.getSeedFor(worldName);
        this.startGame(worldName, seed);
    }

    /**
     * Start a local game.
     *
     * @param worldName The name of the world to load or create.
     * @param seed The seed of the world.
     */
    public void startGame(String worldName, long seed) {
        World world = this.createWorld(worldName, seed, Environment::new);

        UserPlayer player = new UserPlayer(this.gameWindow, world.getMainEnvironment(), 0, 0);
        player.setName(this.name);
        this.setPlayer(player);
        world.getMainEnvironment().addNewGameObject(player);
        this.storage.readLocalPlayer(player);

        this.gameWindow.setScreen(new PlayingScreen(this, player));

        this.getTickingManager().setTicking(player.getEnvironment());
    }

    /**
     * Start joining a server.
     *
     * @param serverAddress The server address, including the port separated by a colon.
     */
    public void startOnlineGame(String serverAddress) {
        TextScreen textScreen = new TextScreen();
        textScreen.setText("Connecting to " + serverAddress);
        this.gameWindow.setScreen(textScreen);

        int index = serverAddress.indexOf(':');
        String host = index > 0 ? serverAddress.substring(0, index) : serverAddress;
        int port;
        if (index > 0) {
            try {
                port = Integer.parseInt(serverAddress.substring(index + 1));
            } catch (NumberFormatException e) {
                this.gameWindow.setScreen(new TextScreenWithBack(this, "Invalid port: " + e.getMessage()));
                return;
            }
        } else {
            port = ClientManager.DEFAULT_PORT;
        }

        try {
            Socket socket = new Socket(host, port);

            RemoteChunkProvider chunkProvider = new RemoteChunkProvider();
            RemoteEnvironment environment = new RemoteEnvironment(this, chunkProvider);

            ServerConnection connection = new ServerConnection(socket, this, environment);
            chunkProvider.setServerConnection(connection);
            environment.setServerConnection(connection);

            UserPlayer player = new UserPlayer(this.gameWindow, environment, 0, 0);
            player.setName(this.name);
            environment.addNewGameObject(player);
            this.setPlayer(player);

            connection.sendPacket(new ToServerHelloPacket(this.name, GAME_VERSION));
        } catch (IOException e) {
            e.printStackTrace();

            gameWindow.setScreen(new TextScreenWithBack(this,
                "Kunde inte ansluta till servern: " + e.getMessage()));
        }
    }

    /**
     * Send the user back to the start screen, leaving the game.
     */
    public void backToStartScreen() {
        if (this.player != null) {
            if (!this.player.getEnvironment().isRemote()) {
                this.storage.saveWorld(this.player.getEnvironment().getWorld());
                this.getTickingManager().setTicking(null);
            }
            if (this.player.getEnvironment().isRemote()) {
                ServerConnection connection = ((RemoteEnvironment) this.player.getEnvironment()).getServerConnection();
                try {
                    connection.getSocket().close();
                } catch (IOException e) {
                    System.err.println("Failed to close server connection.");
                    e.printStackTrace();
                }
            }
            this.player = null;
        }

        this.gameWindow.setScreen(new StartScreen(this));
    }
    /**
     * Get the game window that is rendering the game.
     *
     * @return The game window.
     */
    public GameWindow getGameWindow() {
        return this.gameWindow;
    }

    /**
     * Get the {@link WorldRenderer} that renders the world for the client.
     *
     * @return The {@link WorldRenderer} instance.
     */
    public WorldRenderer getWorldRenderer() {
        return this.worldRenderer;
    }

    /**
     * Get the {@link ItemRenderer} that renders items for the client.
     *
     * @return The {@link ItemRenderer} instance.
     */
    public ItemRenderer getItemRenderer() {
        return this.itemRenderer;
    }

    /**
     * Get the name the user selected for their player.
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name the user selected for their player.
     * <p>
     * This will not save the name. So a call to the method
     * {@link ClientStorage#setName(String)}
     * is required to save the name.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the player this user controls. May be null if the game is not being
     * played right now due to for example being in the main menu.
     *
     * @return The player, or null.
     */
    public UserPlayer getPlayer() {
        return player;
    }

    /**
     * Set the player controlled by the user on this client.
     *
     * @param player The player.
     */
    public void setPlayer(UserPlayer player) {
        this.player = player;
    }
}

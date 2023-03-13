package alvin.slutprojekt.client.screen;

import alvin.slutprojekt.MathUtil;
import alvin.slutprojekt.TickingManager;
import alvin.slutprojekt.client.Camera;
import alvin.slutprojekt.client.ClientMain;
import alvin.slutprojekt.client.FrameCounter;
import alvin.slutprojekt.client.GameWindow;
import alvin.slutprojekt.client.render.CreateTabRenderer;
import alvin.slutprojekt.client.render.ItemRenderer;
import alvin.slutprojekt.client.render.SidebarRenderer;
import alvin.slutprojekt.client.render.StorageRenderer;
import alvin.slutprojekt.client.render.WorldRenderer;
import alvin.slutprojekt.world.gameobject.Player;

import javax.swing.*;
import java.awt.*;

/**
 * The screen used when the user is playing in a world.
 * <p>
 * <strong>deltaTime:</strong> The delta time is the time until the next client
 * tick will occur. This is commonly used for interpolation.
 * <p>
 * Values are linearly interpolated between the old value and the current tick's
 * value, when the deltaTime reaches 1 a client tick will happen, meaning we get
 * new old and current values, where the previous "current" value is placed as
 * old. The interpolation can now continue as the deltaTime will be 0 and the old
 * position is the value previously interpolated towards. The result is a smooth
 * interpolation that can run at any framerate while the actual data may be updated
 * at a slower interval.
 */
public class PlayingScreen implements GraphicsGameScreen {
    public final double ZOOM_SPEED = 0.01;
    public final double MAX_ZOOM = 8;
    public final double MIN_ZOOM = 0.1;

    private final ClientMain clientMain;
    private final Camera camera;
    private final FrameCounter frameCounter = new FrameCounter();
    private final StorageRenderer storageRenderer;
    private final CreateTabRenderer createTabRenderer;
    private final SidebarRenderer sidebarRenderer;
    private final Player player;
    private long lastClientTick;

    public PlayingScreen(ClientMain clientMain, Player player) {
        this.clientMain = clientMain;
        this.camera = new Camera(this.clientMain.getGameWindow());
        this.player = player;
        this.storageRenderer = new StorageRenderer(clientMain.getPlayer(), clientMain.getItemRenderer(), player.getStorage());
        this.createTabRenderer = new CreateTabRenderer(clientMain.getPlayer(), clientMain.getItemRenderer(), player.getStorage(), this.clientMain.getRecipeRegistry());
        this.sidebarRenderer = new SidebarRenderer(this.storageRenderer, this.createTabRenderer);
    }

    @Override
    public void onEnable() {
        // Add sidebar
        GameWindow gameWindow = this.clientMain.getGameWindow();
        gameWindow.setLayout(new GridBagLayout());

        GridBagConstraints con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 0;
        con.weightx = 1;
        con.weighty = 1;
        con.anchor = GridBagConstraints.EAST;
        gameWindow.add(Box.createGlue(), con);

        con = new GridBagConstraints();
        con.gridx = 1;
        con.gridy = 1;
        con.weightx = 1;
        con.weighty = 1;
        con.anchor = GridBagConstraints.NORTHEAST;
        JPanel sidebar = this.sidebarRenderer.render();
        gameWindow.add(sidebar, con);

        gameWindow.validate();
    }

    @Override
    public void render(Graphics g, GameWindow gameWindow) {
        if (System.currentTimeMillis() > this.lastClientTick + TickingManager.MS_PER_TICK) {
            this.clientTick();
        }

        // Calculate the time since the last client tick, this is used for interpolation
        long timeSinceLastTick = System.currentTimeMillis() - this.lastClientTick;
        double deltaTime = (double) timeSinceLastTick / TickingManager.MS_PER_TICK;

        // Update camera pan
        camera.getZoom().tick();
        camera.setX(MathUtil.fixFloatingPointError(
            -(player.getObserver().getX(deltaTime) + player.getSize() / 2) * WorldRenderer.TILE_SIZE)
        );
        camera.setY(MathUtil.fixFloatingPointError(
            -(player.getObserver().getY(deltaTime) + player.getSize() / 2) * WorldRenderer.TILE_SIZE)
        );

        int scrollAmount = gameWindow.getMouseManager().consumeScrollAmount();
        double zoom = camera.getZoom().getDesired() + scrollAmount * ZOOM_SPEED;
        if (zoom > MAX_ZOOM) {
            zoom = MAX_ZOOM;
        }
        if (zoom < MIN_ZOOM) {
            zoom = MIN_ZOOM;
        }
        camera.getZoom().set(zoom);

        // Render the environment
        clientMain.getWorldRenderer().renderEnvironment(g, this.clientMain, gameWindow, this.player.getEnvironment(), this.camera, deltaTime);

        // Other rendering
        frameCounter.render(g);

        if (storageRenderer.shouldReRender()) {
            storageRenderer.render();
        }
    }

    /**
     * Run a client tick that ticks things at the same speed as logic ticking, but
     * only handles client-side-related things, like interpolations.
     * <p>
     * This is called during the painting of a frame for those frames where enough
     * time has elapsed since the last client tick.
     */
    public void clientTick() {
        this.player.getEnvironment().clientTick();
        this.lastClientTick = System.currentTimeMillis();
    }

    public Camera getCamera() {
        return this.camera;
    }
}

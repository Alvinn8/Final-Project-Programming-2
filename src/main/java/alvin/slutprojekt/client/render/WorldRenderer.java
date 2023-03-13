package alvin.slutprojekt.client.render;

import alvin.slutprojekt.Registry;
import alvin.slutprojekt.client.Camera;
import alvin.slutprojekt.client.ClientMain;
import alvin.slutprojekt.client.GameWindow;
import alvin.slutprojekt.client.MouseManager;
import alvin.slutprojekt.client.UserPlayer;
import alvin.slutprojekt.client.render.gameobject.GameObjectRenderer;
import alvin.slutprojekt.client.render.gameobject.MissingGameObjectRenderer;
import alvin.slutprojekt.client.render.gameobject.PigRenderer;
import alvin.slutprojekt.client.render.gameobject.PlayerRenderer;
import alvin.slutprojekt.client.screen.PlayingScreen;
import alvin.slutprojekt.resources.Resources;
import alvin.slutprojekt.world.CoordinateUtil;
import alvin.slutprojekt.world.Environment;
import alvin.slutprojekt.world.LayerType;
import alvin.slutprojekt.world.chunk.Chunk;
import alvin.slutprojekt.world.gameobject.GameObject;
import alvin.slutprojekt.world.gameobject.GameObjectType;
import alvin.slutprojekt.world.gameobject.Pig;
import alvin.slutprojekt.world.gameobject.Player;
import alvin.slutprojekt.world.tile.TileType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * The client-side renderer that renders the world, tiles, and game objects.
 */
public class WorldRenderer {
    public static final int TILE_SIZE = 50;
    public static final int TILE_RENDER_MARGIN = 0;

    private final BufferedImage missingTextureImage;
    private final Map<TileType, BufferedImage> tileTextures = new HashMap<>();
    private final Map<GameObjectType<?>, GameObjectRenderer> gameObjectRenderers = new HashMap<>();

    public WorldRenderer() {
        this.missingTextureImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < this.missingTextureImage.getWidth(); x++) {
            for (int y = 0; y < this.missingTextureImage.getHeight(); y++) {
                this.missingTextureImage.setRGB(x, y, 0xFF2222);
            }
        }
    }

    /**
     * Load the texture for a tile type.
     *
     * @param tileType The tile type to load the texture for.
     * @throws IOException If the image failed to read.
     */
    public void loadTileTexture(TileType tileType) throws IOException {
        InputStream stream = Resources.getResource("textures/tiles/" + tileType.getId() + ".png");
        BufferedImage image = ImageIO.read(stream);

        this.tileTextures.put(tileType, image);
    }

    /**
     * Load the tile textures for all registered tile types.
     * <p>
     * Will handle any errors and print them to the console.
     *
     * @param registry The registry where tile types are registered.
     */
    public void loadTileTextures(Registry<TileType> registry) {
        for (TileType tileType : registry.getRegistered()) {
            try {
                this.loadTileTexture(tileType);
            } catch (IOException e) {
                System.err.println("Failed to load tile texture for " + tileType.getId());
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the texture for a tile, and if there is none loaded, use a missing texture.
     *
     * @param tileType The tile to get the texture for.
     * @return The texture as an image.
     */
    public BufferedImage getTileTexture(TileType tileType) {
        BufferedImage bufferedImage = this.tileTextures.get(tileType);
        if (bufferedImage != null) {
            return bufferedImage;
        } else {
            return this.missingTextureImage;
        }
    }

    /**
     * Render a tile in the world.
     *
     * @param g The graphics object to draw on.
     * @param camera The camera viewing the world.
     * @param x The world x coordinate of the tile.
     * @param y The world y coordinate of the tile.
     * @param tileType The tile type to render.
     */
    public void renderTile(Graphics g, Camera camera, int x, int y, TileType tileType) {
        BufferedImage tileTexture = this.getTileTexture(tileType);
        g.drawImage(
            tileTexture,
            camera.getRenderX(x * TILE_SIZE),
            camera.getRenderY(y * TILE_SIZE),
            camera.getRenderWidth(TILE_SIZE),
            camera.getRenderHeight(TILE_SIZE),
            null
        );
    }

    /**
     * Render the highlighted tile outline.
     *
     * @param g The graphics object to draw on.
     * @param camera The camera viewing the world.
     * @param x The x coordinate of the tile.
     * @param y The y coordinate of the tile.
     */
    public void renderHighlighted(Graphics g, Camera camera, int x, int y) {
        g.setColor(Color.WHITE);
        g.drawRect(
            camera.getRenderX(x * TILE_SIZE),
            camera.getRenderY(y * TILE_SIZE),
            camera.getRenderWidth(TILE_SIZE),
            camera.getRenderHeight(TILE_SIZE)
        );
    }

    public void registerGameObjectRenderers(ItemRenderer itemRenderer) {
        this.gameObjectRenderers.put(Pig.TYPE, new PigRenderer());
        this.gameObjectRenderers.put(Player.TYPE, new PlayerRenderer(itemRenderer));
    }

    /**
     * Get the game object renderer for a game object type.
     *
     * @param gameObjectType The game object type.
     * @return The renderer.
     */
    public GameObjectRenderer getGameObjectRenderer(GameObjectType<?> gameObjectType) {
        GameObjectRenderer renderer = this.gameObjectRenderers.get(gameObjectType);
        if (renderer != null) {
            return renderer;
        } else {
            return MissingGameObjectRenderer.INSTANCE;
        }
    }

    /**
     * Render a game object.
     *
     * @param gameObject The game object to render.
     * @param g The graphics object to draw on.
     * @param camera The camera viewing the world.
     * @param deltaTime The delta time [0-1]. See {@link PlayingScreen} for more info.
     * @see GameObjectRenderer
     */
    public void renderGameObject(GameObject gameObject, Graphics g, Camera camera, double deltaTime) {
        GameObjectRenderer renderer = this.getGameObjectRenderer(gameObject.getType());
        renderer.render(gameObject, g, camera, deltaTime);
    }

    /**
     * Render all visible tiles in the environment.
     *
     * @param g The graphics object to draw on.
     * @param gameWindow The game window rendering the game.
     * @param environment The environment to render.
     * @param camera The camera viewing the world.
     */
    public void renderEnvironment(Graphics g, ClientMain main, GameWindow gameWindow, Environment environment, Camera camera, double deltaTime) {
        int w = gameWindow.getWidth() / 2;
        int h = gameWindow.getHeight() / 2;
        double zoom = camera.getZoom().get();

        int minTileX = (int) Math.floor((-w * zoom - camera.getX()) / TILE_SIZE) - TILE_RENDER_MARGIN;
        int minTileY = (int) Math.floor((-h * zoom - camera.getY()) / TILE_SIZE) - TILE_RENDER_MARGIN;
        int maxTileX = (int) Math.floor((w * zoom - camera.getX()) / TILE_SIZE) + TILE_RENDER_MARGIN + 1;
        int maxTileY = (int) Math.floor((h * zoom - camera.getY()) / TILE_SIZE) + TILE_RENDER_MARGIN + 1;

        int minChunkX = CoordinateUtil.tileToChunk(minTileX);
        int minChunkY = CoordinateUtil.tileToChunk(minTileY);
        int maxChunkX = CoordinateUtil.tileToChunk(maxTileX);
        int maxChunkY = CoordinateUtil.tileToChunk(maxTileY);

        // Render tiles
        synchronized (environment.getChunkProvider()) {
            for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
                for (int chunkY = minChunkY; chunkY <= maxChunkY; chunkY++) {
                    Chunk chunk = environment.getChunkProvider().getChunkIfExists(chunkX, chunkY);
                    if (chunk != null) {
                        if (chunk.getChunkX() != chunkX || chunk.getChunkY() != chunkY) {
                            throw new RuntimeException("Incorrect chunk");
                        }
                        int startX = 0;
                        if (chunk.getTileStartX() < minTileX) {
                            startX = CoordinateUtil.tileToRelative(minTileX);
                        }
                        int endX = Chunk.CHUNK_TILE_SIZE;
                        if (chunk.getTileStartX() + endX > maxTileX) {
                            endX = CoordinateUtil.tileToRelative(maxTileX);
                        }

                        int startY = 0;
                        if (chunk.getTileStartY() < minTileY) {
                            startY = CoordinateUtil.tileToRelative(minTileY);
                        }
                        int endY = Chunk.CHUNK_TILE_SIZE;
                        if (chunk.getTileStartY() + endY > maxTileY) {
                            endY = CoordinateUtil.tileToRelative(maxTileY);
                        }

                        for (int x = startX; x < endX; x++) {
                            for (int y = startY; y < endY; y++) {
                                int worldX = CoordinateUtil.relativeToTile(x, chunk.getChunkX());
                                int worldY = CoordinateUtil.relativeToTile(y, chunk.getChunkY());

                                TileType backgroundTileType = chunk.getTileAt(LayerType.BACKGROUND, x, y);
                                if (backgroundTileType != null) {
                                    this.renderTile(g, camera, worldX, worldY, backgroundTileType);
                                }

                                TileType tileType = chunk.getTileAt(LayerType.MAIN, x, y);
                                if (tileType != null) {
                                    this.renderTile(g, camera, worldX, worldY, tileType);
                                }
                            }
                        }
                    } else {
                        environment.getChunkProvider().requestMissingChunk(chunkX, chunkY);
                    }
                }
            }
        }

        // Render game objects
        synchronized (environment.getGameObjectLock()) {
            for (GameObject gameObject : environment.getGameObjects()) {
                this.renderGameObject(gameObject, g, camera, deltaTime);
            }
        }

        MouseManager mouse = gameWindow.getMouseManager();
        int mouseTileX = (int) Math.floor(((mouse.getMouseX() - gameWindow.getWidth() / 2.0) * camera.getZoom().get() - camera.getX()) / TILE_SIZE);
        int mouseTileY = (int) Math.floor(((mouse.getMouseY() - gameWindow.getHeight() / 2.0) * camera.getZoom().get() - camera.getY()) / TILE_SIZE);

        UserPlayer userPlayer = main.getPlayer();
        userPlayer.updateTargetedTile(mouseTileX, mouseTileY);
        if (userPlayer.hasTargetTile()) {
            renderHighlighted(g, camera, userPlayer.getTargetTileX(), userPlayer.getTargetTileY());
        }

    }

    public BufferedImage getMissingTextureImage() {
        return missingTextureImage;
    }
}

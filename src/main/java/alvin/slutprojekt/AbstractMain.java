/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alvin.slutprojekt;

import alvin.slutprojekt.item.ItemType;
import alvin.slutprojekt.item.ItemTypes;
import alvin.slutprojekt.item.TileBreakRewards;
import alvin.slutprojekt.networking.PacketType;
import alvin.slutprojekt.networking.Packets;
import alvin.slutprojekt.networking.ToClientPacket;
import alvin.slutprojekt.networking.ToServerPacket;
import alvin.slutprojekt.client.ClientStorage;
import alvin.slutprojekt.storage.Storage;
import alvin.slutprojekt.world.Environment;
import alvin.slutprojekt.world.EnvironmentConstructor;
import alvin.slutprojekt.world.LayerType;
import alvin.slutprojekt.world.World;
import alvin.slutprojekt.world.chunk.ChunkProvider;
import alvin.slutprojekt.world.chunk.MainChunkProvider;
import alvin.slutprojekt.world.gameobject.GameObjectType;
import alvin.slutprojekt.world.gameobject.GameObjectTypes;
import alvin.slutprojekt.world.gameobject.Pig;
import alvin.slutprojekt.world.generation.MainEnvTerrainGenerator;
import alvin.slutprojekt.world.generation.TerrainGenerationThread;
import alvin.slutprojekt.world.generation.TerrainGenerator;
import alvin.slutprojekt.world.generation.UndergroundEnvTerrainGenerator;
import alvin.slutprojekt.world.tile.ItemPlaceTiles;
import alvin.slutprojekt.world.tile.TileType;
import alvin.slutprojekt.world.tile.TileTypes;

/**
 * Abstract class for both application entrypoints.
 * <p>
 * Holds common fields and methods used by both the client and server entrypoint.
 */
public abstract class AbstractMain {
    public static final String GAME_VERSION = "1.0";

    private final TileTypes tileTypes;
    private final ItemTypes itemTypes;
    private final TickingManager tickingManager;
    private final ItemPlaceTiles itemPlaceTiles;
    private final TileBreakRewards tileBreakRewards;
    private final TerrainGenerationThread terrainGenerationThread;

    private final Registry<TileType> tileTypeRegistry;
    private final Registry<ItemType> itemTypeRegistry;
    private final Registry<PacketType<ToServerPacket>> toServerPackets;
    private final Registry<PacketType<ToClientPacket>> toClientPackets;
    private final Registry<GameObjectType<?>> gameObjectTypeRegistry;
    private final Registry<CreateRecipe> recipeRegistry;

    public AbstractMain() {
        // Registries
        this.tileTypeRegistry = new Registry<>();
        this.itemTypeRegistry = new Registry<>();
        this.toServerPackets = new Registry<>();
        this.toClientPackets = new Registry<>();
        this.gameObjectTypeRegistry = new Registry<>();
        this.recipeRegistry = new Registry<>();

        // Register entries in registries
        this.tileTypes = new TileTypes(this.tileTypeRegistry);
        this.itemTypes = new ItemTypes(this.itemTypeRegistry);
        this.itemPlaceTiles = new ItemPlaceTiles();
        this.itemPlaceTiles.register(this.tileTypes, this.itemTypes);
        this.tileBreakRewards = new TileBreakRewards();
        this.tileBreakRewards.register(this.tileTypes, this.itemTypes);
        Packets.registerToServerPackets(this.toServerPackets);
        Packets.registerToClientPackets(this.toClientPackets);
        GameObjectTypes.register(this.gameObjectTypeRegistry);
        CreateRecipes.register(this.recipeRegistry, this.itemTypes);

        this.tickingManager = new TickingManager();
        this.terrainGenerationThread = new TerrainGenerationThread();

        // Add a shutdown hook to run when the game is closing to save the world
        // before the process exits.
        Runtime.getRuntime().addShutdownHook(new Thread("Shutdown Save Thread") {
            @Override
            public void run() {
                shutdownSave();
            }
        });
    }

    /**
     * Check whether the game is running server side.
     *
     * @return true or false.
     */
    public boolean isServerSide() {
        return false;
    }

    /**
     * Check whether the game is running client side.
     *
     * @return true or false.
     */
    public boolean isClientSide() {
        return false;
    }

    /**
     * Get the {@link Storage} instance responsible for getting the path to store
     * files about the project.
     *
     * @return The ClientStorage instance.
     */
    public abstract Storage getStorage();

    /**
     * Save the world.
     * <p>
     * This method called when the process is shutting down.
     */
    public abstract void shutdownSave();

    /**
     * Create a world for processing locally (aka not remote).
     * <p>
     * The {@code environmentConstructor} argument can be used to either create a
     * {@link Environment} or {@link alvin.slutprojekt.server.ServerEnvironment}
     * depending on if this is running server side or client side.
     *
     * @param name The name of the world.
     * @param seed The seed to use for generation.
     * @param environmentConstructor A function for constructing an environment.
     * @return The created world.
     */
    public World createWorld(String name, long seed, EnvironmentConstructor environmentConstructor) {
        World world = new World(name, seed);

        TerrainGenerator mainEnvTerrainGenerator = new MainEnvTerrainGenerator(seed);
        ChunkProvider mainEnvChunkProvider = new MainChunkProvider(this.getStorage(), mainEnvTerrainGenerator, this.getTerrainGenerationThread());
        Environment mainEnv = environmentConstructor.create(this, world, mainEnvChunkProvider);
        world.setMainEnvironment(mainEnv);

        TerrainGenerator undergroundEnvTerrainGenerator = new UndergroundEnvTerrainGenerator(seed);
        ChunkProvider undergroundEnvChunkProvider = new MainChunkProvider(this.getStorage(), undergroundEnvTerrainGenerator, this.getTerrainGenerationThread());
        Environment undergroundEnv = environmentConstructor.create(this, world, undergroundEnvChunkProvider);
        world.setUndergroundEnvironment(undergroundEnv);

        for (int i = 0; i < 3; i++) {
            mainEnv.addNewGameObject(new Pig(mainEnv, 5, 0));
        }

        return world;
    }

    /**
     * Add a runnable that will run on the ticking thread the next tick.
     *
     * @param runnable The runnable to run.
     */
    public void runOnTickingThread(Runnable runnable) {
        this.tickingManager.runOnTickingThread(runnable);
    }

    /**
     * Get the {@link TileTypes} instance containing the tile types for the game.
     *
     * @return The {@link TileTypes} instance.
     */
    public TileTypes getTileTypes() {
        return this.tileTypes;
    }

    /**
     * Get the {@link ItemTypes} instance containing the item types for the game.
     *
     * @return The {@link ItemTypes} instance.
     */
    public ItemTypes getItemTypes() {
        return itemTypes;
    }

    /**
     * Get the {@link TileBreakRewards} instance containing rewards for broken tiles.
     *
     * @return The {@link TileBreakRewards} instance.
     */
    public TileBreakRewards getTileBreakRewards() {
        return this.tileBreakRewards;
    }

    /**
     * Get the {@link ItemPlaceTiles} instance containing the tiles to place for items.
     *
     * @return The {@link ItemPlaceTiles} instance.
     */
    public ItemPlaceTiles getItemPlaceTiles() {
        return this.itemPlaceTiles;
    }

    /**
     * Get the ticking manager.
     *
     * @return The ticking manager.
     */
    public TickingManager getTickingManager() {
        return this.tickingManager;
    }

    /**
     * Get the thread that generates chunks.
     *
     * @return The {@link TerrainGenerationThread} instance.
     */
    public TerrainGenerationThread getTerrainGenerationThread() {
        return this.terrainGenerationThread;
    }

    /**
     * Get the tile type registry where all tile types are registered.
     *
     * @return The tile type registry.
     */
    public Registry<TileType> getTileTypeRegistry() {
        return this.tileTypeRegistry;
    }

    /**
     * Get the item type registry where all item types are registered.
     *
     * @return The item type registry.
     */
    public Registry<ItemType> getItemTypeRegistry() {
        return itemTypeRegistry;
    }

    /**
     * Get the registry of packets going to the server.
     *
     * @return The registry.
     */
    public Registry<PacketType<ToServerPacket>> getToServerPackets() {
        return toServerPackets;
    }

    /**
     * Get the registry of packets going to the client.
     *
     * @return The registry.
     */
    public Registry<PacketType<ToClientPacket>> getToClientPackets() {
        return toClientPackets;
    }

    /**
     * Get the registry of game object types.
     *
     * @return The registry.
     */
    public Registry<GameObjectType<?>> getGameObjectTypeRegistry() {
        return gameObjectTypeRegistry;
    }

    /**
     * Get the registry of create recipes.
     *
     * @return The registry
     */
    public Registry<CreateRecipe> getRecipeRegistry() {
        return recipeRegistry;
    }
}

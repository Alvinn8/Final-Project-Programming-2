package alvin.slutprojekt.client.server;

import alvin.slutprojekt.Registry;
import alvin.slutprojekt.client.ClientMain;
import alvin.slutprojekt.client.UserPlayer;
import alvin.slutprojekt.client.screen.PlayingScreen;
import alvin.slutprojekt.client.screen.TextScreenWithBack;
import alvin.slutprojekt.item.Item;
import alvin.slutprojekt.item.ItemStorage;
import alvin.slutprojekt.item.ItemType;
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
import alvin.slutprojekt.world.LayerType;
import alvin.slutprojekt.world.chunk.ChunkProvider;
import alvin.slutprojekt.world.gameobject.GameObject;
import alvin.slutprojekt.world.gameobject.GameObjectType;
import alvin.slutprojekt.world.gameobject.Player;
import alvin.slutprojekt.world.tile.TileType;

/**
 * Handles packets coming from the server to the client.
 */
public class ClientSidePacketHandler {
    private final ServerConnection connection;
    private final ClientMain main;
    private RemoteEnvironment environment;

    public ClientSidePacketHandler(ServerConnection connection, ClientMain main, RemoteEnvironment environment) {
        this.connection = connection;
        this.main = main;
        this.environment = environment;
    }

    public void handleDisconnect(ToClientDisconnectPacket packet) {
        this.main.getGameWindow().setScreen(new TextScreenWithBack(this.main, "Disconnected by server: " + packet.getMessage()));
    }

    public void handleWelcome(ToClientWelcomePacket packet) {
        System.out.println("We were welcomed!");

        Player player = this.main.getPlayer();
        player.setId(packet.getPlayerId());
        player.setX(packet.getPlayerX());
        player.setY(packet.getPlayerY());

        // Set screen
        this.main.getGameWindow().setScreen(new PlayingScreen(this.main, player));
    }

    public void handleChunk(ToClientChunkPacket packet) {
        ChunkProvider chunkProvider = this.environment.getChunkProvider();
        if (chunkProvider instanceof RemoteChunkProvider) {
            ((RemoteChunkProvider) chunkProvider).handleChunkPacket(packet);
        }
    }

    public void handleNewGameObject(ToClientNewGameObjectPacket packet) {
        Registry<GameObjectType<?>> registry = this.environment.getMain().getGameObjectTypeRegistry();
        GameObjectType<?> type = registry.get(packet.getTypeId());

        GameObject gameObject = type.getConstructor().create(this.environment, packet.getX(), packet.getY());
        gameObject.setId(packet.getId());

        this.environment.addNewGameObject(gameObject);
    }

    public void handleMove(ToClientMovePacket packet) {
        synchronized (this.environment.getGameObjectLock()) {
            for (GameObject gameObject : this.environment.getGameObjects()) {
                if (gameObject.getId() == packet.getId()) {
                    gameObject.setX(packet.getX());
                    gameObject.setY(packet.getY());
                    gameObject.setDirectionRad(packet.getDirectionRad());
                }
            }
        }
    }

    public void handleRemoveGameObject(ToClientRemoveGameObjectPacket packet) {
        this.environment.removeGameObject(packet.getId());
    }

    public void handleTileChange(ToClientTileChangePacket packet) {
        TileType tileType = this.main.getTileTypeRegistry().get(packet.getTileId());
        this.environment.setTile(LayerType.MAIN, packet.getX(), packet.getY(), tileType);
    }

    public void handlePlayerName(ToClientPlayerNamePacket packet) {
        synchronized (this.environment.getGameObjectLock()) {
            for (GameObject gameObject : this.environment.getGameObjects()) {
                if (gameObject.getId() == packet.getId() && gameObject instanceof Player) {
                    ((Player) gameObject).setName(packet.getName());
                }
            }
        }
    }

    public void handleStorage(ToClientStoragePacket packet) {
        UserPlayer player = this.main.getPlayer();
        Registry<ItemType> itemTypeRegistry = this.main.getItemTypeRegistry();
        ItemStorage storage = player.getStorage();

        Item selectedItem = player.getSelectedItem();
        int selectedItemIndex = selectedItem == null ? -1 : storage.indexOf(selectedItem);

        for (int i = 0; i < packet.getSlots(); i++) {
            storage.setItem(i, packet.getItem(i, itemTypeRegistry));
        }

        if (selectedItemIndex != -1) {
            player.setSelectedItem(storage.getItem(selectedItemIndex));
        }
    }

    public void handleSetSelectedItem(ToClientSetSelectedItemPacket packet) {
        synchronized (this.environment.getGameObjectLock()) {
            for (GameObject gameObject : this.environment.getGameObjects()) {
                if (gameObject.getId() == packet.getPlayerId() && gameObject instanceof Player) {
                    ItemType itemType = main.getItemTypeRegistry().get(packet.getTileTypeId());
                    // We can create a new instance here, we only care about the visuals
                    Item item = itemType == null ? null : new Item(itemType, 1);
                    ((Player) gameObject).setSelectedItem(item);
                }
            }
        }
    }

    public void handleMoveToEnvironment(ToClientMoveToEnvironmentPacket packet) {
        double x = packet.getX();
        double y = packet.getY();
        // Run on render thread to avoid concurrency issues
        this.main.runOnRenderThread(() -> {
            RemoteChunkProvider chunkProvider = new RemoteChunkProvider();
            chunkProvider.setServerConnection(this.connection);
            this.environment = new RemoteEnvironment(this.main, chunkProvider);
            this.environment.setServerConnection(this.connection);

            this.main.getPlayer().moveToEnvironment(this.environment, x, y);
        });
    }
}

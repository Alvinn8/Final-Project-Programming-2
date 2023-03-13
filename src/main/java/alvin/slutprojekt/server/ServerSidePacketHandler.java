package alvin.slutprojekt.server;

import alvin.slutprojekt.CreateRecipe;
import alvin.slutprojekt.Debugging;
import alvin.slutprojekt.item.Item;
import alvin.slutprojekt.networking.packet.ToClientChunkPacket;
import alvin.slutprojekt.networking.packet.ToClientSetSelectedItemPacket;
import alvin.slutprojekt.networking.packet.ToClientWelcomePacket;
import alvin.slutprojekt.networking.packet.ToServerBreakPacket;
import alvin.slutprojekt.networking.packet.ToServerCreatePacket;
import alvin.slutprojekt.networking.packet.ToServerHelloPacket;
import alvin.slutprojekt.networking.packet.ToServerMovePacket;
import alvin.slutprojekt.networking.packet.ToServerPlacePacket;
import alvin.slutprojekt.networking.packet.ToServerRequestChunkPacket;
import alvin.slutprojekt.networking.packet.ToServerSelectItemPacket;
import alvin.slutprojekt.storage.SavePlayerRequest;
import alvin.slutprojekt.world.LayerType;
import alvin.slutprojekt.world.tile.TileType;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Handles packets coming from the client to the server for a specific user.
 */
public class ServerSidePacketHandler {
    private final ClientConnection connection;
    private final ServerMain main;
    private ServerPlayer player;

    public ServerSidePacketHandler(ClientConnection connection, ServerMain main) {
        this.connection = connection;
        this.main = main;
    }

    public void disconnected() {
        this.main.getStorage().addToQueue(new SavePlayerRequest(this.player));
        this.player.remove();
        Debugging.println("user disconnected. name: " + this.player.getName());
    }

    /**
     * Get the {@link ServerEnvironment} where the player currently is.
     *
     * @return The environment.
     */
    private ServerEnvironment getEnvironment() {
        return (ServerEnvironment) this.player.getEnvironment();
    }

    public void handleHello(ToServerHelloPacket packet) {
        String name = packet.getName();
        Debugging.println("got hello, name: " + name);
        System.out.println(name + " joined with game version " + packet.getGameVersion());

        this.connection.setPlayerName(name);

        // Add player game object
        ServerEnvironment mainEnvironment = (ServerEnvironment) this.main.getWorld().getMainEnvironment();
        this.player = new ServerPlayer(mainEnvironment, 0, 0, this.connection);
        this.player.setName(name);
        this.main.getStorage().readPlayer(this.player);
        this.getEnvironment().addNewGameObject(this.player);
        this.player.getStorage().add(new Item(this.main.getItemTypes().MINE, 1));

        // Send packets
        this.connection.sendPacket(new ToClientWelcomePacket(this.player.getId(), this.player.getX(), this.player.getY()));
        this.getEnvironment().sendInfoTo(this.player);
        this.player.syncStorage();
    }

    public void handleChunkRequest(ToServerRequestChunkPacket packet) {
        // Request the chunk async ...
        this.getEnvironment().getChunkProvider().getChunk(packet.getChunkX(), packet.getChunkY())
            .thenAccept(chunk -> {
                // ... and reply with a packet once we have the chunk
                // send the packet on the ticking thread to avoid concurrency issues
                this.main.runOnTickingThread(() -> {
                    try {
                        connection.sendPacket(new ToClientChunkPacket(chunk));
                    } catch (IOException e) {
                        throw new UncheckedIOException("Failed to serialize chunk", e);
                    }
                });
            });
    }

    public void handleMove(ToServerMovePacket packet) {
        this.player.setX(packet.getX());
        this.player.setY(packet.getY());
        this.player.setDirectionRad(packet.getDirectionRad());
    }

    public void handlePlace(ToServerPlacePacket packet) {
        TileType tileType = this.main.getTileTypeRegistry().get(packet.getTileTypeId());
        this.getEnvironment().setTile(LayerType.MAIN, packet.getTargetTileX(), packet.getTargetTileY(), tileType);

        // Consume one of the selected item
        Item selectedItem = this.player.getSelectedItem();
        if (selectedItem != null && this.main.getItemPlaceTiles().getTileToPlace(selectedItem.getType()) == tileType) {
            selectedItem.setAmount(selectedItem.getAmount() - 1);
            if (selectedItem.getAmount() <= 0) {
                this.player.setSelectedItem(null);
            }
            this.player.getStorage().filterEmpty();
            this.player.syncStorage();
        }
    }

    public void handleBreak(ToServerBreakPacket packet) {
        ServerEnvironment environment = getEnvironment();
        TileType oldTile = environment.getTile(LayerType.MAIN, packet.getTargetTileX(), packet.getTargetTileY());
        environment.setTile(LayerType.MAIN, packet.getTargetTileX(), packet.getTargetTileY(), null);
        if (oldTile != null) {
            oldTile.onBreak(this.player, this.main.getTileBreakRewards());
        }
    }

    public void handleSelectItem(ToServerSelectItemPacket packet) {
        int index = packet.getIndex();
        Item item = index == -1 ? null : player.getStorage().getItem(index);
        player.setSelectedItem(item);
        getEnvironment().sendPacketToAllExcept(new ToClientSetSelectedItemPacket(player.getId(), item == null ? "" : item.getType().getId()), player);
    }

    public void handleCreate(ToServerCreatePacket packet) {
        CreateRecipe createRecipe = this.main.getRecipeRegistry().get(packet.getCreateRecipeId());
        createRecipe.create(this.player.getStorage());
        this.player.syncStorage();
    }
}

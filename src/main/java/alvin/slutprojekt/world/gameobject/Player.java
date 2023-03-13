package alvin.slutprojekt.world.gameobject;

import alvin.slutprojekt.Registry;
import alvin.slutprojekt.TickingManager;
import alvin.slutprojekt.item.Item;
import alvin.slutprojekt.item.ItemStorage;
import alvin.slutprojekt.item.ItemType;
import alvin.slutprojekt.world.CoordinateUtil;
import alvin.slutprojekt.world.Environment;
import alvin.slutprojekt.world.LayerType;
import alvin.slutprojekt.world.World;
import alvin.slutprojekt.world.chunk.Chunk;
import alvin.slutprojekt.world.tile.TileType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Player extends GameObject {
    public static final GameObjectType<Player> TYPE = new GameObjectType<>("player", Player::new);
    public static final double MOVEMENT_SPEED = 4.0 / TickingManager.TICKS_PER_SECOND;

    private final ItemStorage storage = new ItemStorage(10);
    private Item selectedItem;
    private String name;
    private boolean pendingCaveClear = false;

    public Player(Environment environment, double x, double y) {
        super(environment, x, y);
    }

    @Override
    public GameObjectType<Player> getType() {
        return TYPE;
    }

    @Override
    public void tick() {
        super.tick();

        Environment environment = getEnvironment();

        // Do a cave clear if one is pending
        if (this.pendingCaveClear) {
            int tileX = (int) Math.round(this.x);
            int tileY = (int) Math.round(this.y);
            Chunk chunk = environment.getChunkProvider().getChunkIfExists(CoordinateUtil.tileToChunk(tileX), CoordinateUtil.tileToChunk(tileY));
            if (chunk != null) {
                for (int offsetX = -1; offsetX < 2; offsetX++) {
                    for (int offsetY = -2; offsetY < 3; offsetY++) {
                        TileType currentTile = environment.getTile(LayerType.MAIN, tileX + offsetX, tileY + offsetY);
                        if (currentTile != null) {
                            environment.setTile(LayerType.MAIN, tileX + offsetX, tileY + offsetY, null);
                        }
                    }
                }
                environment.setTile(LayerType.MAIN, tileX, tileY - 2, environment.getMain().getTileTypes().MINE);
                this.pendingCaveClear = false;
            }
        }

        // Check if the player is entering a mine
        int tileX = (int) Math.round(this.x);
        int tileY = (int) Math.round(this.y);
        TileType tile = environment.getTile(LayerType.MAIN, tileX, tileY);
        if (tile != null && tile == environment.getMain().getTileTypes().MINE) {
            // If the player is standing on a mine, let's switch environment.
            World world = environment.getWorld();
            if (environment == world.getMainEnvironment()) {
                System.out.println("main --> underground");
                // main --> underground
                Environment undergroundEnvironment = world.getUndergroundEnvironment();
                this.moveToEnvironment(undergroundEnvironment, this.x, this.y + 2);
            } else if (environment == world.getUndergroundEnvironment()) {
                System.out.println("underground --> main");
                // underground --> main
                Environment mainEnvironment = world.getMainEnvironment();
                this.moveToEnvironment(mainEnvironment, this.x, this.y + 2);
            }
            // There should be a mine regardless where you enter, and the surrounding area
            // should clear. Add the pending flag so when the chunk is loaded it will clear
            this.pendingCaveClear = true;
        }
    }

    /**
     * Write this player to a {@link DataOutputStream}.
     * <p>
     * This is used to save the player's data.
     *
     * @param stream The stream to write to.
     * @throws IOException If an I/O error occurs while writing.
     */
    public void write(DataOutputStream stream) throws IOException {
        stream.writeUTF(this.getEnvironment().getWorld().getEnvironmentId(this.getEnvironment()));
        stream.writeDouble(this.x);
        stream.writeDouble(this.y);
        stream.writeDouble(this.directionRad);
        stream.writeUTF(this.name);

        for (int i = 0; i < this.storage.getSlots(); i++) {
            Item item = this.storage.getItem(i);
            Item.writeItem(stream, item);
        }
    }

    /**
     * Read this player from a {@link DataInputStream}.
     *
     * @param stream The stream to read from.
     * @throws IOException If an I/O error occurs while reading.
     */
    public void read(DataInputStream stream) throws IOException {
        String environmentId = stream.readUTF();
        this.x = stream.readDouble();
        this.y = stream.readDouble();
        this.directionRad = stream.readDouble();
        this.name = stream.readUTF();

        Registry<ItemType> itemTypeRegistry = this.getEnvironment().getMain().getItemTypeRegistry();
        for (int i = 0; i < this.storage.getSlots(); i++) {
            Item item = Item.readItem(stream, itemTypeRegistry);
            this.storage.setItem(i, item);
        }

        if (!getEnvironment().getWorld().getEnvironmentId(getEnvironment()).equals(environmentId)) {
            this.moveToEnvironment(this.getEnvironment().getWorld().getEnvironmentById(environmentId), this.x, this.y);
        }
    }

    public ItemStorage getStorage() {
        return storage;
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Item selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

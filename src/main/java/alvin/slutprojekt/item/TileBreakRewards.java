package alvin.slutprojekt.item;

import alvin.slutprojekt.world.tile.TileType;
import alvin.slutprojekt.world.tile.TileTypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps track of the items that should be rewarded to a player when a tile is
 * broken.
 */
public class TileBreakRewards {
    private final Map<TileType, Item> items = new HashMap<>();

    /**
     * Register an item that should be rewarded when the tile is broken.
     *
     * @param tileType The tile being broken.
     * @param item The item to reward.
     */
    public void registerReward(TileType tileType, Item item) {
        this.items.put(tileType, item);
    }

    /**
     * Get the drops for a tile type.
     *
     * @param tileType The tile type.
     * @return The drops, or null.
     */
    public Item getReward(TileType tileType) {
        return items.get(tileType);
    }

    /**
     * Register the built-in rewards.
     *
     * @param tileTypes The tile types in the game.
     * @param itemTypes The item types in the game.
     */
    public void register(TileTypes tileTypes, ItemTypes itemTypes) {
        this.registerReward(tileTypes.TREE, new Item(itemTypes.WOOD, 1));
        this.registerReward(tileTypes.WOOD, new Item(itemTypes.WOOD, 1));
        this.registerReward(tileTypes.STONE, new Item(itemTypes.STONE, 1));
        this.registerReward(tileTypes.COAL_ORE, new Item(itemTypes.COAL, 1));
        this.registerReward(tileTypes.IRON_ORE, new Item(itemTypes.IRON, 1));
        this.registerReward(tileTypes.COPPER_ORE, new Item(itemTypes.COPPER, 1));
        this.registerReward(tileTypes.BAUXITE, new Item(itemTypes.ALUMINIUM, 1));
        this.registerReward(tileTypes.MINE, new Item(itemTypes.MINE, 1));
    }
}

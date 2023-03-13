package alvin.slutprojekt.world.tile;

import alvin.slutprojekt.item.ItemType;
import alvin.slutprojekt.item.ItemTypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores the tiles that are placed when an item is used.
 */
public class ItemPlaceTiles {
    private final Map<ItemType, TileType> data = new HashMap<>();

    /**
     * Register that the item type places the tile type.
     *
     * @param itemType The item type that will place the tile.
     * @param tileType The tile type to place.
     */
    public void registerPlacement(ItemType itemType, TileType tileType) {
        this.data.put(itemType, tileType);
    }

    /**
     * Get the tile the specified item should place.
     *
     * @param itemType The item.
     * @return The tile to place, or null.
     */
    public TileType getTileToPlace(ItemType itemType) {
        return this.data.get(itemType);
    }

    /**
     * Register the built-in placements.
     *
     * @param tileTypes The tile types in the game.
     * @param itemTypes The item types in the game.
     */
    public void register(TileTypes tileTypes, ItemTypes itemTypes) {
        this.registerPlacement(itemTypes.WOOD, tileTypes.WOOD);
        this.registerPlacement(itemTypes.STONE, tileTypes.STONE);
        this.registerPlacement(itemTypes.MINE, tileTypes.MINE);

        this.registerPlacement(itemTypes.COAL, tileTypes.COAL_ORE);
        this.registerPlacement(itemTypes.IRON, tileTypes.IRON_ORE);
        this.registerPlacement(itemTypes.COPPER, tileTypes.COPPER_ORE);
        this.registerPlacement(itemTypes.ALUMINIUM, tileTypes.BAUXITE);
    }
}

package alvin.slutprojekt.item;

import alvin.slutprojekt.Registry;

import java.util.ArrayList;
import java.util.List;

/**
 * The item types in the game.
 */
public class ItemTypes {
    private List<ItemType> toRegister = new ArrayList<>();

    public final ItemType WOOD = register(new ItemType("wood", "Trä"));
    public final ItemType STONE = register(new ItemType("stone", "Sten"));
    public final ItemType ALUMINIUM = register(new ItemType("aluminium", "Aluminium"));
    public final ItemType COAL = register(new ItemType("coal", "Kol"));
    public final ItemType COPPER = register(new ItemType("copper", "Koppar"));
    public final ItemType IRON = register(new ItemType("iron", "Järn"));
    public final ItemType MINE = register(new ItemType("mine", "Gruva"));

    /**
     * Create a new ItemTypes instance.
     *
     * @param registry The registry to register all tile types to.
     */
    public ItemTypes(Registry<ItemType> registry) {
        // Let the fields be created, (happens before constructor is called) and then register.
        for (ItemType tileType : this.toRegister) {
            registry.register(tileType.getId(), tileType);
        }
        this.toRegister = null;
    }

    private ItemType register(ItemType tileType) {
        this.toRegister.add(tileType);
        return tileType;
    }
}

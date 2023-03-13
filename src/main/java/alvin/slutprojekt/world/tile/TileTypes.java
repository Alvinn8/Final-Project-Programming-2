package alvin.slutprojekt.world.tile;

import alvin.slutprojekt.Registry;

import java.util.ArrayList;
import java.util.List;

/**
 * The tile types in the game.
 */
public class TileTypes {
    private List<TileType> toRegister = new ArrayList<>();

    public final TileType GRASS = register(new TileType("grass"));
    public final TileType GRASS_WITH_ROCKS = register(new TileType("grass_with_rocks"));
    public final TileType WATER = register(new TileType("water"));
    public final TileType TREE = register(new TileType("tree"));
    public final TileType WOOD = register(new TileType("wood"));
    public final TileType SAND = register(new TileType("sand"));
    public final TileType GRAVEL = register(new TileType("gravel"));
    public final TileType STONE = register(new TileType("stone"));
    public final TileType BACKGROUND_STONE = register(new TileType("background_stone"));
    public final TileType COAL_ORE = register(new TileType("coal_ore"));
    public final TileType IRON_ORE = register(new TileType("iron_ore"));
    public final TileType COPPER_ORE = register(new TileType("copper_ore"));
    public final TileType BAUXITE = register(new TileType("bauxite"));
    public final TileType MINE = register(new TileType("mine", false));

    /**
     * Create a new TileTypes instance.
     *
     * @param registry The registry to register all tile types to.
     */
    public TileTypes(Registry<TileType> registry) {
        // Let the fields be created, (happens before constructor is called) and then register.
        for (TileType tileType : this.toRegister) {
            registry.register(tileType.getId(), tileType);
        }
        this.toRegister = null;
    }

    private TileType register(TileType tileType) {
        this.toRegister.add(tileType);
        return tileType;
    }
}

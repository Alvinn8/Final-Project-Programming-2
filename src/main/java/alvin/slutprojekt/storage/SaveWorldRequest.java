package alvin.slutprojekt.storage;

import alvin.slutprojekt.world.World;

/**
 * A request to save all chunks with changes in a world.
 */
public class SaveWorldRequest implements StorageRequest {
    private final World world;

    public SaveWorldRequest(World world) {
        this.world = world;
    }

    @Override
    public void execute(Storage storage) {
        storage.saveWorld(this.world);
    }
}

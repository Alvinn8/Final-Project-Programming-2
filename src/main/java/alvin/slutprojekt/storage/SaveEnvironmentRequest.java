package alvin.slutprojekt.storage;

import alvin.slutprojekt.world.Environment;

/**
 * A request to save all chunks with changes in an environment.
 */
public class SaveEnvironmentRequest implements StorageRequest {
    private final Environment environment;

    public SaveEnvironmentRequest(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void execute(Storage storage) {
        storage.saveEnvironment(this.environment.getWorld(), this.environment);
    }
}

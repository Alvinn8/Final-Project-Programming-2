package alvin.slutprojekt.storage;

/**
 * A request that will be run on the storage thread.
 */
public interface StorageRequest {
    /**
     * Execute this request on the storage thread.
     * <p>
     * This method will be called on the storage thread.
     *
     * @param storage The storage instance.
     */
    void execute(Storage storage);
}

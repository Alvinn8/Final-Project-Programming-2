package alvin.slutprojekt.storage;

import alvin.slutprojekt.world.gameobject.Player;

public class SavePlayerRequest implements StorageRequest {
    private final Player player;

    public SavePlayerRequest(Player player) {
        this.player = player;
    }

    @Override
    public void execute(Storage storage) {
        storage.savePlayer(this.player);
    }
}

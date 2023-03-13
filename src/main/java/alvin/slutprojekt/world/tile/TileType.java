package alvin.slutprojekt.world.tile;

import alvin.slutprojekt.item.Item;
import alvin.slutprojekt.item.TileBreakRewards;
import alvin.slutprojekt.server.ServerPlayer;
import alvin.slutprojekt.world.gameobject.Player;

/**
 * A type of tile.
 */
public class TileType {
    private final String id;
    private final boolean collision;

    public TileType(String id) {
        this(id, true);
    }

    public TileType(String id, boolean collision) {
        this.id = id;
        this.collision = collision;
    }

    public String getId() {
        return id;
    }

    public boolean hasCollision() {
        return collision;
    }

    /**
     * Called when a player breaks this tile.
     *
     * @param player The player that broke the tile.
     * @param tileBreakRewards The {@link TileBreakRewards} instance.
     */
    public void onBreak(Player player, TileBreakRewards tileBreakRewards) {
        Item reward = tileBreakRewards.getReward(this);
        if (reward != null) {
            player.getStorage().add(reward);
            // If we are a server, let the client know their storage changed
            if (player instanceof ServerPlayer) {
                ((ServerPlayer) player).syncStorage();
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TileType tileType = (TileType) o;

        return id.equals(tileType.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

package alvin.slutprojekt.client;

import alvin.slutprojekt.client.server.RemoteEnvironment;
import alvin.slutprojekt.client.server.ServerConnection;
import alvin.slutprojekt.item.Item;
import alvin.slutprojekt.networking.packet.ToServerBreakPacket;
import alvin.slutprojekt.networking.packet.ToServerMovePacket;
import alvin.slutprojekt.networking.packet.ToServerPlacePacket;
import alvin.slutprojekt.networking.packet.ToServerSelectItemPacket;
import alvin.slutprojekt.world.Environment;
import alvin.slutprojekt.world.LayerType;
import alvin.slutprojekt.world.gameobject.Player;
import alvin.slutprojekt.world.tile.TileType;

/**
 * The player that the user is controlling.
 */
public class UserPlayer extends Player {
    public static final int MIN_REACH_DISTANCE_SQ = 2 * 2;
    public static final int MAX_REACH_DISTANCE_SQ = 6 * 6;

    private final GameWindow gameWindow;
    private boolean hasTargetTile = false;
    private int targetTileX;
    private int targetTileY;

    public UserPlayer(GameWindow gameWindow, Environment environment, double x, double y) {
        super(environment, x, y);
        this.gameWindow = gameWindow;
    }

    @Override
    public void clientTick() {
        KeyboardManager keyboardManager = gameWindow.getKeyboardManager();

        // Movement
        double oldDirectionRad = this.directionRad;

        double deltaX = 0;
        double deltaY = 0;
        double MOVEMENT_SPEED = UserPlayer.MOVEMENT_SPEED;
        // if (keyboardManager.isPressed('z')) {
        //     MOVEMENT_SPEED *= 5;
        // }
        if (keyboardManager.isPressingUp()) {
            deltaY -= MOVEMENT_SPEED;
        }
        if (keyboardManager.isPressingDown()) {
            deltaY += MOVEMENT_SPEED;
        }
        if (keyboardManager.isPressingLeft()) {
            deltaX -= MOVEMENT_SPEED;
        }
        if (keyboardManager.isPressingRight()) {
            deltaX += MOVEMENT_SPEED;
        }
        this.move(deltaX, deltaY);

        // Face the mouse cursor.
        MouseManager mouseManager = gameWindow.getMouseManager();
        double relMouseY = mouseManager.getMouseY() - gameWindow.getHeight() / 2.0;
        double relMouseX = mouseManager.getMouseX() - gameWindow.getWidth() / 2.0;
        this.directionRad = Math.atan2(relMouseY, relMouseX);

        // If we are connected to a server, and we moved...
        if (this.getEnvironment().isRemote() && (deltaX != 0 || deltaY != 0 || this.directionRad != oldDirectionRad)) {
            // ...notify the server that we moved
            ServerConnection connection = ((RemoteEnvironment) this.getEnvironment()).getServerConnection();
            connection.sendPacket(new ToServerMovePacket(this));
        }

        // Placing tiles
        if (keyboardManager.isPressingPlace() || mouseManager.isRightClicking()) {
            Item selectedItem = getSelectedItem();
            TileType tileType = selectedItem == null
                ? null
                : getEnvironment().getMain().getItemPlaceTiles().getTileToPlace(selectedItem.getType());

            // The user is holding a tile and pressing place
            if (tileType != null) {
                // If the tile isn't already a tile there...
                if (getEnvironment().getTile(LayerType.MAIN, targetTileX, targetTileY) == null) {
                    // ... then set the tile type
                    getEnvironment().setTile(LayerType.MAIN, targetTileX, targetTileY, tileType);

                    selectedItem.setAmount(selectedItem.getAmount() - 1);
                    if (selectedItem.getAmount() <= 0) {
                        setSelectedItem(null);
                    }
                    getStorage().filterEmpty();

                    // And if we are connected to a server...
                    if (getEnvironment().isRemote()) {
                        // ...notify the server that we placed a tile
                        ServerConnection connection = ((RemoteEnvironment) this.getEnvironment()).getServerConnection();
                        connection.sendPacket(new ToServerPlacePacket(targetTileX, targetTileY, tileType.getId()));
                    }
                }
            }
        }

        // Breaking tiles
        if (keyboardManager.isPressingBreak() || mouseManager.isLeftClicking()) {
            TileType oldTile = getEnvironment().getTile(LayerType.MAIN, targetTileX, targetTileY);
            if (oldTile != null) {
                getEnvironment().setTile(LayerType.MAIN, targetTileX, targetTileY, null);

                // If we are connected to a server...
                if (getEnvironment().isRemote()) {
                    // ...notify the server that we broke a tile
                    ServerConnection connection = ((RemoteEnvironment) this.getEnvironment()).getServerConnection();
                    connection.sendPacket(new ToServerBreakPacket(targetTileX, targetTileY));
                    // ... and let the server handle items
                } else {
                    // ... otherwise we're in change or processing, let's add the item
                    oldTile.onBreak(this, this.getEnvironment().getMain().getTileBreakRewards());
                }
            }
        }

        super.clientTick();
    }

    @Override
    public void moveToEnvironment(Environment environment, double x, double y) {
        super.moveToEnvironment(environment, x, y);

        // If the game is local make sure the new environment is being ticked
        if (!environment.isRemote()) {
            environment.getMain().getTickingManager().setTicking(environment);
        }
    }

    /**
     * Update the tile that is being targeted by this user by calculating the tile
     * from the mouse coordinates.
     *
     * @param mouseTileX The mouse x coordinate.
     * @param mouseTileY The mouse y coordinate.
     */
    public void updateTargetedTile(int mouseTileX, int mouseTileY) {
        double deltaX = x - mouseTileX;
        double deltaY = y - mouseTileY;
        double distanceSq = deltaX * deltaX + deltaY * deltaY;

        if (distanceSq < MAX_REACH_DISTANCE_SQ && distanceSq > MIN_REACH_DISTANCE_SQ) {
            this.targetTileX = mouseTileX;
            this.targetTileY = mouseTileY;
            this.hasTargetTile = true;
        } else {
            this.hasTargetTile = false;
        }
    }

    @Override
    public void setSelectedItem(Item selectedItem) {
        super.setSelectedItem(selectedItem);

        if (getEnvironment().isRemote()) {
            ServerConnection connection = ((RemoteEnvironment) this.getEnvironment()).getServerConnection();
            connection.sendPacket(new ToServerSelectItemPacket(getStorage().indexOf(selectedItem)));
        }
    }

    /**
     * Whether the user is currently hovering over a tile.
     *
     * @return true or false.
     */
    public boolean hasTargetTile() {
        return hasTargetTile;
    }

    /**
     * Get the x coordinate of the tile this user is targeting.
     * <p>
     * Only use this value if {@link #hasTargetTile()} is true.
     *
     * @return The x coordinate of the tile.
     */
    public int getTargetTileX() {
        return targetTileX;
    }

    /**
     * Get the y coordinate of the tile this user is targeting.
     * <p>
     * Only use this value if {@link #hasTargetTile()} is true.
     *
     * @return The y coordinate of the tile.
     */
    public int getTargetTileY() {
        return targetTileY;
    }
}

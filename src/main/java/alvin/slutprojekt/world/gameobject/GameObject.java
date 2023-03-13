package alvin.slutprojekt.world.gameobject;

import alvin.slutprojekt.client.ClientGameObjectObserver;
import alvin.slutprojekt.client.screen.PlayingScreen;
import alvin.slutprojekt.server.ClientConnection;
import alvin.slutprojekt.server.ServerGameObjectTracker;
import alvin.slutprojekt.world.Environment;
import alvin.slutprojekt.world.LayerType;
import alvin.slutprojekt.world.tile.TileType;

import java.awt.*;

/**
 * An object that can move in the world, for example a player.
 */
public abstract class GameObject {
    private static int nextId = 1;

    private int id = nextId++;
    private Environment environment;
    protected double x;
    protected double y;
    protected double directionRad;
    private final ClientGameObjectObserver observer = new ClientGameObjectObserver(this);
    private final ServerGameObjectTracker  tracker  = new ServerGameObjectTracker(this);
    protected double size = 1;
    private boolean removed = false;

    public GameObject(Environment environment, double x, double y) {
        this.environment = environment;
        this.x = x;
        this.y = y;
    }

    /**
     * Get the {@link GameObjectType} of this game object.
     *
     * @return The type.
     */
    public abstract GameObjectType<?> getType();

    /**
     * Change the environment for this game object.
     *
     * @param environment The new environment.
     * @param x The x coordinate within that environment.
     * @param y The y coordinate within that environment.
     */
    public void moveToEnvironment(Environment environment, double x, double y) {
        this.environment = environment;
        this.x = x;
        this.y = y;

        // Add to new
        environment.addNewGameObject(this);
    }

    /**
     * Tick this game object.
     */
    public void tick() {}

    /**
     * Client tick this game object.
     * @see PlayingScreen#clientTick()
     */
    public void clientTick() {
        this.getObserver().set();
    }

    /**
     * Mark this game object for removal, removing it the next tic.
     */
    public void remove() {
        this.removed = true;
    }

    /**
     * Check whether this game object has been marked for removal.
     *
     * @return Whether marked for removal.
     */
    public boolean isRemoved() {
        return removed;
    }

    /**
     * Get the id of this game object.
     * <p>
     * The id is used to identify a game object when sending and receiving packets,
     * for example.
     * <p>
     * The id will change when a game object is serialized and deserialized.
     * <p>
     * The id of one same game object will be the same on both the server and client.
     *
     * @return The id.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Set the game object id for this game object.
     * <p>
     * Two game objects may not have the same id.
     *
     * @param id The id.
     * @see #getId()
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Send additional packets about this game object to a connection.
     * <p>
     * This is called on the server side when a client is informed of a game object in
     * the world.
     *
     * @param connection The connection to send the packets to.
     */
    public void sendAdditionalCreatePackets(ClientConnection connection) {}

    public Environment getEnvironment() {
        return environment;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    /**
     * Move this game object by the amounts specified in the arguments to this method,
     * while taking collision into account.
     * <p>
     * If the game object collides, it will not move there.
     * <p>
     * It is recommended to buffer calls to this method so collisions only need to be
     * calculated once.
     *
     * @param deltaX How much to move in the x direction.
     * @param deltaY How much to move in the y direction.
     */
    public void move(double deltaX, double deltaY) {
        // If the game object already collides (is stuck), let them walk freely,
        // otherwise check collision
        if (!collidesAt(x, y)) {
            // If the game object will collide at the new location, don't let them
            // go in that axis
            if (collidesAt(x + deltaX, y)) {
                deltaX = 0;
            }
            if (collidesAt(x, y + deltaY)) {
                deltaY = 0;
            }
        }
        this.x += deltaX;
        this.y += deltaY;
    }

    /**
     * Check if this game object would collide if it was at the specified coordinates.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return Whether the game object collides.
     */
    public boolean collidesAt(double x, double y) {
        for (int tileX = (int) Math.floor(x); tileX <= x + size; tileX++) {
            for (int tileY = (int) Math.floor(y); tileY < y + size; tileY++) {
                TileType tile = this.environment.getTile(LayerType.MAIN, tileX, tileY);
                if (tile != null && tile.hasCollision()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get the tile coordinates this game object is currently targeting.
     * <p>
     * This is the tile the game object is facing.
     *
     * @return The point containing the x and y coordinates.
     * @see #directionRad
     */
    @Deprecated
    public Point getTargetPosition() {
        double directionX = Math.cos(this.directionRad) * 2;
        double directionY = Math.sin(this.directionRad) * 2;
        int tileX = (int) (x - size / 2 + directionX);
        int tileY = (int) (y - size / 2 + directionY);

        return new Point(tileX, tileY);
    }

    /**
     * Get the direction angle of this game object, measured in radians.
     *
     * @return The angle in radians.
     */
    public double getDirectionRad() {
        return this.directionRad;
    }

    /**
     * Set the direction angle of this game object, measured in radians.
     *
     * @param directionRad The angle in radians.
     */
    public void setDirectionRad(double directionRad) {
        this.directionRad = directionRad;
    }

    /**
     * Get the {@link ClientGameObjectObserver} observing this game object.
     *
     * @return The observer.
     */
    public ClientGameObjectObserver getObserver() {
        return this.observer;
    }

    /**
     * Get the {@link ServerGameObjectTracker} tracking this game object.
     *
     * @return The tracker.
     */
    public ServerGameObjectTracker getTracker() {
        return this.tracker;
    }

    /**
     * Get the size of this game object in the unit tile coordinates.
     *
     * @return The size of the tile.
     */
    public double getSize() {
        return this.size;
    }
}

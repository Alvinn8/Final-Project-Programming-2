package alvin.slutprojekt.server;

import alvin.slutprojekt.world.gameobject.GameObject;

/**
 * An object that tracks properties of a {@link GameObject} for the server, used
 * to track when to send position update packets.
 */
public class ServerGameObjectTracker {
    private final GameObject gameObject;

    // the last information sent to the clients
    private double x;
    private double y;
    private double directionRad;

    public ServerGameObjectTracker(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public void update() {
        this.x = this.gameObject.getX();
        this.y = this.gameObject.getY();
        this.directionRad = this.gameObject.getDirectionRad();
    }

    public boolean hasChanged() {
        if (this.x != this.gameObject.getX()
                || this.y  != this.gameObject.getY()) {
            return true;
        }
        double diff = Math.abs(this.directionRad - this.gameObject.getDirectionRad());
        return diff > 0.01;
    }
}

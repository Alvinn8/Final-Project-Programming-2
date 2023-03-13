package alvin.slutprojekt.client;

import alvin.slutprojekt.MathUtil;
import alvin.slutprojekt.client.screen.PlayingScreen;
import alvin.slutprojekt.world.gameobject.GameObject;

/**
 * An object that observes properties of a {@link GameObject} for the client, used
 * for interpolation.
 * <p>
 * This works as a "view" or a "snapshot" of the properties. This ensures values are
 * updated consistently on the client
 */
public class ClientGameObjectObserver {
    private final GameObject gameObject;

    private double directionRadOld;
    private double xOld;
    private double yOld;

    private double directionRad;
    private double x;
    private double y;

    public ClientGameObjectObserver(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    /**
     * Update the observed position with the actual position
     */
    public void set() {
        this.xOld = this.x;
        this.yOld = this.y;
        this.directionRadOld = this.directionRad;

        this.x = this.gameObject.getX();
        this.y = this.gameObject.getY();
        this.directionRad = this.gameObject.getDirectionRad();
    }

    /**
     * Get the interpolated x position of the observed game object.
     *
     * @param deltaTime The delta time [0-1]. See {@link PlayingScreen} for more info.
     * @return The position.
     * @see GameObject#getX()
     */
    public double getX(double deltaTime) {
        return MathUtil.lerp(this.xOld, this.x, deltaTime);
    }

    /**
     * Get the interpolated y position of the observed game object.
     *
     * @param deltaTime The delta time [0-1]. See {@link PlayingScreen} for more info.
     * @return The position.
     * @see GameObject#getY()
     */
    public double getY(double deltaTime) {
        return MathUtil.lerp(this.yOld, this.y, deltaTime);
    }

    /**
     * Get the interpolated direction of the observed game object.
     *
     * @param deltaTime The delta time [0-1]. See {@link PlayingScreen} for more info.
     * @return The angle in radians.
     * @see GameObject#getDirectionRad()
     */
    public double getDirectionRad(double deltaTime) {
        return MathUtil.lerpAngle(this.directionRadOld, this.directionRad, deltaTime);
    }

    /**
     * Get the game object being observed.
     *
     * @return The game object.
     */
    public GameObject getGameObject() {
        return this.gameObject;
    }
}

package alvin.slutprojekt.client.render.gameobject;

import alvin.slutprojekt.client.Camera;
import alvin.slutprojekt.client.render.WorldRenderer;
import alvin.slutprojekt.client.screen.PlayingScreen;
import alvin.slutprojekt.world.gameobject.GameObject;

import java.awt.*;
import java.awt.geom.AffineTransform;

public abstract class GameObjectRenderer {

    /**
     * Render the specified game object on the screen.
     *
     * @param gameObject The game object that is being rendered.
     * @param g The graphics object to draw on.
     * @param camera The camera viewing the game.
     * @param deltaTime This frame's deltaTime. See {@link PlayingScreen} for more info.
     */
    public void render(GameObject gameObject, Graphics g, Camera camera, double deltaTime) {
        double x = gameObject.getObserver().getX(deltaTime);
        double y = gameObject.getObserver().getY(deltaTime);
        double directionRad = gameObject.getObserver().getDirectionRad(deltaTime);

        double centerX = x + gameObject.getSize() / 2;
        double centerY = y + gameObject.getSize() / 2;

        Graphics2D g2 = (Graphics2D) g;

        // Store the current rotation
        AffineTransform reset = g2.getTransform();
        // Rotate
        g2.rotate(
            directionRad,
            camera.getRenderX((centerX * WorldRenderer.TILE_SIZE)),
            camera.getRenderY((centerY * WorldRenderer.TILE_SIZE))
        );
        // Render the head
        this.renderHead(gameObject, g, camera, x, y, centerX, centerY);
        // Reset the rotation
        g2.setTransform(reset);

        // Render the body
        g.setColor(this.getBodyColor());
        this.renderBody(gameObject, g, camera, x, y);
    }

    /**
     * Render the head of the game object at the rightmost part of the body.
     * <p>
     * When this method is called the graphics will be rotated so that the actual
     * drawing ends up in the right place taking the direction into account.
     *
     * @param gameObject The game object being rendered.
     * @param g The graphics object to draw on.
     * @param camera The camera viewing the game.
     * @param x The interpolated x coordinate of the game object.
     * @param y The interpolated y coordinate of the game object.
     * @param centerX The interpolated center of the game object.
     * @param centerY The interpolated center of the game object.
     */
    protected abstract void renderHead(GameObject gameObject, Graphics g, Camera camera, double x, double y, double centerX, double centerY);

    /**
     * Render the body of the game object.
     * <p>
     * By default, this is a circle.
     *
     * @param gameObject The game object being rendered.
     * @param g The graphics object to draw on.
     * @param camera The camera viewing the game.
     * @param x The interpolated x coordinate of the game object.
     * @param y The interpolated y coordinate of the game object.
     */
    protected void renderBody(GameObject gameObject, Graphics g, Camera camera, double x, double y) {
        g.fillOval(
            camera.getRenderX(x * WorldRenderer.TILE_SIZE),
            camera.getRenderY(y * WorldRenderer.TILE_SIZE),
            camera.getRenderWidth(gameObject.getSize() * WorldRenderer.TILE_SIZE),
            camera.getRenderHeight(gameObject.getSize() * WorldRenderer.TILE_SIZE)
        );
    }

    /**
     * Get the color to set before rendering the body.
     *
     * @return The color.
     */
    protected Color getBodyColor() {
        return Color.BLACK;
    }
}

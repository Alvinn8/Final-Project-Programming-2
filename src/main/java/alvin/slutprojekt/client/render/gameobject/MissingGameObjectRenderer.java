package alvin.slutprojekt.client.render.gameobject;

import alvin.slutprojekt.client.Camera;
import alvin.slutprojekt.world.gameobject.GameObject;

import java.awt.*;

/**
 * A {@link GameObjectRenderer} for when no renderer has been registered.
 */
public class MissingGameObjectRenderer extends GameObjectRenderer {
    public static final MissingGameObjectRenderer INSTANCE = new MissingGameObjectRenderer();

    private MissingGameObjectRenderer() {}

    @Override
    protected void renderHead(GameObject gameObject, Graphics g, Camera camera, double x, double y, double centerX, double centerY) {}
}

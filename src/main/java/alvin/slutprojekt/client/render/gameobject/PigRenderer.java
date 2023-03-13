package alvin.slutprojekt.client.render.gameobject;

import alvin.slutprojekt.client.Camera;
import alvin.slutprojekt.client.render.WorldRenderer;
import alvin.slutprojekt.resources.Resources;
import alvin.slutprojekt.world.gameobject.GameObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;

public class PigRenderer extends GameObjectRenderer {
    public static final Color COLOR = new Color(241, 163, 166);
    private final BufferedImage face;

    public PigRenderer() {
        BufferedImage face;
        try {
            face = ImageIO.read(Resources.getResource("textures/gameobjects/pig_face.png"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        this.face = face;
    }

    @Override
    protected void renderHead(GameObject gameObject, Graphics g, Camera camera, double x, double y, double centerX, double centerY) {
        g.drawImage(
            this.face,
            camera.getRenderX((x + gameObject.getSize()) * WorldRenderer.TILE_SIZE - 3),
            camera.getRenderY(centerY * WorldRenderer.TILE_SIZE - 12),
            camera.getRenderWidth(16),
            camera.getRenderHeight(24),
            null
        );
    }

    @Override
    protected Color getBodyColor() {
        return COLOR;
    }
}

package alvin.slutprojekt.client.render.gameobject;

import alvin.slutprojekt.client.Camera;
import alvin.slutprojekt.client.UserPlayer;
import alvin.slutprojekt.client.render.ItemRenderer;
import alvin.slutprojekt.client.render.WorldRenderer;
import alvin.slutprojekt.item.Item;
import alvin.slutprojekt.world.gameobject.GameObject;
import alvin.slutprojekt.world.gameobject.Player;

import java.awt.*;

public class PlayerRenderer extends GameObjectRenderer {
    public static final Color NAME_BACKGROUND_COLOR = new Color(0, 0, 0, 128);
    public static final Color BODY_COLOR = Color.BLUE;
    public static final Color TIP_COLOR = new Color(133, 204, 255);
    public static final int TIP_BASE = 14;
    public static final int TIP_LENGTH = 12;

    private final ItemRenderer itemRenderer;

    public PlayerRenderer(ItemRenderer itemRenderer) {
        this.itemRenderer = itemRenderer;
    }

    @Override
    protected void renderBody(GameObject gameObject, Graphics g, Camera camera, double x, double y) {
        super.renderBody(gameObject, g, camera, x, y);

        // If there is a name, and this player is not the user player (they already know
        // their name, no need to show them that) then show a name plate
        Player player = (Player) gameObject;
        String name = player.getName();
        if (name != null && !name.isEmpty() && !(player instanceof UserPlayer)) {
            g.setColor(NAME_BACKGROUND_COLOR);
            g.fillRect(
                camera.getRenderX((x * WorldRenderer.TILE_SIZE)),
                camera.getRenderY((y * WorldRenderer.TILE_SIZE) - 15),
                camera.getRenderWidth((gameObject.getSize() * WorldRenderer.TILE_SIZE)),
                camera.getRenderHeight(10)
            );
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.PLAIN, camera.getRenderHeight(10)));
            g.drawString(name, camera.getRenderX((x * WorldRenderer.TILE_SIZE)) + 4, camera.getRenderY((y * WorldRenderer.TILE_SIZE) - 6));
        }
    }

    @Override
    protected void renderHead(GameObject gameObject, Graphics g, Camera camera, double x, double y, double centerX, double centerY) {
        g.setColor(TIP_COLOR);
        double cornerAX = ((x + gameObject.getSize()) * WorldRenderer.TILE_SIZE);
        double cornerAY = (centerY * WorldRenderer.TILE_SIZE) - TIP_BASE / 2.0;
        double cornerBX = cornerAX;
        double cornerBY = cornerAY + TIP_BASE;
        double cornerCX = cornerAX + TIP_LENGTH;
        double cornerCY = cornerAY + TIP_BASE / 2.0;

        Polygon triangle = new Polygon(
            new int[] { camera.getRenderX(cornerAX), camera.getRenderX(cornerBX), camera.getRenderX(cornerCX) },
            new int[] { camera.getRenderY(cornerAY), camera.getRenderY(cornerBY), camera.getRenderY(cornerCY) },
            3
        );
        g.fillPolygon(triangle);

        // Render selected item
        Player player = (Player) gameObject;
        Item selectedItem = player.getSelectedItem();
        if (selectedItem != null) {
            Image image = this.itemRenderer.getItemTexture(selectedItem.getType()).getImage();
            g.drawImage(
                image,
                camera.getRenderX((x + gameObject.getSize()) * WorldRenderer.TILE_SIZE),
                camera.getRenderY(centerY * WorldRenderer.TILE_SIZE),
                camera.getRenderWidth(16),
                camera.getRenderHeight(16),
                null
            );
        }
    }

    @Override
    protected Color getBodyColor() {
        return BODY_COLOR;
    }
}

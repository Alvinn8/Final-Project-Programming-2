package alvin.slutprojekt.client.render;

import alvin.slutprojekt.Registry;
import alvin.slutprojekt.item.ItemType;
import alvin.slutprojekt.resources.Resources;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ItemRenderer {
    private final ImageIcon missingTextureIcon;
    private final Map<ItemType, ImageIcon> itemTextures = new HashMap<>();

    public ItemRenderer(BufferedImage missingTextureImage) {
        this.missingTextureIcon = new ImageIcon(missingTextureImage);
    }

    /**
     * Load the texture for an item type.
     *
     * @param itemType The item type to load the texture for.
     * @throws IOException If the image failed to read.
     */
    public void loadItemTexture(ItemType itemType) throws IOException {
        InputStream stream = Resources.getResource("textures/items/" + itemType.getId() + ".png");
        BufferedImage image = ImageIO.read(stream);
        ImageIcon icon = new ImageIcon(image);

        this.itemTextures.put(itemType, icon);
    }

    /**
     * Load the item textures for all registered item types.
     * <p>
     * Will handle any errors and print them to the console.
     *
     * @param registry The registry where item types are registered.
     */
    public void loadItemTextures(Registry<ItemType> registry) {
        for (ItemType itemType : registry.getRegistered()) {
            try {
                this.loadItemTexture(itemType);
            } catch (IOException e) {
                System.err.println("Failed to load item texture for " + itemType.getId());
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the texture for an item, and if there is none loaded, use a missing texture.
     *
     * @param itemType The item to get the texture for.
     * @return The texture as an image icon.
     */
    public ImageIcon getItemTexture(ItemType itemType) {
        ImageIcon bufferedImage = this.itemTextures.get(itemType);
        if (bufferedImage != null) {
            return bufferedImage;
        } else {
            return this.missingTextureIcon;
        }
    }

}

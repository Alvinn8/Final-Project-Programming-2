package alvin.slutprojekt.client.render;

import alvin.slutprojekt.CreateRecipe;
import alvin.slutprojekt.Registry;
import alvin.slutprojekt.client.UserPlayer;
import alvin.slutprojekt.client.server.RemoteEnvironment;
import alvin.slutprojekt.client.server.ServerConnection;
import alvin.slutprojekt.item.Item;
import alvin.slutprojekt.item.ItemStorage;
import alvin.slutprojekt.networking.packet.ToServerCreatePacket;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

/**
 * A swing component for rendering a recipe.
 */
public class RecipeComponent extends JPanel {
    private final CreateRecipe recipe;
    private final UserPlayer player;
    private final JButton createButton;

    public RecipeComponent(ItemRenderer itemRenderer, CreateRecipe recipe, UserPlayer player) {
        this.recipe = recipe;
        this.player = player;

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setBorder(new EtchedBorder());
        this.add(new ItemComponent(itemRenderer, recipe.getResult(), false));
        this.add(Box.createHorizontalGlue());

        JPanel ingredientsPanel = new JPanel();
        ingredientsPanel.setLayout(new BoxLayout(ingredientsPanel, BoxLayout.Y_AXIS));
        ingredientsPanel.add(new JLabel("Ingredienser:"));
        for (Item ingredient : recipe.getIngredients()) {
            ingredientsPanel.add(new ItemComponent(itemRenderer, ingredient, false));
        }
        this.add(ingredientsPanel);

        this.createButton = new JButton("Skapa");
        this.add(this.createButton);

        this.createButton.addActionListener(event -> {
            if (this.recipe.canAfford(this.player.getStorage())) {
                if (this.player.getEnvironment().isRemote()) {
                    ServerConnection serverConnection = ((RemoteEnvironment) this.player.getEnvironment()).getServerConnection();
                    Registry<CreateRecipe> registry = this.player.getEnvironment().getMain().getRecipeRegistry();
                    String createRecipeId = registry.getIdFor(this.recipe);
                    serverConnection.sendPacket(new ToServerCreatePacket(createRecipeId));
                } else {
                    this.recipe.create(this.player.getStorage());
                }
            }
        });

        this.updateCanAfford();
    }

    public void updateCanAfford() {
        this.createButton.setEnabled(this.recipe.canAfford(this.player.getStorage()));
        this.validate();
    }
}

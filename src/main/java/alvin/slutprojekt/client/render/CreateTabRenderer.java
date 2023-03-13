package alvin.slutprojekt.client.render;

import alvin.slutprojekt.CreateRecipe;
import alvin.slutprojekt.Registry;
import alvin.slutprojekt.client.UserPlayer;
import alvin.slutprojekt.item.ItemStorage;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class CreateTabRenderer implements PropertyChangeListener {
    private final ItemRenderer itemRenderer;
    private final UserPlayer player;
    private final Registry<CreateRecipe> recipeRegistry;
    private List<RecipeComponent> recipeComponents;

    public CreateTabRenderer(UserPlayer player, ItemRenderer itemRenderer, ItemStorage storage, Registry<CreateRecipe> recipeRegistry) {
        this.player = player;
        this.itemRenderer = itemRenderer;
        storage.addPropertyChangeListener(this);
        this.recipeRegistry = recipeRegistry;
    }


    public JPanel render() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        this.recipeComponents = new ArrayList<>();
        for (CreateRecipe createRecipe : this.recipeRegistry.getRegistered()) {
            RecipeComponent recipeComponent = new RecipeComponent(this.itemRenderer, createRecipe, this.player);
            panel.add(recipeComponent);
            this.recipeComponents.add(recipeComponent);
        }
        return panel;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        for (RecipeComponent recipeComponent : this.recipeComponents) {
            recipeComponent.updateCanAfford();
        }
    }
}

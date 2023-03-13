package alvin.slutprojekt;

import alvin.slutprojekt.item.Item;
import alvin.slutprojekt.item.ItemStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A recipe for something that can be created on the create tab.
 */
public class CreateRecipe {
    private final Item result;
    private final List<Item> ingredients;

    public CreateRecipe(Item result, Item... ingredients) {
        this.result = result;
        this.ingredients = Arrays.asList(ingredients);
    }

    /**
     * Check whether the specified storage can afford to create this recipe.
     *
     * @param storage The storage.
     * @return Whether the recipe can be afforded.
     */
    public boolean canAfford(ItemStorage storage) {
        for (Item ingredient : this.ingredients) {
            boolean hasIngredient = false;
            for (int i = 0; i < storage.getSlots(); i++) {
                Item item = storage.getItem(i);
                if (item != null && item.getType() == ingredient.getType() && item.getAmount() >= ingredient.getAmount()) {
                    hasIngredient = true;
                    break;
                }
            }

            if (!hasIngredient) {
                // An ingredient is missing, the recipe can not be afforded.
                return false;
            }
        }
        // No ingredient was missing, the recipe can be afforded.
        return true;
    }

    /**
     * Create this recipe, consuming all ingredients and adding the result to the
     * storage.
     *
     * @param storage The storage to add to.
     */
    public void create(ItemStorage storage) {
        for (Item ingredient : this.ingredients) {
            for (int i = 0; i < storage.getSlots(); i++) {
                Item item = storage.getItem(i);
                if (item != null && item.getType() == ingredient.getType()) {
                    item.setAmount(item.getAmount() - ingredient.getAmount());
                }
            }
        }
        storage.filterEmpty();
        storage.add(this.result);
    }

    public Item getResult() {
        return result;
    }

    public List<Item> getIngredients() {
        return new ArrayList<>(ingredients);
    }
}

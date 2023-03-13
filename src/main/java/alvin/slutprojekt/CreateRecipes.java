package alvin.slutprojekt;

import alvin.slutprojekt.item.Item;
import alvin.slutprojekt.item.ItemTypes;

/**
 * Utility for registering the default create recipes.
 */
public class CreateRecipes {
    public static void register(Registry<CreateRecipe> recipeRegistry, ItemTypes itemTypes) {
        recipeRegistry.register(
            // id
            "mine",
            new CreateRecipe(
                // result
                new Item(itemTypes.MINE, 1),
                // ingredients
                new Item(itemTypes.WOOD, 20)
            )
        );
    }
}

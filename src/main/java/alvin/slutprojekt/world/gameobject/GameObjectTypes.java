package alvin.slutprojekt.world.gameobject;

import alvin.slutprojekt.Registry;

/**
 * Utility method for registering built in game objects in the game.
 */
public class GameObjectTypes {
    /**
     * Register a game object type to a registry.
     *
     * @param registry The registry to register to.
     * @param gameObjectType The game object type to register.
     */
    private static void register(Registry<GameObjectType<?>> registry, GameObjectType<?> gameObjectType) {
        registry.register(gameObjectType.getId(), gameObjectType);
    }

    /**
     * Register all built in game objects in the game to the registry.
     *
     * @param registry The registry to register to.
     */
    public static void register(Registry<GameObjectType<?>> registry) {
        register(registry, Player.TYPE);
        register(registry, Pig.TYPE);
    }
}

package alvin.slutprojekt.world.gameobject;

import alvin.slutprojekt.world.Environment;

/**
 * A {@link java.util.function.Function} for calling the constructor of a
 * {@link GameObject}.
 *
 * @param <T> The {@link GameObject} class.
 */
@FunctionalInterface
public interface GameObjectConstructor<T extends GameObject> {
    /**
     * Create a game object.
     *
     * @param environment The environment the game object is in.
     * @param x The x position of the game object.
     * @param y The y position of the game object.
     * @return The created game object.
     * @see GameObject#GameObject(Environment, double, double)
     */
    T create(Environment environment, double x, double y);
}

package alvin.slutprojekt.world.gameobject;

/**
 * A type of {@link GameObject}.
 *
 * @param <T> The class of the game object.
 */
public class GameObjectType<T extends GameObject> {
    private final String id;
    private final GameObjectConstructor<T> constructor;

    public GameObjectType(String id, GameObjectConstructor<T> constructor) {
        this.id = id;
        this.constructor = constructor;
    }

    public String getId() {
        return id;
    }

    public GameObjectConstructor<T> getConstructor() {
        return this.constructor;
    }
}

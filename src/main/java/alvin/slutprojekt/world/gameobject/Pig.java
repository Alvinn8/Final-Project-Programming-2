package alvin.slutprojekt.world.gameobject;

import alvin.slutprojekt.world.Environment;

/**
 * A pig animal.
 */
public class Pig extends Animal {
    public static final GameObjectType<Pig> TYPE = new GameObjectType<>("pig", Pig::new);

    public Pig(Environment environment, double x, double y) {
        super(environment, x, y);
    }

    @Override
    public GameObjectType<?> getType() {
        return TYPE;
    }
}

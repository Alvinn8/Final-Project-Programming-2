package alvin.slutprojekt.world;

/**
 * The type of layer in an {@link Environment}.
 */
public enum LayerType {
    /**
     * the background layer where ground tiles are.
     */
    BACKGROUND,
    /**
     * The main layer where breakable tiles are. The player usually collides
     */
    MAIN;
}

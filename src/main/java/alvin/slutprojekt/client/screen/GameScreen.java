package alvin.slutprojekt.client.screen;

/**
 * A screen that is visible to the user.
 *
 * @see GraphicsGameScreen
 * @see SwingGameScreen
 */
public interface GameScreen {
    /**
     * Called when the screen is enabled.
     */
    default void onEnable() {}
}

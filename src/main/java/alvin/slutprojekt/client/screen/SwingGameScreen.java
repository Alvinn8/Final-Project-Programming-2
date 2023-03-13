package alvin.slutprojekt.client.screen;

import javax.swing.*;

/**
 * A {@link GameScreen} that the window can render by placing swing components
 * on the screen.
 */
public interface SwingGameScreen extends GameScreen {
    /**
     * Get the {@link JPanel} to show when this screen is activated.
     *
     * @return The {@link JPanel}.
     */
    JPanel onShow();
}

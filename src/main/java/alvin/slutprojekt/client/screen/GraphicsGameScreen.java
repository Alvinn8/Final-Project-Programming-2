package alvin.slutprojekt.client.screen;

import alvin.slutprojekt.client.GameWindow;

import java.awt.*;

/**
 * A {@link GameScreen} that the window can render using a graphics object.
 */
public interface GraphicsGameScreen extends GameScreen {
    /**
     * Do the rendering for this frame.
     *
     * @param g The graphics object.
     * @param gameWindow The {@link GameWindow} that requested the render.
     */
    void render(Graphics g, GameWindow gameWindow);
}

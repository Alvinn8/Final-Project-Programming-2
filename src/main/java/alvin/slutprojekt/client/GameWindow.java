package alvin.slutprojekt.client;

import alvin.slutprojekt.ExecutionQueue;
import alvin.slutprojekt.client.screen.GameScreen;
import alvin.slutprojekt.client.screen.GraphicsGameScreen;
import alvin.slutprojekt.client.screen.SwingGameScreen;

import javax.swing.*;
import java.awt.*;

/**
 * The main game window that renders the game.
 */
public class GameWindow extends JPanel {
    private final JFrame frame;
    private final KeyboardManager keyboardManager;
    private final MouseManager mouseManager;
    private final ExecutionQueue executionQueue;
    private GameScreen screen;
    private GraphicsGameScreen graphicsScreen;
    private JPanel swingGameScreenPanel;
    private long lastFrame;

    public GameWindow() {
        this.executionQueue = new ExecutionQueue();

        this.frame = new JFrame("Slutprojekt - Alvin");
        this.frame.add(this);
        this.frame.setSize(600, 400);
        this.frame.setLocationRelativeTo(null);
        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.mouseManager = new MouseManager(this);
        this.frame.addMouseListener(this.mouseManager);
        this.frame.addMouseWheelListener(this.mouseManager);

        this.keyboardManager = new KeyboardManager(this);
        this.frame.addKeyListener(this.keyboardManager);

        this.frame.setVisible(true);
        this.frame.setFocusable(true);
        boolean focus = this.frame.requestFocusInWindow();

        if (!focus) {
            System.out.println("Unable to get focus, there might be issues with keyboard input!");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Clear the screen
        super.paintComponent(g);

        // Update cached mouse position
        this.mouseManager.tick();

        // Render the screen
        if (this.graphicsScreen != null) {
            this.graphicsScreen.render(g, this);
        }

        // Run executions
        synchronized (this.executionQueue) {
            this.executionQueue.run();
        }

        // Update the next frame
        waitForFpsLimit();
        repaint();
    }

    /**
     * Get the amount of frames per second the game should run ideally at.
     *
     * @return The frame count per second.
     */
    private int getFrameRate() {
        if (this.frame.isFocused() || this.frame.isActive()) {
            return 60;
        } else {
            // If the game window does not have focus, make the frame
            // rate 1 fps to not slow down the computer unnecessarily.
            return 1;
        }
    }

    /**
     * Get the amount of milliseconds a frame should last for.
     *
     * @return The milliseconds.
     */
    private int getFrameTime() {
        return 1000 / this.getFrameRate();
    }

    /**
     * If the frame rendered faster than the maximum time allowed, sleep the remaining
     * milliseconds. This avoids the game running on a higher frame rate than the set
     * maximum.
     */
    private void waitForFpsLimit() {
        long timeSinceLast = System.currentTimeMillis() - this.lastFrame;
        long timeToSleep = this.getFrameTime() - timeSinceLast;
        if (timeToSleep > 0) {
            try {
                Thread.sleep(timeToSleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
        this.lastFrame = System.currentTimeMillis();
    }

    /**
     * Add a runnable that will run on this thread the next frame.
     *
     * @param runnable The runnable to run.
     */
    public void runOnRenderThread(Runnable runnable) {
        synchronized (this.executionQueue) {
            this.executionQueue.add(runnable);
        }
    }

    /**
     * Get the {@link JFrame} instance for with this game window.
     *
     * @return The {@link JFrame}.
     */
    public JFrame getFrame() {
        return this.frame;
    }

    /**
     * Get the current {@link GameScreen} that is being rendered on this window.
     *
     * @return The screen.
     */
    public GameScreen getScreen() {
        return this.screen;
    }

    /**
     * Set the {@link GameScreen} that should render on this window.
     *
     * @param screen The screen to set.
     */
    public void setScreen(GameScreen screen) {
        // Remove old screen
        if (this.screen instanceof SwingGameScreen) {
            if (this.swingGameScreenPanel != null) {
                this.frame.remove(this.swingGameScreenPanel);
                this.swingGameScreenPanel = null;
            }
        } else if (this.screen instanceof GraphicsGameScreen) {
            this.graphicsScreen = null;
        }
        this.removeAll();
        // Set new screen
        this.screen = screen;
        if (screen instanceof SwingGameScreen) {
            // For a swing game screen we need to add the JPanel provided by
            // the screen
            this.swingGameScreenPanel = ((SwingGameScreen) screen).onShow();
            this.frame.remove(this);
            this.frame.add(this.swingGameScreenPanel);
        } else if (screen instanceof GraphicsGameScreen) {
            // A graphics screen needs this GameWindow instance on the frame
            // so the paintComponent method is called which is forwarded to
            // the GraphicsGameScreen.
            this.graphicsScreen = (GraphicsGameScreen) screen;
            this.frame.add(this);
            this.repaint();
        } else {
            throw new IllegalArgumentException("Unknown type of screen: " + screen);
        }
        this.screen.onEnable();
        this.frame.validate();
    }

    public KeyboardManager getKeyboardManager() {
        return this.keyboardManager;
    }

    public MouseManager getMouseManager() {
        return this.mouseManager;
    }
}

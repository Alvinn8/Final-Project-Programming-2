package alvin.slutprojekt.client;

import alvin.slutprojekt.client.screen.GameScreen;
import alvin.slutprojekt.client.screen.PauseScreen;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Listens for keyboard events and provides methods for checking which keys are
 * being pressed.
 */
public class KeyboardManager implements KeyListener {
    private final GameWindow gameWindow;
    private ClientMain main;
    private final Set<Character> keysDown = new HashSet<>();
    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;
    private boolean placing;
    private boolean breaking;

    public KeyboardManager(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        this.keysDown.add(e.getKeyChar());
        set(e, true);

        // Escape = pause
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            GameScreen prevScreen = this.gameWindow.getScreen();
            this.gameWindow.setScreen(new PauseScreen(this.main, prevScreen));
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        this.keysDown.remove(e.getKeyChar());
        set(e, false);
    }

    private void set(KeyEvent e, boolean value) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                this.up = value;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                this.down = value;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                this.left = value;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                this.right = value;
                break;
            case KeyEvent.VK_P:
                this.placing = value;
                break;
            case KeyEvent.VK_B:
                this.breaking = value;
                break;
        }
    }

    /**
     * Check whether the user is pressing the key for a specified character.
     *
     * @deprecated Prefer the named methods instead of passing the key. They work
     * when other keys like shift, ctrl, etc. are also pressed.
     *
     * @param c The character.
     * @return Whether this user is pressing the key.
     */
    @Deprecated
    public boolean isPressed(char c) {
        return this.keysDown.contains(c);
    }


    public boolean isPressingUp() {
        return up;
    }

    public boolean isPressingDown() {
        return down;
    }

    public boolean isPressingLeft() {
        return left;
    }

    public boolean isPressingRight() {
        return right;
    }

    public boolean isPressingPlace() {
        return placing;
    }

    public boolean isPressingBreak() {
        return breaking;
    }

    public void setMain(ClientMain main) {
        this.main = main;
    }
}

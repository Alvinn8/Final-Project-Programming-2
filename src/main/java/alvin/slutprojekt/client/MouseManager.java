package alvin.slutprojekt.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class MouseManager extends MouseAdapter {
    private final GameWindow gameWindow;
    private int mouseX;
    private int mouseY;
    private boolean leftClicking = false;
    private boolean rightClicking = false;
    private int scrollAmount;

    public MouseManager(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }

    /**
     * Tick the mouse manager, updating the cached mouse position.
     */
    public void tick() {
        Point point = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(point, this.gameWindow);
        this.mouseX = point.x;
        this.mouseY = point.y;
    }

    @Override
    public void mousePressed(MouseEvent event) {
        set(event, true);
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        set(event, false);
    }

    private void set(MouseEvent event, boolean value) {
        // On Mac computers without a mouse, Ctrl + click equals a right click
        if (SwingUtilities.isRightMouseButton(event)
            || (Device.IS_MAC && SwingUtilities.isLeftMouseButton(event) && event.isControlDown()))
        {
            this.rightClicking = value;
        }
        else if (SwingUtilities.isLeftMouseButton(event)) {
            this.leftClicking = value;
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        this.scrollAmount += e.getUnitsToScroll();
    }

    /**
     * Get the amount to scroll by (mouse wheel) and reset the stored value.
     *
     * @return The scroll amount.
     */
    public int consumeScrollAmount() {
        int tmp = this.scrollAmount;
        this.scrollAmount = 0;
        return tmp;
    }

    /**
     * Get the current x coordinate of the mouse, relative to the {@link GameWindow}.
     * <p>
     * (0,0) is the top left corner of the game screen.
     *
     * @return The mouse x coordinate.
     */
    public int getMouseX() {
        return mouseX;
    }

    /**
     * Get the current y coordinate of the mouse, relative to the {@link GameWindow}.
     * <p>
     * (0,0) is the top left corner of the game screen.
     *
     * @return The mouse y coordinate.
     */
    public int getMouseY() {
        return mouseY;
    }

    /**
     * Whether the user is holding down the left mouse button right now.
     *
     * @return Clicking or not.
     */
    public boolean isLeftClicking() {
        return leftClicking;
    }

    /**
     * Whether the user is holding down the right mouse button right now.
     *
     * @return Clicking or not.
     */
    public boolean isRightClicking() {
        return rightClicking;
    }
}

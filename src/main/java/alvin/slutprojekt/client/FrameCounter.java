package alvin.slutprojekt.client;

import java.awt.*;

/**
 * Counts the frames per second.
 */
public class FrameCounter {
    private int frameCount;
    private long lastSecond = System.currentTimeMillis();
    private int framesPerSecond = -1;

    public void render(Graphics g) {
        frameCount++;
        long now = System.currentTimeMillis();
        if (now - lastSecond > 1000) {
            framesPerSecond = frameCount;
            frameCount = 0;
            lastSecond = now;
        }
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("fps: " + framesPerSecond, 10, 10);
    }
}

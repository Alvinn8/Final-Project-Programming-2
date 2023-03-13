package alvin.slutprojekt.client.screen;

import alvin.slutprojekt.TransitioningDouble;
import alvin.slutprojekt.client.GameWindow;

import java.awt.*;

public class LoadingScreen implements GraphicsGameScreen {
    public static double LOADING_BAR_X_MARGIN = 0.1;
    public static int LOADING_BAR_HEIGHT = 40;
    public static double TOTAL_COUNT = 5.0;

    private final TransitioningDouble progress = new TransitioningDouble(0.0, 0.2);
    private String majorText = "";
    private String minorText = "";
    private int counter;

    @Override
    public void render(Graphics g, GameWindow gameWindow) {
        progress.set(counter / TOTAL_COUNT);
        progress.tick();

        int windowWidth = gameWindow.getWidth();

        int marginX = (int) (windowWidth * LOADING_BAR_X_MARGIN);
        int y = gameWindow.getHeight() / 2 - LOADING_BAR_HEIGHT / 2;
        int barWidth = windowWidth - 2 * marginX;
        int filledWidth = (int) Math.ceil(barWidth * progress.get());

        // Bar background
        g.setColor(Color.BLACK);
        g.fillRect(marginX, y, barWidth, LOADING_BAR_HEIGHT);
        // Filled bar
        g.setColor(Color.GREEN.darker());
        g.fillRect(marginX, y, filledWidth, LOADING_BAR_HEIGHT);

        if (progress.get() > 0.1) {
            g.setColor(Color.BLACK);

            // Major text
            g.drawString(this.majorText, marginX, y + 50);

            // Minor text
            g.drawString(this.minorText, marginX, y + 75);
        }
    }

    public void incrementCounter() {
        this.counter++;
        // try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    public void stage(String majorText) {
        this.majorText = majorText;
        this.minorText = "";
        this.incrementCounter();
    }

    public TransitioningDouble getProgress() {
        return this.progress;
    }

    public String getMajorText() {
        return majorText;
    }

    public void setMajorText(String majorText) {
        this.majorText = majorText;
    }

    public String getMinorText() {
        return minorText;
    }

    public void setMinorText(String minorText) {
        this.minorText = minorText;
    }
}

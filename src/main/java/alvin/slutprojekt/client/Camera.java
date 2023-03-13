package alvin.slutprojekt.client;

import alvin.slutprojekt.TransitioningDouble;

public class Camera {
    private final GameWindow gameWindow;
    private double cameraX;
    private double cameraY;
    private final TransitioningDouble zoom = new TransitioningDouble(1, 0.15);

    public Camera(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }

    public double getX() {
        return this.cameraX;
    }

    public double getY() {
        return this.cameraY;
    }

    public void setX(double x) {
        this.cameraX = x;
    }

    public void setY(double y) {
        this.cameraY = y;
    }

    public TransitioningDouble getZoom() {
        return this.zoom;
    }

    public int getRenderX(double x) {
        return (int) Math.floor(this.gameWindow.getWidth() / 2.0 + ((this.cameraX + x) / this.getZoom().get()));
    }

    public int getRenderY(double y) {
        return (int) Math.floor(this.gameWindow.getHeight() / 2.0 + ((this.cameraY + y) / this.getZoom().get()));
    }

    public int getRenderWidth(double width) {
        return (int) Math.ceil(width / this.getZoom().get());
    }

    public int getRenderHeight(double height) {
        return (int) Math.ceil(height / this.getZoom().get());
    }
}

package alvin.slutprojekt.world.gameobject;

import alvin.slutprojekt.TickingManager;
import alvin.slutprojekt.world.Environment;

public abstract class Animal extends GameObject {
    public static final double MOVEMENT_SPEED = 0.5 / TickingManager.TICKS_PER_SECOND;

    private int nextRandomTicksLeft;
    private double dirX;
    private double dirY;

    public Animal(Environment environment, double x, double y) {
        super(environment, x, y);
    }

    @Override
    public void tick() {
        nextRandomTicksLeft--;
        if (nextRandomTicksLeft <= 0) {
            directionRad = Math.random() * 2 * Math.PI;
            dirX = Math.cos(directionRad);
            dirY = Math.sin(directionRad);
            dirX *= MOVEMENT_SPEED;
            dirY *= MOVEMENT_SPEED;
            nextRandomTicksLeft = 3 * TickingManager.TICKS_PER_SECOND;
        }

        move(dirX, dirY);
    }
}

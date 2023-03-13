package alvin.slutprojekt;

/**
 * An object responsible for ticking the game.
 */
public class TickingManager {
    public static final int TICKS_PER_SECOND = 10;
    public static final int MS_PER_TICK = 1000 / TICKS_PER_SECOND;

    private final ExecutionQueue executionQueue = new ExecutionQueue();
    private long lastTick;
    private Tickable tickable;

    /**
     * Start the tick loop.
     * <p>
     * This method will not return, and will block the thread for the rest of the
     * execution of the game. Therefore, this should be called on a thread
     * specifically for ticking.
     */
    public void startTickLoop() {
        while (!Thread.interrupted()) {
            // Tick
            if (tickable != null) {
                tickable.tick();
            }

            // Run executions
            synchronized (executionQueue) {
                executionQueue.run();
            }

            // Wait for next tick
            waitForNextTick();
        }
    }

    /**
     * Sleep for however many milliseconds to ensure the game runs at the correct speed.
     */
    private void waitForNextTick() {
        long timeSinceLast = System.currentTimeMillis() - this.lastTick;
        long timeToSleep = MS_PER_TICK - timeSinceLast;
        if (timeToSleep > 0) {
            try {
                Thread.sleep(timeToSleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
        this.lastTick = System.currentTimeMillis();
    }

    /**
     * Get the timestamp when the game was last ticked.
     *
     * @return The timestamp of the last tick.
     */
    public long getLastTick() {
        return this.lastTick;
    }

    /**
     * Add a runnable that will run on this thread the next tick.
     *
     * @param runnable The runnable to run.
     */
    public void runOnTickingThread(Runnable runnable) {
        synchronized (this.executionQueue) {
            this.executionQueue.add(runnable);
        }
    }

    /**
     * Set the thing that should be ticked.
     *
     * @param tickable The thing to tick.
     */
    public void setTicking(Tickable tickable) {
        this.tickable = tickable;
    }
}

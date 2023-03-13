package alvin.slutprojekt;

import java.util.LinkedList;
import java.util.Queue;

/**
 * A queue with {@link Runnable}s that should be run.
 * <p>
 * This is used to schedule code to be run on other threads.
 */
public class ExecutionQueue {
    private final Queue<Runnable> queue = new LinkedList<>();

    /**
     * Add a runnable that should be run by this execution queue.
     *
     * @param runnable The runnable to run.
     */
    public void add(Runnable runnable) {
        this.queue.offer(runnable);
    }

    /**
     * Run the code that has been added to the queue.
     */
    public void run() {
        Runnable runnable;
        while ((runnable = this.queue.poll()) != null) {
            runnable.run();
        }
    }
}

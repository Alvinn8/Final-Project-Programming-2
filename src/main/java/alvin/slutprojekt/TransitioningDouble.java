package alvin.slutprojekt;

/**
 * A double that will smoothly transition between values.
 */
public class TransitioningDouble {
    private double transitionFactor;
    private double current;
    private double desired;

    /**
     * Create a new TransitioningDouble.
     * <p>
     * The transition factor determines how quickly the current value will transition
     * to the desired value. Higher values transition faster.
     *
     * @param startValue The start value.
     * @param transitionFactor The transition factor [0-1].
     */
    public TransitioningDouble(double startValue, double transitionFactor) {
        this.current = startValue;
        this.desired = startValue;
        this.transitionFactor = transitionFactor;
    }

    /**
     * Get the current value to use. Will be transitioning if required.
     *
     * @return The double value.
     */
    public double get() {
        return this.current;
    }

    /**
     * Set the desired value, the value will transition to this value.
     *
     * @param desired The value to transition to.
     */
    public void set(double desired) {
        this.desired = desired;
    }

    /**
     * Ann an amount to the desired value.
     *
     * @param amount The value to add.
     * @see #set(double)
     */
    public void add(double amount) {
        this.desired += amount;
    }

    /**
     * Get the current desired value.
     *
     * @return The desired value.
     */
    public double getDesired() {
        return this.desired;
    }

    /**
     * Tick the transition, causing the current value to transition towards the
     * desired value if they differ.
     */
    public void tick() {
        double diff = this.desired - this.current;
        this.current += diff * this.transitionFactor;
    }

    public double getTransitionFactor() {
        return transitionFactor;
    }

    public void setTransitionFactor(double transitionFactor) {
        this.transitionFactor = transitionFactor;
    }
}

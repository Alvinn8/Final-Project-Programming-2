package alvin.slutprojekt;

/**
 * Utility math methods.
 */
public class MathUtil {
    private MathUtil() {}

    /**
     * Linearly interpolate between two values.
     * <p>
     * A delta of 0 returns {@code a}, and a delta of 1 returns {@code b}.
     *
     * @param a The first value.
     * @param b The second value.
     * @param delta How much of the way we are from a to b [0-1].
     * @return The linearly interpolated value.
     */
    public static double lerp(double a, double b, double delta) {
        return a + delta * (b - a);
    }

    /**
     * Linearly interpolate between two angles.
     * <p>
     * This will take into account that an angle at -179° and 181° are only 2° from
     * each other and will therefore spin in that direction instead of going around
     * an entire revolution.
     *
     * @param a The first angle.
     * @param b The second angle.
     * @param delta How much of the way we are from a to b [0-1].
     * @return The linearly interpolated angle.
     * @see #lerp(double, double, double)
     */
    public static double lerpAngle(double a, double b, double delta) {
        double difference = b - a;
        if (difference < -Math.PI) {
            difference += 2 * Math.PI;
        }
        if (difference >= Math.PI) {
            difference -= 2 * Math.PI;
        }
        return a + delta * difference;
    }

    /**
     * Fix slight floating point precision errors by ensuring only a few decimals are
     * kept.
     *
     * @param value The double precision floating point value.
     * @return The fixed value.
     */
    public static double fixFloatingPointError(double value) {
        final double FACTOR = 10000000;
        return Math.round(value * FACTOR) / FACTOR;
    }
}

package alvin.slutprojekt.noise;

import java.util.Random;

/**
 * Provides predictable 2D perlin noise.
 * <p>
 * Passing the same seed and same coordinates will yield the same result each time.
 * <p>
 * Implementation based on the algorithm from
 * <a href="https://en.wikipedia.org/wiki/Perlin_noise">Perlin noise - Wikipedia</a>
 * [Read 2022-05-24].
 */
public class Noise {
    private static final double HALF_SQRT_2 = Math.sqrt(2) / 2;

    private final Random sharedRandom = new Random();
    private final long seed;

    /**
     * Create a new Noise instance with a seed.
     *
     * @param seed The seed to use for this Noise instance.
     */
    public Noise(long seed) {
        this.seed = seed;
    }

    /**
     * Get the noise from the specified coordinate.
     * <p>
     * If the same seed has been passed to a noise instance, passing the same x and y
     * values to this method will yield the same result.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return The value, roughly in the range [0-1].
     */
    public double noise(double x, double y) {
        // Get the cell coordinates around the point
        int lowerX = (int) Math.floor(x);
        int upperX = lowerX + 1;
        int lowerY = (int) Math.floor(y);
        int upperY = lowerY + 1;

        // How much have we moved from the lower x?
        // This is used for interpolation.
        double deltaX = x - lowerX;
        double deltaY = y - lowerY;

        // Calculate the values for the 4 corners
        double topLeft = cornerGradient(lowerX, lowerY, x, y);
        double topRight = cornerGradient(upperX, lowerY, x, y);
        double bottomLeft = cornerGradient(lowerX, upperY, x, y);
        double bottomRight = cornerGradient(upperX, upperY, x, y);

        // Interpolate the x for the top and bottom
        double topX = interpolate(topLeft, topRight, deltaX);
        double bottomX = interpolate(bottomLeft, bottomRight, deltaX);

        // Interpolate the top and bottom
        double value = interpolate(topX, bottomX, deltaY);

        // The value roughly goes from [-sqrt(2)/2, sqrt(2)/2], let's map it to [0, 1]
        return map(value, -HALF_SQRT_2, HALF_SQRT_2, 0, 1);
    }

    /**
     * Get the gradient value of a corner.
     *
     * @param cornerX The corner x coordinate.
     * @param cornerY The corner y coordinate.
     * @param x The x value of the point.
     * @param y The y value of the point.
     * @return The gradient value.
     */
    private double cornerGradient(int cornerX, int cornerY, double x, double y) {
        // gradient vector
        // Create a random unit vector by getting a random angle
        double rand = randomDouble(cornerX, cornerY);
        double angle = rand * 2 * Math.PI;
        // The angle is a random number between [0, 2pi]
        // Create a unit vector from that angle
        double gradientX = Math.cos(angle);
        double gradientY = Math.sin(angle);

        // distance vector
        // A vector from the coordinate to the corner
        double distanceX = x - cornerX;
        double distanceY = y - cornerY;

        // dot product of the distance vector and gradient vector
        // dot product = x1 * x2 + y1 * y2
        return (distanceX * gradientX + distanceY * gradientY);
    }

    /**
     * Interpolate between the two points using a fitting algorithm. Currently,
     * this method is implemented using {@link #smoothStep(double, double, double)}.
     *
     * @param a The start value.
     * @param b The end value.
     * @param delta How much of the way we are from a to b [0-1].
     * @return The interpolated value.
     * @see #smoothStep(double, double, double)
     */
    private double interpolate(double a, double b, double delta) {
        // The interpolation function can be changed to yield different smoothness to
        // the noise. A linear interpolation can for example be used instead for a
        // less smooth result. For an even smoother result the "smootherStep" function
        // can be used instead.
        //
        return smoothStep(a, b, delta);
    }

    /**
     * Interpolate between the two values using the SmoothStep algorithm.
     * <p>
     * <a href="https://en.wikipedia.org/wiki/Smoothstep">Smoothstep - Wikipedia</a> [Read 2022-05-24]
     * <p>
     * A delta of 0 returns {@code a}, and a delta of 1 returns {@code b}.
     *
     * @param a The start value to interpolate from.
     * @param b The end value to interpolate to.
     * @param delta How much of the way we are from a to b [0-1].
     * @return The smoothly interpolated value.
     */
    private double smoothStep(double a, double b, double delta) {
        return (b - a) * (3.0 - delta * 2.0) * delta * delta + a;
    }

    /**
     * Map the value between one range to another. From the [valMin, valMax] range to
     * the [newMin, newMax] range.
     *
     * @param value A value in the range [valMin, valMax].
     * @param valMin The minimum value.
     * @param valMax The maximum value.
     * @param newMin The new, mapped, minimum value.
     * @param newMax The new, mapped, maximum value.
     * @return The mapped value in the range [newMin, newMax].
     */
    private double map(double value, double valMin, double valMax, double newMin, double newMax) {
        double a = (value - valMin) / (valMax - valMin);
        return a * (newMax - newMin) + newMin;
    }

    /**
     * Get a predictable random number from a coordinate.
     * <p>
     * The seed specified to the constructor of this Noise instance will also affect
     * the resulting value.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return The pseudorandom random [0-1].
     */
    private double randomDouble(double x, double y) {
        // The constants here are random values to ensure the seed is different when
        // x and y only change slightly or in a way that would cancel the effect out.
        return randomDouble((long) (392 * (x + 1923) * (y + 836) + 937));
    }

    /**
     * Get a predictable random number from a seed.
     * <p>
     * The seed specified to the constructor of this Noise instance will also affect
     * the resulting value.
     *
     * @param seed The seed for the random number.
     * @return The pseudorandom number [0-1].
     */
    private double randomDouble(long seed) {
        // We reuse the random instance and use setSeed instead of creating
        // a new Random instance for each call to this method.
        sharedRandom.setSeed(seed * this.seed);
        return sharedRandom.nextDouble();
    }
}

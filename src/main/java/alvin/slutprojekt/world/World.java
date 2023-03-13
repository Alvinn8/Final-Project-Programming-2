package alvin.slutprojekt.world;

import java.util.Arrays;
import java.util.List;

/**
 * A world is the place where the user plays in the world.
 * <p>
 * A world consists of a number of {@link Environment}s.
 */
public class World {
    public static final String MAIN_ENV_ID = "main";
    public static final String UNDERGROUND_ENV_ID = "underground";

    private final String name;
    private long seed;
    private Environment mainEnvironment;
    private Environment undergroundEnvironment;

    public World(String name, long seed) {
        this.name = name;
        this.seed = seed;
    }

    /**
     * Get the id of the provided environment.
     * <p>
     * This is used to determine the folder to store the chunks in.
     *
     * @param environment The environment.
     * @return The id of the environment.
     * @throws IllegalArgumentException if the provided environment is not
     * a part of this world.
     */
    public String getEnvironmentId(Environment environment) {
        if (environment == this.mainEnvironment) {
            return MAIN_ENV_ID;
        }
        if (environment == this.undergroundEnvironment) {
            return UNDERGROUND_ENV_ID;
        }
        throw new IllegalArgumentException("The provided environment is not a part of this world.");
    }

    public Environment getEnvironmentById(String environmentId) {
        switch (environmentId) {
            case MAIN_ENV_ID: return this.mainEnvironment;
            case UNDERGROUND_ENV_ID: return this.undergroundEnvironment;
            default: throw new IllegalArgumentException("Unknown environment id: " + environmentId);
        }
    }
    /**
     * Get the name of the world.
     *
     * @return The name of the world.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the seed used for generation in this world.
     *
     * @return The seed.
     */
    public long getSeed() {
        return seed;
    }

    /**
     * Get the main environment where the user spawns by default, at the surface.
     *
     * @return The environment.
     */
    public Environment getMainEnvironment() {
        return mainEnvironment;
    }

    /**
     * Set the main environment.
     * <p>
     * Changing the environment after world creation is not supported.
     *
     * @param mainEnvironment The environment.
     */
    public void setMainEnvironment(Environment mainEnvironment) {
        this.mainEnvironment = mainEnvironment;
    }

    /**
     * Get the underground environment where the player can mine for minerals.
     *
     * @return The environment.
     */
    public Environment getUndergroundEnvironment() {
        return undergroundEnvironment;
    }

    /**
     * Set the underground environment.
     * <p>
     * Changing the environment after world creation is not supported.
     *
     * @param undergroundEnvironment The environment.
     */
    public void setUndergroundEnvironment(Environment undergroundEnvironment) {
        this.undergroundEnvironment = undergroundEnvironment;
    }

    /**
     * Get a list of all environments in this world.
     *
     * @return The list.
     */
    public List<Environment> getEnvironments() {
        return Arrays.asList(
            this.mainEnvironment,
            this.undergroundEnvironment
        );
    }
}

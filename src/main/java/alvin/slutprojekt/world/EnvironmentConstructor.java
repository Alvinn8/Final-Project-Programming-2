package alvin.slutprojekt.world;

import alvin.slutprojekt.AbstractMain;
import alvin.slutprojekt.world.chunk.ChunkProvider;

/**
 * A {@link java.util.function.Function} for creating an {@link Environment}.
 */
@FunctionalInterface
public interface EnvironmentConstructor {
    /**
     * Invoke the constructor of an {@link Environment} or subclass.
     *
     * @param world The world.
     * @param provider The chunk provider.
     * @return The constructed environment.
     */
    Environment create(AbstractMain main, World world, ChunkProvider provider);
}

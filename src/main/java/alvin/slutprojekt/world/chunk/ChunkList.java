package alvin.slutprojekt.world.chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * A list of chunks.
 * <p>
 * Will constantly sort the internal list to ensure getting chunks is fast via a
 * binary search.
 *
 * @param <T> The type of the list.
 */
public class ChunkList<T extends ChunkCoordinate> {
    private final List<T> chunks = new ArrayList<>();

    private T binarySearch(int chunkX, int chunkY) {
        int min = 0;
        int max = chunks.size() - 1;
        while (min <= max) {
            int mid = (min + max) / 2;
            T chunk = chunks.get(mid);
            int cmp = compare(chunk, chunkX, chunkY);
            if (cmp == 0) {
                return chunk;
            } else if (cmp < 0) {
                min = mid + 1;
            } else {
                max = mid - 1;
            }
        }
        return null;
    }

    private int compare(T chunk, int chunkX, int chunkY) {
        if (chunk == null) return -1;
        int x = Integer.compare(chunk.getChunkX(), chunkX);
        return (x != 0) ? x : Integer.compare(chunk.getChunkY(), chunkY);
    }

    private int compare(T a, T b) {
        return compare(a, b.getChunkX(), b.getChunkY());
    }

    /**
     * Add a chunk to the list and sort the list.
     *
     * @param chunk The chunk to add.
     */
    public synchronized void add(T chunk) {
        chunks.add(chunk);
        chunks.sort(this::compare);
    }

    /**
     * Get a chunk by its coordinates.
     *
     * @param chunkX The chunk x coordinate.
     * @param chunkY The chunk y coordinate.
     * @return The chunk, or null.
     */
    public synchronized T get(int chunkX, int chunkY) {
        return binarySearch(chunkX, chunkY);
    }

    /**
     * @see List#removeIf(Predicate)
     *
     * @param filter The filter.
     */
    public void removeIf(Predicate<T> filter) {
        chunks.removeIf(filter);
    }

    /**
     * Get the size of the list.
     *
     * @return The size of the list.
     * @see List#size()
     */
    public int size() {
        return chunks.size();
    }

    /**
     * Get a copy of the list of chunks.
     *
     * @return The list.
     */
    public List<T> getCopy() {
        return new ArrayList<>(this.chunks);
    }
}

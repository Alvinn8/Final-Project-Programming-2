package alvin.slutprojekt;

/**
 * A two-dimensional grid where values can be stored by an x and a y coordinate.
 * @param <T> The type of the elements in the grid.
 */
public class Grid<T> {
    private T[][] data;

    /**
     * Create a new grid.
     *
     * @param rows The amount of elements in the x axis.
     * @param cols The amount of elements in the y axis.
     */
    public Grid(int rows, int cols) {
        data = makeData(rows, cols);
    }

    // There is no way to create generic arrays, so we have to do an ugly cast. We
    // suppress the warning because there is no other way. ArrayList also does
    // something similar.
    @SuppressWarnings("unchecked")
    private T[][] makeData(int rows, int cols) {
        return (T[][]) new Object[rows][cols];
    }

    /**
     * Get the element at the specified coordinates.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return The element at the position, or null.
     * @throws ArrayIndexOutOfBoundsException If the x and y are out of bounds.
     */
    public T get(int x, int y) {
        return this.data[x][y];
    }

    /**
     * Set the element at the specified position.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param value The value to set at the coordinates.
     * @throws ArrayIndexOutOfBoundsException If the x and y are out of bounds.
     */
    public void set(int x, int y, T value) {
        this.data[x][y] = value;
    }
}

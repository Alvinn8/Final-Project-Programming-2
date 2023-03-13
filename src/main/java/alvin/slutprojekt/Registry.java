package alvin.slutprojekt;

import java.util.*;

/**
 * A registry where elements can be registered and referred to by string ids.
 */
public class Registry<T> {
    private final Map<String, T> data = new HashMap<>();

    /**
     * Register an element in the registry.
     *
     * @param id The id of the element.
     * @param element The element to register.
     */
    public void register(String id, T element) {
        this.data.put(id, element);
    }

    /**
     * Get the element with the specified id.
     *
     * @param id The id of the element.
     * @return The element.
     */
    public T get(String id) {
        return this.data.get(id);
    }

    /**
     * Get the key for a registered element.
     *
     * @param element The element.
     * @return The key for this element, or null if not registered.
     */
    public String getIdFor(T element) {
        for (Map.Entry<String, T> entry : this.data.entrySet()) {
            if (entry.getValue() == element) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Get a list of the elements that have been registered.
     *
     * @return The list of elements (a copy).
     */
    public List<T> getRegistered() {
        return new ArrayList<>(this.data.values());
    }
}

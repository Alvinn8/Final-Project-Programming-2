package alvin.slutprojekt.item;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * The player's or a chest's item storage.
 */
public class ItemStorage {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final Item[] items;

    public ItemStorage(int slots) {
        this.items = new Item[slots];
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    /**
     * Add (merge) an item into this storage, if it fits.
     *
     * @param item The item to add.
     * @return Whether the item fits or not.
     */
    public boolean add(Item item) {
        this.pcs.firePropertyChange("item addition", null, null);
        boolean merged = false;
        for (Item item1 : this.items) {
            if (item1 != null && item1.getType() == item.getType()) {
                item1.setAmount(item1.getAmount() + item.getAmount());
                merged = true;
                break;
            }
        }
        if (merged) {
            return true;
        }
        for (int i = 0; i < this.items.length; i++) {
            Item item1 = this.items[i];
            if (item1 == null) {
                this.items[i] = new Item(item.getType(), item.getAmount());
                return true;
            }
        }
        return false;
    }

    /**
     * Set an item at a specific index.
     *
     * @param index The index to set the item to.
     * @param item The item to set.
     */
    public void setItem(int index, Item item) {
        this.items[index] = item;
        this.pcs.firePropertyChange("item change", null, null);
    }

    /**
     * Get an item at the specified index.
     *
     * @param index The index.
     * @return The item, or null.
     */
    public Item getItem(int index) {
        return this.items[index];
    }

    /**
     * Get the index of the specified item.
     * <p>
     * Will compare using reference equality.
     *
     * @param item The item to get the index of.
     * @return The index, or -1.
     */
    public int indexOf(Item item) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == item) {
                return i;
            }
        }
        return -1;
    }

    public int getSlots() {
        return items.length;
    }

    /**
     * Remove all empty items.
     */
    public void filterEmpty() {
        for (int i = 0; i < items.length; i++) {
            Item item = items[i];
            if (item != null && item.getAmount() <= 0) {
                items[i] = null;
            }
        }
        pcs.firePropertyChange("filter", null, null);
    }
}

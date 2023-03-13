package alvin.slutprojekt.item;

import alvin.slutprojekt.Registry;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * An item that a player can hold and have in their storage.
 */
public class Item {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private ItemType type;
    private int amount;

    public Item(ItemType type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        ItemType oldType = this.type;
        this.type = type;
        this.pcs.firePropertyChange("type", oldType, this.type);
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        int oldAmount = this.amount;
        this.amount = amount;
        this.pcs.firePropertyChange("amount", oldAmount, this.amount);
    }

    /**
     * Write an item to a {@link DataOutputStream}.
     *
     * @param stream The stream to write to.
     * @param item The item to write, may be null.
     * @throws IOException If an I/O error occurs while writing.
     */
    public static void writeItem(DataOutputStream stream, Item item) throws IOException {
        if (item == null) {
            stream.writeBoolean(false);
        } else {
            stream.writeBoolean(true);
            stream.writeUTF(item.getType().getId());
            stream.writeInt(item.getAmount());
        }
    }

    /**
     * Read an item from a {@link DataInputStream}.
     *
     * @param stream The stream to read from.
     * @param itemTypeRegistry The registry to get item types from.
     * @return The read item, can be null.
     * @throws IOException If an I/O error occurs while reading.
     */
    public static Item readItem(DataInputStream stream, Registry<ItemType> itemTypeRegistry) throws IOException {
        boolean exists = stream.readBoolean();
        if (!exists) {
            return null;
        }
        String itemTypeId = stream.readUTF();
        ItemType itemType = itemTypeRegistry.get(itemTypeId);

        int amount = stream.readInt();
        return new Item(itemType, amount);
    }
}

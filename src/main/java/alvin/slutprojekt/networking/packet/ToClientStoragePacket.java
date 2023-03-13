package alvin.slutprojekt.networking.packet;

import alvin.slutprojekt.Registry;
import alvin.slutprojekt.client.server.ClientSidePacketHandler;
import alvin.slutprojekt.item.Item;
import alvin.slutprojekt.item.ItemStorage;
import alvin.slutprojekt.item.ItemType;
import alvin.slutprojekt.networking.PacketType;
import alvin.slutprojekt.networking.ToClientPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ToClientStoragePacket implements ToClientPacket {
    public static final PacketType<ToClientPacket> TYPE = new PacketType<>("storage", ToClientStoragePacket::new);

    private final ItemData[] items;

    public ToClientStoragePacket(ItemStorage storage) {
        this.items = new ItemData[storage.getSlots()];
        for (int i = 0; i < storage.getSlots(); i++) {
            Item item = storage.getItem(i);
            if (item != null) {
                this.items[i] = new ItemData(item.getType().getId(), item.getAmount());
            }
        }
    }

    public ToClientStoragePacket(DataInputStream stream) throws IOException {
        int slots = stream.readInt();
        this.items = new ItemData[slots];
        for (int i = 0; i < slots; i++) {
            boolean exists = stream.readBoolean();
            if (exists) {
                String itemTypeId = stream.readUTF();
                int amount = stream.readInt();
                this.items[i] = new ItemData(itemTypeId, amount);
            }
            // false = there is no item, leave it as null
        }
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeInt(this.items.length);
        for (ItemData item : this.items) {
            if (item == null) {
                // false = there is no item
                stream.writeBoolean(false);
            } else {
                // true = there is an item
                stream.writeBoolean(true);
                stream.writeUTF(item.typeId);
                stream.writeInt(item.amount);
            }
        }
    }

    @Override
    public PacketType<ToClientPacket> getType() {
        return TYPE;
    }

    @Override
    public void handle(ClientSidePacketHandler handler) {
        handler.handleStorage(this);
    }

    public int getSlots() {
        return this.items.length;
    }

    /**
     * Get an item from this packet.
     *
     * @param index The index of the item in the storage.
     * @param itemTypeRegistry The registry to get item types from.
     * @return The item, or null.
     */
    public Item getItem(int index, Registry<ItemType> itemTypeRegistry) {
        ItemData item = this.items[index];
        if (item == null) {
            return null;
        }
        ItemType itemType = itemTypeRegistry.get(item.typeId);
        return new Item(itemType, item.amount);
    }

    private static class ItemData {
        private final String typeId;
        private final int amount;

        public ItemData(String typeId, int amount) {
            this.typeId = typeId;
            this.amount = amount;
        }
    }
}

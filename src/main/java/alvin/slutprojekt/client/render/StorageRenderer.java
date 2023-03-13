package alvin.slutprojekt.client.render;

import alvin.slutprojekt.client.UserPlayer;
import alvin.slutprojekt.item.Item;
import alvin.slutprojekt.item.ItemStorage;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Renders the player's item storage on the right.
 */
public class StorageRenderer implements PropertyChangeListener {
    public static final int ITEM_SIZE = 60;

    private final ItemRenderer itemRenderer;
    private final UserPlayer player;
    private JPanel panel;
    private ItemStorage storage;
    private boolean shouldReRender = false;

    public StorageRenderer(UserPlayer player, ItemRenderer itemRenderer, ItemStorage storage) {
        this.player = player;
        this.itemRenderer = itemRenderer;
        this.storage = storage;
        this.storage.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        shouldReRender = true;
    }

    public JPanel render() {
        shouldReRender = false;

        if (panel == null) {
            panel = new JPanel();
        } else {
            this.panel.removeAll();
        }
        panel.setFocusable(false);
        panel.setLayout(new GridBagLayout());
        panel.setPreferredSize(new Dimension(ITEM_SIZE * 2, ITEM_SIZE * 5));

        int x = 0;
        int y = 0;
        for (int i = 0; i < this.storage.getSlots(); i++) {
            JPanel component;
            Item item = this.storage.getItem(i);
            if (item != null) {
                boolean selected = this.player.getSelectedItem() == item;
                ItemComponent itemComponent = new ItemComponent(this.itemRenderer, item, selected);
                itemComponent.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        itemComponent.setSelected(true);
                        player.setSelectedItem(item);
                    }
                });
                component = itemComponent;
            } else {
                component = new JPanel();
                component.setBorder(new LineBorder(Color.LIGHT_GRAY));
            }
            component.setPreferredSize(new Dimension(ITEM_SIZE, ITEM_SIZE));

            GridBagConstraints con = new GridBagConstraints();
            con.gridx = x;
            con.gridy = y;
            con.weightx = 1;
            con.weighty = 1;
            con.fill = GridBagConstraints.BOTH;

            panel.add(component, con);

            y++;
            if (y > 4) {
                x += 1;
                y = 0;
            }
        }
        panel.validate();
        return panel;
    }

    public ItemStorage getStorage() {
        return storage;
    }

    public void setStorage(ItemStorage storage) {
        this.storage = storage;
    }

    public boolean shouldReRender() {
        return shouldReRender;
    }
}

package alvin.slutprojekt.client.render;

import alvin.slutprojekt.item.Item;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A graphical component that renders an item.
 */
public class ItemComponent extends JPanel implements PropertyChangeListener {
    private final ItemRenderer renderer;
    private final Item item;
    private boolean selected;

    public ItemComponent(ItemRenderer renderer, Item item, boolean selected) {
        this.renderer = renderer;
        this.item = item;
        this.selected = selected;
        this.item.addPropertyChangeListener(this);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.recreate();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        recreate();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        this.item.removePropertyChangeListener(this);
    }

    /**
     * Recreate this component, updating it.
     */
    public void recreate() {
        this.removeAll();
        this.setBorder(new LineBorder(this.selected ? Color.BLACK : Color.LIGHT_GRAY));
        JLabel image = new JLabel(this.renderer.getItemTexture(this.item.getType()));
        JLabel amount = new JLabel(this.item.getAmount() + "x " + this.item.getType().getName());
        amount.setFont(new Font("SansSerif", Font.PLAIN, 10));
        this.add(image);
        this.add(amount);
        this.validate();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        this.recreate();
    }
}

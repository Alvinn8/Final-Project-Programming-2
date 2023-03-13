package alvin.slutprojekt.client.render;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Renders the sidebar on the right.
 */
public class SidebarRenderer {
    private final StorageRenderer storageRenderer;
    private final CreateTabRenderer createTabRenderer;

    public SidebarRenderer(StorageRenderer storageRenderer, CreateTabRenderer createTabRenderer) {
        this.storageRenderer = storageRenderer;
        this.createTabRenderer = createTabRenderer;
    }

    public JPanel render() {
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel storageTab = storageRenderer.render();
        JPanel createTab = createTabRenderer.render();
        tabbedPane.addChangeListener(event -> {
            // Only when the create tab is selected should the sidebar be a larger size,
            // otherwise stay at the storage size to ensure the JTabbedPane renders at
            // that size.
            if (tabbedPane.getSelectedComponent() == storageTab) {
                createTab.setPreferredSize(new Dimension(StorageRenderer.ITEM_SIZE * 2, StorageRenderer.ITEM_SIZE * 5));
            } else {
                createTab.setPreferredSize(new Dimension(250, StorageRenderer.ITEM_SIZE * 5));
            }
            tabbedPane.validate();
        });
        tabbedPane.addTab("Förvaring", storageTab);
        tabbedPane.addTab("Skapa", createTab);
        JPanel panel = new JPanel();
        panel.add(tabbedPane);
        return panel;
    }

    public StorageRenderer getStorageRenderer() {
        return storageRenderer;
    }
}

package alvin.slutprojekt.client.screen;

import javax.swing.*;
import java.awt.*;

public class TextScreen implements SwingGameScreen {
    private final JLabel label = new JLabel();

    public TextScreen() {
    }

    public TextScreen(String text) {
        this.setText(text);
    }

    @Override
    public JPanel onShow() {
        JPanel panel = new JPanel();

        panel.setLayout(new GridBagLayout());

        panel.add(this.label, new GridBagConstraints());

        return panel;
    }

    public void setText(String text) {
        this.label.setText(text);
    }
}

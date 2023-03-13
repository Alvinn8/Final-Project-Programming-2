package alvin.slutprojekt.client.screen;

import alvin.slutprojekt.client.ClientMain;

import javax.swing.*;
import java.awt.*;

public class TextScreenWithBack implements SwingGameScreen {
    private final ClientMain main;
    private final JLabel label = new JLabel();

    public TextScreenWithBack(ClientMain main) {
        this.main = main;
    }

    public TextScreenWithBack(ClientMain main, String text) {
        this(main);
        this.setText(text);
    }

    @Override
    public JPanel onShow() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        panel.add(this.label, new GridBagConstraints());

        GridBagConstraints con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 1;
        con.insets = new Insets(50, 10, 50, 10);
        JButton button = new JButton("Tillbaka till startskärmen");
        panel.add(button, con);

        button.addActionListener(evt -> back());

        return panel;
    }

    public void setText(String text) {
        this.label.setText(text);
    }

    private void back() {
        this.main.getGameWindow().setScreen(new StartScreen(this.main));
    }
}

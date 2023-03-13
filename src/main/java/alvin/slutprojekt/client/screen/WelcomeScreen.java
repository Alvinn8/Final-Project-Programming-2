package alvin.slutprojekt.client.screen;

import alvin.slutprojekt.client.ClientMain;
import alvin.slutprojekt.client.GameWindow;
import alvin.slutprojekt.client.ClientStorage;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class WelcomeScreen implements SwingGameScreen {
    private final ClientMain main;
    private final GameWindow gameWindow;
    private final ClientStorage storage;

    public WelcomeScreen(ClientMain main, GameWindow gameWindow, ClientStorage storage) {
        this.main = main;
        this.gameWindow = gameWindow;
        this.storage = storage;
    }

    @Override
    public JPanel onShow() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints con;

        JLabel title = new JLabel("Välkommen!", JLabel.CENTER);
        title.setFont(new Font("SansSerif", Font.PLAIN, 16));
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 0;
        con.fill = GridBagConstraints.HORIZONTAL;
        panel.add(title, con);

        JLabel subtitle = new JLabel("Välkommen till Alvins Slutprojekt i Programmering 2", JLabel.CENTER);
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 1;
        con.fill = GridBagConstraints.HORIZONTAL;
        con.insets = new Insets(0, 0, 50, 0);
        panel.add(subtitle, con);

        JLabel text1 = new JLabel("Projektet kommer spara filer i följande mapp på din dator:");
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 2;
        con.fill = GridBagConstraints.HORIZONTAL;
        panel.add(text1, con);

        JTextField text2 = new JTextField(storage.getRoot().toString());
        text2.setEditable(false);
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 3;
        con.fill = GridBagConstraints.HORIZONTAL;
        panel.add(text2, con);

        JLabel text3 = new JLabel(
            "<html>Om du avinstallerar projektet kan du radera denna mapp så försvinner all sparad data.<br>" +
            "Då försvinner dina världar och inställningar.</html>"
        );
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 4;
        con.fill = GridBagConstraints.HORIZONTAL;
        panel.add(text3, con);

        JButton button = new JButton("OK");
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 5;
        panel.add(button, con);

        button.addActionListener(event -> {
            try {
                storage.create();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Kunde inte skapa mappen.",
                    JOptionPane.ERROR_MESSAGE
                );
                e.printStackTrace();
            }

            this.gameWindow.setScreen(new SetNameScreen(this.main, this.gameWindow, this.storage));
        });

        return panel;
    }
}

package alvin.slutprojekt.client.screen;

import alvin.slutprojekt.client.ClientMain;
import alvin.slutprojekt.client.GameWindow;
import alvin.slutprojekt.client.ClientStorage;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * The screen where the user sets their name.
 */
public class SetNameScreen implements SwingGameScreen {
    private final ClientMain main;
    private final GameWindow gameWindow;
    private final ClientStorage storage;

    public SetNameScreen(ClientMain main, GameWindow gameWindow, ClientStorage storage) {
        this.main = main;
        this.gameWindow = gameWindow;
        this.storage = storage;
    }

    @Override
    public JPanel onShow() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints con;

        JLabel title = new JLabel("Välj ett namn", JLabel.CENTER);
        title.setFont(new Font("SansSerif", Font.PLAIN, 14));
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 0;
        con.fill = GridBagConstraints.HORIZONTAL;
        panel.add(title, con);

        JLabel text1 = new JLabel("Andra spelare kommer se ditt namn om du väljer att spela online.");
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 1;
        con.fill = GridBagConstraints.HORIZONTAL;
        panel.add(text1, con);

        JLabel text2 = new JLabel("Du kan ändra ditt namn när du vill.");
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 2;
        con.fill = GridBagConstraints.HORIZONTAL;
        panel.add(text2, con);

        JTextField textField = new JTextField(16);
        String currentName = this.main.getName();
        if (currentName != null && !currentName.isEmpty()) {
            textField.setText(currentName);
        }
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 3;
        con.ipadx = 10;
        con.ipady = 10;
        con.insets = new Insets(10, 10, 10, 10);
        panel.add(textField, con);

        JButton button = new JButton("Klar");
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 4;
        panel.add(button, con);

        button.addActionListener(event -> {
            String name = textField.getText();
            if (name == null || name.isEmpty()) {
                JOptionPane.showMessageDialog(
                    null,
                    "Vänligen skriv ett namn i rutan.",
                    "Vänligen skriv ett namn",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Set the name
            main.setName(name);

            // Save the name
            try {
                storage.setName(name);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Kunde inte spara namnet.",
                    JOptionPane.ERROR_MESSAGE
                );
            }

            gameWindow.setScreen(new StartScreen(this.main));
        });

        return panel;
    }
}

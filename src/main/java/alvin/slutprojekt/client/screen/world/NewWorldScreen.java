package alvin.slutprojekt.client.screen.world;

import alvin.slutprojekt.client.ClientMain;
import alvin.slutprojekt.client.screen.StartScreen;
import alvin.slutprojekt.client.screen.SwingGameScreen;
import alvin.slutprojekt.world.Environment;
import alvin.slutprojekt.world.World;

import javax.swing.*;
import java.awt.*;

public class NewWorldScreen implements SwingGameScreen {
    private final ClientMain main;

    public NewWorldScreen(ClientMain main) {
        this.main = main;
    }

    @Override
    public JPanel onShow() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints con;

        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 0;
        con.gridwidth = 2;
        panel.add(new JLabel("Namnge världen:"), con);

        JTextField worldNameField = new JTextField(20);
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 1;
        con.gridwidth = 2;
        con.insets = new Insets(0, 0, 50, 0);
        panel.add(worldNameField, con);

        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 2;
        con.gridwidth = 2;
        panel.add(new JLabel("Seed:"), con);

        JTextField seedField = new JTextField("1234", 20);
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 3;
        con.gridwidth = 2;
        con.insets = new Insets(0, 0, 50, 0);
        panel.add(seedField, con);

        JButton cancelButton = new JButton("Avbryt");
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 4;
        panel.add(cancelButton, con);

        cancelButton.addActionListener(event -> {
            this.main.getGameWindow().setScreen(new StartScreen(this.main));
        });

        JButton createButton = new JButton("Skapa");
        con = new GridBagConstraints();
        con.gridx = 1;
        con.gridy = 4;
        panel.add(createButton, con);

        createButton.addActionListener(event -> {
            String worldName = worldNameField.getText().trim();

            if (worldName.isEmpty()) {
                JOptionPane.showMessageDialog(
                    null,
                    "Skriv in ett världnamn.",
                    "Ogiltigt världnamn",
                    JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            long seed;
            try {
                seed = Long.parseLong(seedField.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                    null,
                    "Seed måste vara ett tal.",
                    "Ogiltig seed",
                    JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            if (this.main.getStorage().worldExists(worldName)) {
                JOptionPane.showMessageDialog(
                    null,
                    "En annan värld har redan det namnet. Vänligen välj ett annat namn.",
                    "Världnamnet är upptaget",
                    JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            // Then start the game with that world.
            this.main.startGame(worldName, seed);
        });

        return panel;
    }
}

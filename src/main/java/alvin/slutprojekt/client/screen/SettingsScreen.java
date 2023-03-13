package alvin.slutprojekt.client.screen;

import alvin.slutprojekt.client.ClientMain;
import alvin.slutprojekt.client.GameWindow;
import alvin.slutprojekt.client.ClientStorage;

import javax.swing.*;
import java.awt.*;

/**
 * The settings screen where there are buttons to change settings.
 */
public class SettingsScreen implements SwingGameScreen {
    private final ClientMain main;
    private final GameScreen backScreen;

    public SettingsScreen(ClientMain main, GameScreen backScreen) {
        this.main = main;
        this.backScreen = backScreen;
    }

    @Override
    public JPanel onShow() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints con;

        JButton backButton = new JButton("Tillbaka");
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 0;
        con.insets = new Insets(0, 0, 50, 0);
        panel.add(backButton, con);

        backButton.addActionListener(event -> {
            this.main.getGameWindow().setScreen(this.backScreen);
        });

        JLabel currentName = new JLabel("Namn: " + this.main.getName());
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 1;
        panel.add(currentName, con);

        JButton changeNameButton = new JButton("Ändra namn");
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 2;
        con.insets = new Insets(0, 0, 50, 0);
        panel.add(changeNameButton, con);

        changeNameButton.addActionListener(event -> {
            GameWindow gameWindow = this.main.getGameWindow();
            ClientStorage storage = this.main.getStorage();
            gameWindow.setScreen(new SetNameScreen(this.main, gameWindow, storage));
        });

        return panel;
    }
}

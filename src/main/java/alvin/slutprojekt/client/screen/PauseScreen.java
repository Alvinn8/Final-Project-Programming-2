package alvin.slutprojekt.client.screen;

import alvin.slutprojekt.client.ClientMain;

import javax.swing.*;
import java.awt.*;

public class PauseScreen implements SwingGameScreen {
    private final ClientMain main;
    private final GameScreen backScreen;

    public PauseScreen(ClientMain main, GameScreen backScreen) {
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

        JButton leaveButton = new JButton("Till startskärmen");
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 1;
        panel.add(leaveButton, con);

        leaveButton.addActionListener(event -> {
            this.main.backToStartScreen();
        });

        return panel;
    }
}

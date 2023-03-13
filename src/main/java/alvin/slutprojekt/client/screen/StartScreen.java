package alvin.slutprojekt.client.screen;

import alvin.slutprojekt.client.ClientMain;
import alvin.slutprojekt.client.screen.playonline.PlayOnlineScreen;
import alvin.slutprojekt.client.screen.world.SelectWorldScreen;

import javax.swing.*;
import java.awt.*;

/**
 * The screen where you start the game. The main menu.
 */
public class StartScreen implements SwingGameScreen {
    private final ClientMain main;

    public StartScreen(ClientMain main) {
        this.main = main;
    }

    @Override
    public JPanel onShow() {
        JPanel panel = new JPanel();

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());;

        GridBagConstraints con;

        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 0;
        mainPanel.add(new JLabel("Slutprojekt - Alvin"), con);

        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 1;
        con.insets = new Insets(10, 50, 10, 50);
        JButton playButton = new JButton("Spela");
        mainPanel.add(playButton, con);

        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 2;
        con.insets = new Insets(10, 50, 10, 50);
        JButton playOnlineButton = new JButton("Spela online");
        mainPanel.add(playOnlineButton, con);

        playButton.addActionListener(evt -> play());
        playOnlineButton.addActionListener(evt -> playOnline());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));


        JButton instructionsButton = new JButton("Instruktioner");
        JButton settingsButton = new JButton("Inställningar");
        bottomPanel.add(instructionsButton);
        bottomPanel.add(Box.createHorizontalGlue());
        bottomPanel.add(settingsButton);

        instructionsButton.addActionListener(evt -> {
            this.main.getGameWindow().setScreen(new InstructionsScreen(this.main));
        });

        settingsButton.addActionListener(evt -> {
            this.main.getGameWindow().setScreen(new SettingsScreen(this.main, this));
        });

        panel.add(mainPanel);
        panel.add(bottomPanel);

        return panel;
    }

    private void play() {
        this.main.getGameWindow().setScreen(new SelectWorldScreen(this.main, this.main.getStorage()));
    }

    private void playOnline() {
        this.main.getGameWindow().setScreen(new PlayOnlineScreen(this.main));
    }
}

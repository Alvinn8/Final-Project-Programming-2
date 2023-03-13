package alvin.slutprojekt.client.screen.world;

import alvin.slutprojekt.client.ClientMain;
import alvin.slutprojekt.client.ClientStorage;
import alvin.slutprojekt.client.screen.StartScreen;
import alvin.slutprojekt.client.screen.SwingGameScreen;

import javax.swing.*;
import java.awt.*;

public class SelectWorldScreen implements SwingGameScreen {
    private final ClientMain main;
    private final ClientStorage clientStorage;

    public SelectWorldScreen(ClientMain main, ClientStorage clientStorage) {
        this.main = main;
        this.clientStorage = clientStorage;
    }

    @Override
    public JPanel onShow() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints con;

        JPanel worldsPanel = new JPanel();
        worldsPanel.setLayout(new BoxLayout(worldsPanel, BoxLayout.Y_AXIS));
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 0;
        con.weightx = 4;
        con.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(worldsPanel), con);

        for (String worldName : this.clientStorage.getWorldNames()) {
            worldsPanel.add(new WorldComponent(this.main, worldName));
        }

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridBagLayout());
        con = new GridBagConstraints();
        con.gridx = 1;
        con.gridy = 0;
        con.weightx = 1;
        con.fill = GridBagConstraints.BOTH;
        panel.add(buttonsPanel, con);

        JButton backButton = new JButton("Tillbaka");
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 0;
        con.insets = new Insets(50, 50, 50, 50);
        con.anchor = GridBagConstraints.NORTH;
        buttonsPanel.add(backButton, con);

        backButton.addActionListener(event -> {
            this.main.getGameWindow().setScreen(new StartScreen(this.main));
        });

        JButton newWorldButton = new JButton("Skapa ny värld");
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 1;
        con.insets = new Insets(50, 50, 50, 50);
        con.anchor = GridBagConstraints.SOUTH;
        buttonsPanel.add(newWorldButton, con);

        newWorldButton.addActionListener(event -> {
            this.main.getGameWindow().setScreen(new NewWorldScreen(this.main));
        });

        return panel;
    }
}

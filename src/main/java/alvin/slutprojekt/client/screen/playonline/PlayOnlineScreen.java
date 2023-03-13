package alvin.slutprojekt.client.screen.playonline;

import alvin.slutprojekt.client.ClientMain;
import alvin.slutprojekt.client.screen.StartScreen;
import alvin.slutprojekt.client.screen.SwingGameScreen;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * The screen where the user can choose what server to join when playing online.
 */
public class PlayOnlineScreen implements SwingGameScreen {
    private final ClientMain main;
    private JPanel serverListPanel;
    private JButton removeServerButton;
    private JTextField directServerAddress;

    public PlayOnlineScreen(ClientMain main) {
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
        con.fill = GridBagConstraints.HORIZONTAL;
        JLabel label = new JLabel("Spela online", JLabel.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(label, con);

        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 1;
        con.gridwidth = 2;
        con.fill = GridBagConstraints.HORIZONTAL;
        JButton backButton = new JButton("Tillbaka till startskärmen");
        panel.add(backButton, con);

        backButton.addActionListener(evt -> {
            this.main.getGameWindow().setScreen(new StartScreen(this.main));
        });

        JPanel serversPanel = new JPanel();
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 2;
        con.fill = GridBagConstraints.BOTH;
        con.weightx = 1;
        con.weighty = 1;
        panel.add(serversPanel, con);
        serversPanel.setLayout(new GridBagLayout());
        serversPanel.setBorder(new TitledBorder("Servrar"));

        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 0;
        con.gridwidth = 2;
        con.fill = GridBagConstraints.BOTH;
        con.weightx = 1;
        con.weighty = 1;
        serverListPanel = new JPanel();
        serverListPanel.setLayout(new GridBagLayout());
        serversPanel.add(new JScrollPane(serverListPanel), con);

        addServer("Demo Server", "server1.bkaw.ca:4137");
        addServer("localhost", "localhost:4137");

        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 1;
        con.anchor = GridBagConstraints.WEST;
        JButton addServerButton = new JButton("Lägg till server");
        addServerButton.setEnabled(false);
        serversPanel.add(addServerButton, con);

        con = new GridBagConstraints();
        con.gridx = 1;
        con.gridy = 1;
        con.anchor = GridBagConstraints.EAST;
        removeServerButton = new JButton("Ta bort server");
        removeServerButton.setEnabled(false);
        serversPanel.add(removeServerButton, con);

        JPanel directPanel = new JPanel();
        con = new GridBagConstraints();
        con.gridx = 1;
        con.gridy = 2;
        con.fill = GridBagConstraints.BOTH;
        con.weightx = 1;
        con.weighty = 1;
        panel.add(directPanel, con);
        directPanel.setBorder(new TitledBorder("Anslut direkt"));

        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 1;
        JLabel directTitle = new JLabel("Anslut direkt");
        directPanel.add(directTitle, con);

        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 1;
        directServerAddress = new JTextField(20);
        directPanel.add(directServerAddress, con);

        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 2;
        JButton directConnect = new JButton("Anslut");
        directPanel.add(directConnect, con);

        directConnect.addActionListener(evt -> {
            this.main.startOnlineGame(this.directServerAddress.getText());
        });

        return panel;
    }

    public void addServer(String serverName, String serverAddress) {
        GridBagConstraints con = new GridBagConstraints();
        con.gridx = 0;
        con.fill = GridBagConstraints.HORIZONTAL;
        con.ipadx = 20;
        con.ipady = 20;
        con.insets = new Insets(10, 10, 10, 10);
        serverListPanel.add(new ServerComponent(this.main, serverName, serverAddress), con);
    }
}

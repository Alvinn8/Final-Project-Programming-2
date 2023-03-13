package alvin.slutprojekt.client.screen.playonline;

import alvin.slutprojekt.client.ClientMain;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * A graphical component for a server in the play online menu.
 */
public class ServerComponent extends JComponent {
    private final ClientMain main;
    private final String serverAddress;

    public ServerComponent(ClientMain main, String serverName, String serverAddress) {
        this.main = main;
        this.serverAddress = serverAddress;

        this.setLayout(new GridBagLayout());
        this.setBorder(new EtchedBorder());

        GridBagConstraints con;

        JLabel nameLabel = new JLabel(serverName, JLabel.LEFT);
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 0;
        con.anchor = GridBagConstraints.WEST;
        this.add(nameLabel, con);

        JLabel addressLabel = new JLabel(serverAddress, JLabel.LEFT);
        addressLabel.setForeground(Color.DARK_GRAY);
        addressLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 1;
        con.anchor = GridBagConstraints.WEST;
        this.add(addressLabel, con);

        JButton button = new JButton("Anslut");
        con = new GridBagConstraints();
        con.gridx = 1;
        con.gridy = 0;
        con.gridheight = 2;
        con.anchor = GridBagConstraints.EAST;
        this.add(button, con);

        button.addActionListener(evt -> {
            this.main.startOnlineGame(this.serverAddress);
        });
    }
}

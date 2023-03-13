package alvin.slutprojekt.client.screen.world;

import alvin.slutprojekt.client.ClientMain;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

public class WorldComponent extends JPanel {
    public WorldComponent(ClientMain clientMain, String worldName) {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setBorder(new EtchedBorder());
        this.add(new JLabel(worldName));
        this.add(Box.createHorizontalGlue());
        JButton button = new JButton("Spela");
        this.add(button);

        button.addActionListener(event -> {
            clientMain.startGame(worldName);
        });
    }
}

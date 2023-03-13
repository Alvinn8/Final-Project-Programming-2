package alvin.slutprojekt.client.screen;

import alvin.slutprojekt.client.ClientMain;

import javax.swing.*;

public class InstructionsScreen implements SwingGameScreen{
    private final ClientMain main;

    public InstructionsScreen(ClientMain main) {
        this.main = main;
    }

    @Override
    public JPanel onShow() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel("Man rör sig med WASD eller piltangenterna. Använd musen för att markera tiles. Tilen"));
        panel.add(new JLabel("får inte vara för långt bort från dig men heller inte för nära."));
        panel.add(new JLabel(" "));
        panel.add(new JLabel("Vänsterklicka eller tryck ned `b` för att hugga tiles."));
        panel.add(new JLabel("Markera det föremål du vill placera på höger sida genom att trycka på det."));
        panel.add(new JLabel("Högerklicka sedan eller tryck ned `p` för att placera tiles."));
        panel.add(new JLabel(" "));
        panel.add(new JLabel("Escape för att pausa, då kan du också återgå till startmenyn."));

        JButton button = new JButton("Tillbaka");
        panel.add(button);

        button.addActionListener(evt -> {
            this.main.getGameWindow().setScreen(new StartScreen(this.main));
        });

        return panel;
    }
}

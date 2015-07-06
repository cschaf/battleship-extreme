package de.hsbremen.battleshipextreme.client;

import javax.swing.*;
import java.awt.*;

/**
 * Created on 11.06.2015.
 * Kontrollbar für den Spielfluss / Runde überspringen und Schiffe anzeigen
 */
public class GameControlBarPanel extends JPanel {
// ------------------------------ FIELDS ------------------------------

    private final JToggleButton buttonShowYourShips;
    private JButton buttonDone;

// --------------------------- CONSTRUCTORS ---------------------------

    public GameControlBarPanel() {
        setLayout(new FlowLayout());
        buttonDone = new JButton("Done");
        add(buttonDone);

        buttonShowYourShips = new JToggleButton("Show your ships");
        buttonShowYourShips.setSelected(true);
        buttonShowYourShips.setEnabled(false);
        add(buttonShowYourShips);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public JButton getButtonDone() {
        return buttonDone;
    }

    public JToggleButton getButtonShowYourShips() {
        return buttonShowYourShips;
    }
}

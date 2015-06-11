package de.hsbremen.battleshipextreme.client;

import javax.swing.*;
import java.awt.*;

/**
 * Created by cschaf on 11.06.2015.
 */
public class GameControlBarPanel extends JPanel {

    private final JToggleButton buttonShowYourShips;
    private JButton buttonDone;

    public GameControlBarPanel() {
        setLayout(new FlowLayout());
        buttonDone = new JButton("Done");
        add(buttonDone);

        buttonShowYourShips = new JToggleButton("Show your ships");
        buttonShowYourShips.setSelected(true);
        buttonShowYourShips.setEnabled(false);
        add(buttonShowYourShips);
    }

    public JToggleButton getButtonShowYourShips() {
        return buttonShowYourShips;
    }

    public JButton getButtonDone() {
        return buttonDone;
    }


}

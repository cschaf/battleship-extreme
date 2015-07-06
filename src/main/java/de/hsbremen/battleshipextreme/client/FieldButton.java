package de.hsbremen.battleshipextreme.client;

import javax.swing.*;
import java.awt.*;

/**
 * Klasse für ein Feld in einem Board
 */

public class FieldButton extends JButton {
// ------------------------------ FIELDS ------------------------------

    private int xPos;
    private int yPos;

// --------------------------- CONSTRUCTORS ---------------------------

    public FieldButton(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;

        // setBorderPainted(false);
        // setBorder(null);
        // button.setFocusable(false);
        // setMargin(new Insets(0, 0, 0, 0));
        // setContentAreaFilled(false);
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(40, 40);
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }
}

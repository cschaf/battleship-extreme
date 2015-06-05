package de.hsbremen.battleshipextreme.client;

import javax.swing.*;
import java.awt.*;

/**
 * Created by cschaf on 05.06.2015.
 */
public class PasswordInputPanel extends JPanel {
    private JPasswordField tbxPassword;
    private JLabel label;

    public PasswordInputPanel() {

        label = new JLabel("Enter password:");
        tbxPassword = new JPasswordField(10);

        setLayout(new FlowLayout());
        this.add(label);
        this.add(tbxPassword);
    }

    public JPasswordField getTbxPassword() {
        return tbxPassword;
    }

}

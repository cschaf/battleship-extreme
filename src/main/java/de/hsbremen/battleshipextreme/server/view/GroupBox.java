package de.hsbremen.battleshipextreme.server.view;

import javax.swing.*;

/**
 * Created on 14.05.2015.
 * Komponennte die einen Rahmen um ein Panel setzt mit einer Beschriftung
 */
public class GroupBox extends JPanel {
    public GroupBox(String title){
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.setTitle(title);
    }
    public void setTitle(String title){
        this.setBorder(BorderFactory.createTitledBorder(title));
    }
}

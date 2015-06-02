package de.hsbremen.battleshipextreme.client;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

/**
 * Created by cschaf on 01.06.2015.
 */
public class ServerConnectionPanel extends JPanel {
    private ServerConnectionBarPanel pnlServerConnectionBar;
    private ServerGameBrowserPanel pnlServerGameBrowser;

    public ServerConnectionPanel() {
        setLayout(new MigLayout());
        _initComponents();
        _addComponents();
    }

    private void _initComponents() {
        pnlServerConnectionBar = new ServerConnectionBarPanel();
        pnlServerGameBrowser = new ServerGameBrowserPanel();

    }

    private void _addComponents() {
        // row 1
        add(pnlServerConnectionBar, "spanx 3, growx, pushx, wrap");
        add(pnlServerGameBrowser, "spanx 3, growx, growy, pushx, pushy, wrap");
    }

    public ServerConnectionBarPanel getPnlServerConnectionBar() {
        return pnlServerConnectionBar;
    }

    public ServerGameBrowserPanel getPnlServerGameBrowser() {
        return pnlServerGameBrowser;
    }
}

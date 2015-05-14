package de.hsbremen.battleshipextreme.server.view;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Created by cschaf on 14.05.2015.
 */
public class ServerControl extends JPanel {
    private JLabel lblPort;
    private JTextField tbxPort;
    private JLabel lblIp;
    private JTextField tbxIp;
    private JButton btnStart;
    private JButton btnStop;

    public ServerControl(){
        _initComponents();
        this.add(lblIp, "split 2");
        this.add(tbxIp, "pushx, growx");
        this.add(lblPort, "split 2");
        this.add(tbxPort);
        this.add(btnStart);
        this.add(btnStop);
    }

    private void _initComponents(){
        setLayout(new MigLayout());
        lblIp = new JLabel("IP:");
        tbxIp = new JTextField("127.0.0.1");
        lblPort = new JLabel("Port:");
        tbxPort = new JTextField("1337");
        tbxPort.setPreferredSize(new Dimension(50, 22));
                btnStart = new JButton("Start");
        btnStop = new JButton("Stop");
    }
}

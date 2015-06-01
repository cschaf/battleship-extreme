package de.hsbremen.battleshipextreme.client;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Created by cschaf on 01.06.2015.
 */
public class ServerConnectionBarPanel extends JPanel{
    private JLabel lblPort;
    private JTextField tbxPort;
    private JLabel lblIp;
    private JTextField tbxIp;
    private JLabel lbUsername;
    private JTextField tbxUsername;
    private JButton btnConnect;
    private JButton btnDisconnect;

    public ServerConnectionBarPanel(){
        setLayout(new MigLayout());
        _initComponents();
        _addComponents();
    }

    private void _initComponents(){
        lblIp = new JLabel("IP:");
        tbxIp = new JTextField("127.0.0.1");
        tbxIp.setPreferredSize(new Dimension(100, 21));
        lblPort = new JLabel("Port:");
        tbxPort = new JTextField("1337");
        tbxPort.setPreferredSize(new Dimension(50, 21));
        lbUsername = new JLabel("Username:");
        tbxUsername = new JTextField("Player");
        btnConnect = new JButton("Connect");
        btnDisconnect = new JButton("Disconnect");
        btnDisconnect.setEnabled(false);
    }

    private void _addComponents(){
        this.add(lblIp, "split 2");
        this.add(tbxIp);
        this.add(lblPort, "split 2");
        this.add(tbxPort);
        this.add(lbUsername, "split 2");
        this.add(tbxUsername, "pushx, growx");
        this.add(btnConnect, "sg btn");
        this.add(btnDisconnect, "sg btn");
    }



    public JTextField getTbxPort() {
        return tbxPort;
    }

    public JTextField getTbxIp() {
        return tbxIp;
    }

    public JTextField getTbxUsername() {
        return tbxUsername;
    }

    public JButton getBtnDisconnect() {
        return btnDisconnect;
    }

    public JButton getBtnConnect() {
        return btnConnect;
    }
}

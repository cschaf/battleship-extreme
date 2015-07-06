package de.hsbremen.battleshipextreme.server.view;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Created on 14.05.2015.
 * beinhaltet alle Komponennten zum Verdinden zum Server
 */
public class ServerControlBar extends JPanel {
    private JLabel lblPort;
    private JTextField tbxPort;
    private JLabel lblIp;
    private JTextField tbxIp;
    private JButton btnStart;
    private JButton btnStop;

    public ServerControlBar(){
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
        tbxIp.setEditable(false);
        tbxIp.setBackground(Color.white);
        lblPort = new JLabel("Port:");
        tbxPort = new JTextField("1337");
        tbxPort.setPreferredSize(new Dimension(50, 22));
        btnStart = new JButton("Start");
        btnStop = new JButton("Stop");
        btnStop.setEnabled(false);
    }

    public JTextField getTbxPort(){
        return tbxPort;
    }

    public JTextField getTbxIp(){
        return tbxIp;
    }

    public JButton getBtnStart(){
        return btnStart;
    }

    public JButton getBtnStop(){
        return btnStop;
    }

    public void setEnabledAfterStartStop(boolean state){
        tbxIp.setEnabled(state);
        tbxPort.setEnabled(state);
        btnStart.setEnabled(state);
        btnStop.setEnabled(!state);
    }

}

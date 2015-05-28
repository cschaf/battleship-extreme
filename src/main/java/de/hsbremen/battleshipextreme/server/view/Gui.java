package de.hsbremen.battleshipextreme.server.view;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Created by cschaf on 14.05.2015.
 */
public class Gui extends JFrame {
    private JPanel pnlMain;
    private ServerControlBar pnlServerControlBarPanel;
    private JTextField tbxMessage;
    private JButton btnSend;
    private JScrollPane scrollPanelUsers;
    private JList listUsers;
    private JScrollPane scrollPanelGames;
    private JList listGames;
    private JTextArea traMessages;
    private JScrollPane scrollPanelMessages;
    private GroupBox boxUsers;
    private GroupBox boxGames;
    private GroupBox boxMessages;

    public Gui() {
        super("Server Gui");
        this._initComponents();
        this._addComponents();
        setContentPane(pnlMain);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension d = new Dimension(637, 500);
        setSize(d);
        setPreferredSize(d);
        pack();
        setVisible(true);
    }

    private void _addComponents() {
        // row 1
        pnlMain.add(pnlServerControlBarPanel, "spanx 3, growx, pushx, wrap");

// row 2
        pnlMain.add(boxMessages, "push, grow, spanx 2, spany 2");
        pnlMain.add(boxUsers, "wrap, pushy, growy");
        pnlMain.add(boxGames, "wrap");

// row 3
        pnlMain.add(tbxMessage, "spanx 3, pushx, growx, split");
        pnlMain.add(btnSend);
    }

    private void _initComponents() {
        pnlMain = new JPanel(new MigLayout());

        pnlServerControlBarPanel = new ServerControlBar();

        traMessages = new JTextArea(10, 10);
        scrollPanelMessages = new JScrollPane(traMessages);

        boxMessages = new GroupBox("Messages");
        boxMessages.add(scrollPanelMessages);

        listUsers = new JList();
        scrollPanelUsers = new JScrollPane(listUsers);

        boxUsers = new GroupBox("Connected Users");
        boxUsers.add(scrollPanelUsers);

        listGames = new JList();
        scrollPanelGames = new JScrollPane(listGames);

        boxGames = new GroupBox("Created Games");
        boxGames.add(scrollPanelGames);

        tbxMessage = new JTextField("");
        btnSend = new JButton("Send");
    }

    public JPanel getPnlMain(){
        return pnlMain;
    }

    public ServerControlBar getPnlServerControlBarPanel(){
        return pnlServerControlBarPanel;
    }

    public JTextField getTbxMessage(){
        return tbxMessage;
    }

    public JButton getBtnSend(){
        return btnSend;
    }

    public JList getListUsers(){
        return listUsers;
    }

    public JList getListGames(){
        return listGames;
    }

    public JTextArea getTraMessages(){
        return traMessages;
    }
}


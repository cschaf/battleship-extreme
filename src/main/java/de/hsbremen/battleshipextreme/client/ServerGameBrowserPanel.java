package de.hsbremen.battleshipextreme.client;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Vector;

/**
 * Created by cschaf on 01.06.2015.
 */
public class ServerGameBrowserPanel extends JPanel {
    private JButton btnRefresh;
    private JButton btnBack;
    private JButton btnJoin;
    private JButton btnCreate;
    private JTable tblGames;
    private DefaultTableModel tblModel;
    private JScrollPane spnlGames;

    public ServerGameBrowserPanel() {
        setLayout(new MigLayout());
        _initComponents();
        _addComponents();
    }

    private void _initComponents() {
        btnJoin = new JButton("Join");
        btnCreate = new JButton("Create");
        btnRefresh = new JButton("Refresh List");
        btnBack = new JButton("Back");

        tblModel = _setupGameTableModel();
        tblGames = new JTable(tblModel);
        spnlGames = new JScrollPane(tblGames);
        tblGames.setShowGrid(true);
    }

    private DefaultTableModel _setupGameTableModel() {
        DefaultTableModel tblModel = new DefaultTableModel();
        tblModel.addColumn("Name");
        tblModel.addColumn("Player");
        tblModel.addColumn("Password");
        tblModel.addColumn("ID");
        return tblModel;
    }

    public JButton getBtnCreate() {
        return btnCreate;
    }

    private void _addComponents() {
        this.add(spnlGames, "spanx 4, pushx, pushy, growx, growy,  wrap");
        this.add(btnBack, "sg btn, growx, pushx");
        this.add(btnCreate, "sg btn, growx, pushx");
        this.add(btnJoin, "sg btn, growx, pushx");
        this.add(btnRefresh, "sg btn, growx, pushx");
    }


    public void addGameToTable(String name, String player, boolean password){
        Vector row = new Vector();
        row.add(name);
        row.add(player);
        String isPassword = "no";
        if (password){
            isPassword = "yes";
        }
        row.add(isPassword);
        tblModel.addRow(row);
    }

    public DefaultTableModel getTblModel() {
        return tblModel;
    }

    public JTable getTblGames() {
        return tblGames;
    }

    public JButton getBtnJoin() {
        return btnJoin;
    }

    public JButton getBtnBack() {
        return btnBack;
    }

    public JButton getBtnRefresh() {
        return btnRefresh;
    }

}

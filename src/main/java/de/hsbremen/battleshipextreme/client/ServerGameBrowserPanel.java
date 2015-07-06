package de.hsbremen.battleshipextreme.client;

import de.hsbremen.battleshipextreme.network.transfarableObject.NetGame;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

/**
 * Created on 01.06.2015.
 */
public class ServerGameBrowserPanel extends JPanel {
    private JButton btnRefresh;
    private JButton btnBack;
    private JButton btnJoin;
    private JButton btnCreate;
    private JTable tblGames;
    private GameListModel tblModel;
    private JScrollPane spnlGames;

    public ServerGameBrowserPanel() {
        setLayout(new MigLayout());
        _initComponents();
        _addComponents();
    }

    private void _initComponents() {
        btnJoin = new JButton("Join");
        btnCreate = new JButton("Create");
        btnCreate.setEnabled(false);
        btnRefresh = new JButton("Refresh List");
        btnRefresh.setEnabled(false);
        btnBack = new JButton("Back");

        tblModel = new GameListModel();
        tblGames = new JTable(tblModel);
        tblGames.getColumn("PW").setCellRenderer(new IconTableCellRenderer());
        spnlGames = new JScrollPane(tblGames);
        tblGames.setShowGrid(true);
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

    public void addGameToTable(NetGame netGame) {
        tblModel.addGame(netGame);
    }

    public GameListModel getTblModel() {
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

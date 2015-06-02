package de.hsbremen.battleshipextreme.client;

import de.hsbremen.battleshipextreme.network.transfarableObject.NetGame;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

/**
 * Created by cschaf on 02.06.2015.
 */
public class GameListModel extends AbstractTableModel {
    private Vector<NetGame> netGames;
    private String[] columns;

    public GameListModel() {
        this.netGames = new Vector<NetGame>();
        this.columns = new String[]{"Name", "Player", "ID", "Password"};
    }

    public int getRowCount() {
        return netGames.size();
    }

    public int getColumnCount() {
        return columns.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return netGames.get(rowIndex).getName();
            case 1:
                return netGames.get(rowIndex).getJoinedPlayers().size() + " / " + netGames.get(rowIndex).getMaxPlayers();

            case 2:
                return netGames.get(rowIndex).getId();

            case 3:
                return netGames.get(rowIndex).isPrivate();
            default:
                return netGames.get(rowIndex);
        }
    }

    public void addGame(NetGame netGame) {
        this.netGames.add(netGame);
        fireTableDataChanged();
    }

    public void removeGame(NetGame netGame) {
        this.netGames.remove(netGame);
        fireTableDataChanged();
    }

    public void getGame(int rowIndex) {
        this.netGames.get(rowIndex);
    }

    public String getColumnName(int columnIndex) {
        return columns[columnIndex];
    }

    public void removeAllGames() {
        for (int i=0; i< netGames.size(); i++){
            netGames.remove(i);
        }
    }
}

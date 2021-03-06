package de.hsbremen.battleshipextreme.client.listener;

import de.hsbremen.battleshipextreme.client.GameListModel;
import de.hsbremen.battleshipextreme.client.MultiPlayerClientController;
import de.hsbremen.battleshipextreme.model.network.NetworkClient;
import de.hsbremen.battleshipextreme.network.transfarableObject.NetGame;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created on 03.06.2015.
 * Listener, der für die Events der Controls des GameBrowsers verwendet wird
 */
public class ServerGameBrowserListener implements TableColumnModelListener, MouseListener {
// ------------------------------ FIELDS ------------------------------

    private NetworkClient network;
    private MultiPlayerClientController ctrl;

// --------------------------- CONSTRUCTORS ---------------------------

    public ServerGameBrowserListener(NetworkClient network, MultiPlayerClientController ctrl) {
        this.network = network;
        this.ctrl = ctrl;
    }


// --------------------- Interface MouseListener ---------------------


    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            JTable target = (JTable) e.getSource();
            int rowIndex = target.getSelectedRow();
            GameListModel model = (GameListModel) target.getModel();
            NetGame game = model.getGame(rowIndex);
            if (game.isPrivate()) {
                ctrl.createPasswordPrompt(game);
            } else {
                network.join(game.getId());
            }
        }
    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

// --------------------- Interface TableColumnModelListener ---------------------

    public void columnAdded(TableColumnModelEvent e) {
    }

    public void columnRemoved(TableColumnModelEvent e) {
    }

    public void columnMoved(TableColumnModelEvent e) {

    }

    public void columnMarginChanged(ChangeEvent e) {
        ctrl.resizeServerGameListColumns();
    }

    public void columnSelectionChanged(ListSelectionEvent e) {

    }
}

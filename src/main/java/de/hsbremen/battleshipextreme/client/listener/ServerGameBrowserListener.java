package de.hsbremen.battleshipextreme.client.listener;

import de.hsbremen.battleshipextreme.client.Controller;
import de.hsbremen.battleshipextreme.client.GUI;
import de.hsbremen.battleshipextreme.model.network.NetworkClient;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

/**
 * Created by cschaf on 03.06.2015.
 */
public class ServerGameBrowserListener implements TableColumnModelListener {
    private Controller ctrl;

    public ServerGameBrowserListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

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

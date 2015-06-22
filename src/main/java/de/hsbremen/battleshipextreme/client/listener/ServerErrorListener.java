package de.hsbremen.battleshipextreme.client.listener;

import de.hsbremen.battleshipextreme.client.GUI;
import de.hsbremen.battleshipextreme.model.network.NetworkClient;
import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.eventhandling.listener.IErrorListener;

import javax.swing.*;

/**
 * Created by cschaf on 03.06.2015.
 */
public class ServerErrorListener implements IErrorListener {
    private GUI gui;
    private NetworkClient network;

    public ServerErrorListener(GUI gui, NetworkClient network) {

        this.gui = gui;
        this.network = network;
    }

    public void onError(EventArgs<ITransferable> eventArgs) {
        JOptionPane.showMessageDialog(gui.getFrame(), eventArgs.getItem(), "Error", JOptionPane.ERROR_MESSAGE);
        if (!network.isConnected()) {
            gui.getPanelServerConnection().getPnlServerConnectionBar().setEnabledAfterStartStop(true);
            gui.getPanelServerConnection().getPnlServerGameBrowser().getTblModel().removeAllGames();
        }
    }
}

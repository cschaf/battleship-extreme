package de.hsbremen.battleshipextreme.server.listener;

import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;

/**
 * Created on 26.04.2015.
 * Listener für das Connecten und Disconnected von Clients
 */
public class ClientConnectionConsoleListener implements IClientConnectionListener {

// --------------------- Interface IClientConnectionListener ---------------------

    public void onClientHasConnected(EventArgs<ITransferable> eventArgs) {
        System.out.println(eventArgs.getItem());
    }

    public void onClientHasDisconnected(EventArgs<ITransferable> eventArgs) {
        System.out.println(eventArgs.getItem());
    }
}

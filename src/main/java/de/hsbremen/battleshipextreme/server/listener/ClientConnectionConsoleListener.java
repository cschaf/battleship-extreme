package de.hsbremen.battleshipextreme.server.listener;

import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.transfarableObject.ClientInfo;

/**
 * Created by cschaf on 26.04.2015.
 */
public class ClientConnectionConsoleListener implements IClientConnectionListener {

    public void onClientHasConnected(EventArgs<ITransferable> eventArgs) {
        System.out.println(eventArgs.getItem());
    }

    public void onClientHasDisconnected(EventArgs<ITransferable> eventArgs) {
        System.out.println(eventArgs.getItem());
    }
}

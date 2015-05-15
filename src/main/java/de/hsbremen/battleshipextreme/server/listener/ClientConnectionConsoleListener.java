package de.hsbremen.battleshipextreme.server.listener;

import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.transfarableObject.ClientInfo;

/**
 * Created by cschaf on 26.04.2015.
 */
public class ClientConnectionConsoleListener implements IClientConnectionListener {

    public void onClientHasConnected(EventArgs<ITransferable> eventArgs) {
        ClientInfo info = (ClientInfo) eventArgs.getItem();
        System.out.println("--- New user for user list ---");
        System.out.println(info.getUsername() + " (" + info.getIp() + ":" + info.getPort() + ")");
        System.out.println("--- end ---");
    }

    public void onClientHasDisconnected(EventArgs<ITransferable> eventArgs) {
        System.out.println(eventArgs.getItem());
    }
}

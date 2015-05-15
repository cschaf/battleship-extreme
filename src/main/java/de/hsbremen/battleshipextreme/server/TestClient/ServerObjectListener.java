package de.hsbremen.battleshipextreme.server.TestClient;

import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.transfarableObject.ClientInfo;
import de.hsbremen.battleshipextreme.network.transfarableObject.Message;

import javax.swing.*;

/**
 * Created by cschaf on 17.04.2015.
 */
public class ServerObjectListener implements IServerObjectReceivedListener {

    @Override
    public void onObjectReceived(EventArgs<ITransferable> eventArgs) {

    }

    @Override
    public void onMessageObjectReceived(EventArgs<Message> eventArgs) {
        System.out.println(eventArgs.getItem());
    }

    @Override
    public void onClientInfoObjectReceived(EventArgs<ClientInfo> eventArgs) {
        switch (eventArgs.getItem().getReason()) {
            case Connect:
                System.out.println("--- New User for user list");
                System.out.println(eventArgs.getItem().getUsername() + "(" + eventArgs.getItem().getPort() + ")");
                System.out.println("--- end ---");
                break;
            case Disconnect:
                System.out.println("--- Remove User from user list");
                System.out.println(eventArgs.getItem().getUsername() + "(" + eventArgs.getItem().getPort() + ")");
                System.out.println("--- end ---");
                break;
        }
    }
}

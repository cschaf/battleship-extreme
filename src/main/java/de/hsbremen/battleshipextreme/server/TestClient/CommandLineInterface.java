package de.hsbremen.battleshipextreme.server.TestClient;

import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.eventhandling.listener.IErrorListener;
import de.hsbremen.battleshipextreme.network.transfarableObject.ClientInfo;
import de.hsbremen.battleshipextreme.network.transfarableObject.Message;

/**
 * Created by cschaf on 15.05.2015.
 */

public class CommandLineInterface {
    public static void main(String[] args) throws Exception {
        ClientNetworker client = new ClientNetworker("localhost", 1337, "Shuffle");

        ErrorListener errorListener = new ErrorListener();
        client.addErrorListener(errorListener);

        ServerObjectListener serverObjectListener = new ServerObjectListener();
        client.addServerObjectReceivedListener(serverObjectListener);

        client.connect();
    }
}
package de.hsbremen.battleshipextreme.server.TestClient;

/**
 * Created on 15.05.2015.
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
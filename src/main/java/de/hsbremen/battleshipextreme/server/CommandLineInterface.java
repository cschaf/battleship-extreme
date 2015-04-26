package de.hsbremen.battleshipextreme.server;

import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.eventhandling.listener.IErrorListener;
import de.hsbremen.battleshipextreme.server.listener.ClientConnectionConsoleListener;
import de.hsbremen.battleshipextreme.server.listener.IClientObjectReceivedListener;
import de.hsbremen.battleshipextreme.server.listener.IServerListener;

/**
 * Created by cschaf on 26.04.2015.
 */
public class CommandLineInterface {
    public static void main(String[] args) {
        Server server = new Server(1337);
        server.addErrorListener(new IErrorListener() {
            public void onError(EventArgs<ITransferable> eventArgs) {
                System.out.println(eventArgs.getItem());
            }
        });
        server.addServerListener(new IServerListener() {
            public void onInfo(EventArgs<ITransferable> eventArgs) {
                System.out.println(eventArgs.getItem());
            }
        });

        server.addClientObjectReceivedListener(new IClientObjectReceivedListener() {
            public void onObjectReceived(EventArgs<ITransferable> eventArgs) {
                System.out.println(eventArgs.getItem());
            }
        });
        ClientConnectionConsoleListener consoleListener = new ClientConnectionConsoleListener();
        server.addClientConnectionListener(consoleListener);

        server.start();
    }
}

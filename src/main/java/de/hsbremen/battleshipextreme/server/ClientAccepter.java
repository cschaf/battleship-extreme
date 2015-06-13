package de.hsbremen.battleshipextreme.server;

import de.hsbremen.battleshipextreme.network.IDisposable;
import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.TransferableObjectFactory;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by cschaf on 25.04.2015.
 */
public class ClientAccepter extends Thread implements IDisposable {
    private ServerSocket serverSocket;
    private ServerDispatcher serverDispatcher;
    private boolean disposed;

    public ClientAccepter(ServerSocket serverSocket, ServerDispatcher serverDispatcher) {
        this.disposed = false;
        this.serverSocket = serverSocket;
        this.serverDispatcher = serverDispatcher;
    }

    /**
     * Until interrupted, accept clients
     */
    public void run() {
        while (!this.disposed) {
            try {

                Socket socket = serverSocket.accept();
                if (serverDispatcher.getClients().size() >= serverDispatcher.getMaxPlayers()) {
                    socket.close();
                }
                ClientHandler clientHandler = new ClientHandler(socket);
                ClientSender clientSender = new ClientSender(clientHandler, serverDispatcher);
                ClientListener clientListener = new ClientListener(clientHandler, serverDispatcher);

                clientHandler.setClientListener(clientListener);
                clientHandler.setClientSender(clientSender);
                clientListener.start();
                clientSender.start();

                serverDispatcher.addClient(clientHandler);

                boolean isBanned = serverDispatcher.isBanned(socket.getInetAddress().getHostAddress());
                if (isBanned) {
                    serverDispatcher.unicast(TransferableObjectFactory.CreateError("You are banned from this server!"), clientHandler);
                    serverDispatcher.removeClient(clientHandler);
                }
            } catch (IOException e) {
                this.serverDispatcher.getErrorHandler().errorHasOccurred(new EventArgs<ITransferable>(this, TransferableObjectFactory.CreateMessage("Stopped listening for clients")));
            }
        }
    }

    public void dispose() {
        this.disposed = true;
    }
}

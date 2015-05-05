package de.hsbremen.battleshipextreme.server;

import de.hsbremen.battleshipextreme.network.IDisposable;
import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.TransferableObjectFactory;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.transfarableObject.ClientInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Created by cschaf on 25.04.2015.
 */
public class ClientListener extends Thread implements IDisposable {
    private ServerDispatcher serverDispatcher;
    private ClientHandler clientHandler;
    private ObjectInputStream in;
    private boolean disposed;

    public ClientListener(ClientHandler clientHandler, ServerDispatcher serverDispatcher) throws IOException {
        this.disposed = false;
        this.clientHandler = clientHandler;
        this.serverDispatcher = serverDispatcher;
        Socket socket = clientHandler.getSocket();
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public void run() {
        try {
            while (!isInterrupted() && !this.disposed) {
                ITransferable receivedObject = null;
                try {
                    receivedObject = (ITransferable) this.in.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (receivedObject == null) {
                    break;
                }
                switch (receivedObject.getType()) {
                    case ClientInfo:
                        ClientInfo info = (ClientInfo) receivedObject;
                        switch (info.getReason()) {
                            case Connect:
                                break;
                            case Disconnect:
                                break;
                        }
                        break;
                    case Game:
                        this.serverDispatcher.addGame(receivedObject);
                        break;
                    case Turn:
                        // Turn get Game
                        // add Turn to game
                        break;
                    default:
                        this.serverDispatcher.dispatchObject(receivedObject);
                }
            }
        } catch (IOException e) {
            this.serverDispatcher.getErrorHandler().errorHasOccurred(new EventArgs<ITransferable>(this, TransferableObjectFactory.CreateMessage("Problem reading from socket(" + clientHandler.getSocket().getInetAddress().getHostAddress() + ":" + clientHandler.getSocket().getPort() + ") -> communication is broken")));
            this.dispose();
        }

        // Communication is broken. Interrupt both listener and sender threads
        this.clientHandler.getClientSender().interrupt();
        this.serverDispatcher.deleteClient(this.clientHandler);
    }

    public void dispose() {
        try {
            this.disposed = true;
            this.in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

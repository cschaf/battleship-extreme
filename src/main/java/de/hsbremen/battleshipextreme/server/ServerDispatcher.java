package de.hsbremen.battleshipextreme.server;

import de.hsbremen.battleshipextreme.network.*;
import de.hsbremen.battleshipextreme.network.eventhandling.ErrorHandler;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.transfarableObject.Game;
import de.hsbremen.battleshipextreme.server.listener.IClientConnectionListener;
import de.hsbremen.battleshipextreme.server.listener.IClientObjectReceivedListener;

import javax.swing.event.EventListenerList;
import java.util.List;
import java.util.Vector;

/**
 * Created by cschaf on 25.04.2015.
 */
public class ServerDispatcher extends Thread implements IDisposable {
    protected EventListenerList listeners;
    private Vector<ClientHandler> clients;
    private Vector<ITransferable> objectQueue;
    private Vector<Game> games;
    private boolean disposed;
    private ErrorHandler errorHandler;

    public ServerDispatcher(ErrorHandler errorHandler) {
        this.listeners = new EventListenerList();
        this.errorHandler = errorHandler;
        this.disposed = false;
        this.clients = new Vector<ClientHandler>();
        this.objectQueue = new Vector<ITransferable>();
        this.games = new Vector<Game>();
    }

    public Vector<ClientHandler> getClients() {
        return this.clients;
    }

    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    public synchronized void addClient(ClientHandler clientHandler) {
        this.clients.add(clientHandler);
        ITransferable serverMessage = TransferableObjectFactory.CreateMessage(clientHandler.getSocket().getInetAddress().getHostAddress() + ":" + clientHandler.getSocket().getPort() + " has connected");
        notifyClientConnectionListeners(new EventArgs<ITransferable>(this, serverMessage));
    }

    public synchronized void deleteClient(ClientHandler clientHandler) {
        int clientIndex = this.clients.indexOf(clientHandler);
        if (clientIndex != -1) {
            this.clients.removeElementAt(clientIndex);
            ITransferable serverMessage = TransferableObjectFactory.CreateMessage(clientHandler.getSocket().getInetAddress().getHostAddress() + ":" + clientHandler.getSocket().getPort() + " (" + clientHandler.getUsername() + ")" + " has disconnected");
            notifyClientConnectionListeners(new EventArgs<ITransferable>(this, serverMessage));
            // addObjectToQueue to client the disconnected user info
            this.broadcast(TransferableObjectFactory.CreateClientInfo(clientHandler.getUsername(), clientHandler.getSocket().getInetAddress().getHostAddress(), clientHandler.getSocket().getPort(), InfoSendingReason.Disconnect), clientHandler);
        }
    }

    public synchronized void dispatchObject(ITransferable transferableObject) {
        this.objectQueue.add(transferableObject);
        notifyClientObjectReceivedListener(new EventArgs<ITransferable>(this, transferableObject));
        notify();
    }

    private synchronized ITransferable getNextObjectFromQueue() throws InterruptedException {
        while (this.objectQueue.size() == 0) {
            wait();
        }
        ITransferable object = this.objectQueue.get(0);
        this.objectQueue.removeElementAt(0);
        return object;
    }

    public synchronized void broadcast(ITransferable transferableObject, ClientHandler excludedClient) {
        for (int i = 0; i < this.clients.size(); i++) {
            ClientHandler clientHandler = this.clients.get(i);
            if (clientHandler != excludedClient) {
                clientHandler.getClientSender().addObjectToQueue(transferableObject);
            }
        }
    }

    public synchronized void multicast(ITransferable transferableObject, List<ClientHandler> excludedClients) {
        for (int i = 0; i < excludedClients.size(); i++) {
            ClientHandler clientHandler = excludedClients.get(i);
            clientHandler.getClientSender().addObjectToQueue(transferableObject);
        }
    }

    public synchronized void unicast(ITransferable transferableObject, ClientHandler client) {
        for (int i = 0; i < this.clients.size(); i++) {
            ClientHandler clientHandler = this.clients.get(i);
            if (clientHandler == client) {
                clientHandler.getClientSender().addObjectToQueue(transferableObject);
                break;
            }
        }
    }

    public void run() {
        try {
            while (!this.disposed) {
                ITransferable object = this.getNextObjectFromQueue();
                this.broadcast(object, null);
            }
        } catch (InterruptedException ie) {
            this.errorHandler.errorHasOccurred(new EventArgs<ITransferable>(this, TransferableObjectFactory.CreateMessage("ServerDispatcher thread interrupted, stopped its execution")));
            this.dispose();
        }
    }

    public void addClientObjectReceivedListener(IClientObjectReceivedListener listener) {
        this.listeners.add(IClientObjectReceivedListener.class, listener);
    }

    public void removeClientObjectReceivedListener(IClientObjectReceivedListener listener) {
        this.listeners.remove(IClientObjectReceivedListener.class, listener);
    }

    public void addClientConnectionListener(IClientConnectionListener listener) {
        this.listeners.add(IClientConnectionListener.class, listener);
    }

    public void removeClientConnectionListener(IClientConnectionListener listener) {
        this.listeners.remove(IClientConnectionListener.class, listener);
    }

    private void notifyClientConnectionListeners(EventArgs<ITransferable> eventArgs) {
        Object[] listeners = this.listeners.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == IClientConnectionListener.class) {
                ((IClientConnectionListener) listeners[i + 1]).onClientHasConnected(eventArgs);
            }
        }
    }


    private void notifyClientObjectReceivedListener(EventArgs<ITransferable> eventArgs) {
        Object[] listeners = this.listeners.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == IClientObjectReceivedListener.class) {
                ((IClientObjectReceivedListener) listeners[i + 1]).onObjectReceived(eventArgs);
            }
        }
    }

    public void dispose() {
        this.disposed = true;
        for (ClientHandler clients : this.clients) {
            clients.dispose();
        }
    }

    public synchronized void addGame(ITransferable receivedObject) {
        if (receivedObject.getType() != TransferableType.Game){
            this.errorHandler.errorHasOccurred(new EventArgs<ITransferable>(this, TransferableObjectFactory.CreateMessage("Couldn't create new game!")));
            return;
        }
        Game game = (Game) receivedObject;
        this.games.add(game);
        // TODO: hier noch alle clients benachichigen


    }

    public synchronized void deleteGame(ITransferable receivedObject) {
        if (receivedObject.getType() != TransferableType.Game){
            this.errorHandler.errorHasOccurred(new EventArgs<ITransferable>(this, TransferableObjectFactory.CreateMessage("Couldn't delete game!")));
            return;
        }

        int gameIndex = this.games.indexOf(receivedObject);
        if (gameIndex != -1) {
            this.games.removeElementAt(gameIndex);
            // TODO: hier noch alle clients benachichigen
        }
    }
}

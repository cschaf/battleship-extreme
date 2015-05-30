package de.hsbremen.battleshipextreme.server;

import de.hsbremen.battleshipextreme.network.*;
import de.hsbremen.battleshipextreme.network.eventhandling.ErrorHandler;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.transfarableObject.Game;
import de.hsbremen.battleshipextreme.network.transfarableObject.Join;
import de.hsbremen.battleshipextreme.network.transfarableObject.Turn;
import de.hsbremen.battleshipextreme.server.listener.IClientConnectionListener;
import de.hsbremen.battleshipextreme.server.listener.IClientObjectReceivedListener;
import de.hsbremen.battleshipextreme.server.listener.IServerListener;

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
        clientHasConnected(new EventArgs<ITransferable>(this, serverMessage));
    }

    public synchronized void deleteClient(ClientHandler clientHandler) {
        int clientIndex = this.clients.indexOf(clientHandler);
        if (clientIndex != -1) {
            this.clients.removeElementAt(clientIndex);
            ITransferable user = TransferableObjectFactory.CreateClientInfo(clientHandler.getUsername(), clientHandler.getSocket().getInetAddress().getHostAddress(), clientHandler.getSocket().getPort());
            clientHasDisconnected(new EventArgs<ITransferable>(this, user));

        }
    }

    public synchronized void dispatchObject(ITransferable transferableObject) {
        this.objectQueue.add(transferableObject);
        objectReceived(new EventArgs<ITransferable>(this, transferableObject));
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

    private void clientHasDisconnected(EventArgs<ITransferable> eventArgs) {
        Object[] listeners = this.listeners.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == IClientConnectionListener.class) {
                ((IClientConnectionListener) listeners[i + 1]).onClientHasDisconnected(eventArgs);
            }
        }
    }

    private void clientHasConnected(EventArgs<ITransferable> eventArgs) {
        Object[] listeners = this.listeners.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == IClientConnectionListener.class) {
                ((IClientConnectionListener) listeners[i + 1]).onClientHasConnected(eventArgs);
            }
        }
    }

    public void objectReceived(EventArgs<ITransferable> eventArgs) {
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
        if (receivedObject.getType() != TransferableType.Game) {
            this.errorHandler.errorHasOccurred(new EventArgs<ITransferable>(this, TransferableObjectFactory.CreateMessage("Couldn't create new game!")));
            return;
        }
        Game game = (Game) receivedObject;
        this.games.add(game);
        objectReceived(new EventArgs<ITransferable>(this, game));
        this.broadcast(game, null);
    }

    public synchronized void deleteGame(ITransferable receivedObject) {
        if (receivedObject.getType() != TransferableType.Game) {
            this.errorHandler.errorHasOccurred(new EventArgs<ITransferable>(this, TransferableObjectFactory.CreateMessage("Couldn't delete game!")));
            return;
        }

        int gameIndex = this.games.indexOf(receivedObject);
        if (gameIndex != -1) {
            this.games.removeElementAt(gameIndex);
            objectReceived(new EventArgs<ITransferable>(this, receivedObject));
        }
    }

    public synchronized void addTurn(ClientHandler handler, ITransferable receivedObject) {
        Turn turn = (Turn) receivedObject;
        boolean gameFound = false;
        for (Game game : this.games) {
            for(ClientHandler client : game.getJoinedPlayers()){
                if (client == handler) {
                    game.addTurn(turn);
                    turn.setGameId(game.getId());
                    gameFound = true;
                    break;
                }
            }
            if(gameFound){
                this.multicast(turn, game.getJoinedPlayers());
                break;
            }
        }

        objectReceived(new EventArgs<ITransferable>(this, turn));
    }

    public void assignClientToGame(ClientHandler clientHandler, ITransferable receivedObject) {
        Join join = (Join) receivedObject;
        for (Game game : this.games) {
            if (join.getGameId().equals(game.getId())) {
                game.addPlayer(clientHandler);
                this.multicast(TransferableObjectFactory.CreateClientInfo(clientHandler.getUsername(), clientHandler.getSocket().getInetAddress().getHostAddress(), clientHandler.getSocket().getPort(), InfoSendingReason.Connect), game.getJoinedPlayers());
                break;
            }
        }
    }

    public void addServerListener(IServerListener listener) {
        this.listeners.add(IServerListener.class, listener);
    }


    public void removeServerListener(IServerListener listener) {
        this.listeners.remove(IServerListener.class, listener);
    }
    public void printInfo(EventArgs<ITransferable> eventArgs) {
        Object[] listeners = this.listeners.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == IServerListener.class) {
                ((IServerListener) listeners[i + 1]).onInfo(eventArgs);
            }
        }
    }
}

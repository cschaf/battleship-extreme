package de.hsbremen.battleshipextreme.server;

import de.hsbremen.battleshipextreme.network.*;
import de.hsbremen.battleshipextreme.network.eventhandling.ErrorHandler;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.transfarableObject.NetGame;
import de.hsbremen.battleshipextreme.network.transfarableObject.Join;
import de.hsbremen.battleshipextreme.network.transfarableObject.Turn;
import de.hsbremen.battleshipextreme.server.listener.IClientConnectionListener;
import de.hsbremen.battleshipextreme.server.listener.IClientObjectReceivedListener;
import de.hsbremen.battleshipextreme.server.listener.IServerListener;

import javax.swing.event.EventListenerList;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

/**
 * Created by cschaf on 25.04.2015.
 */
public class ServerDispatcher extends Thread implements IDisposable, Serializable {
    protected EventListenerList listeners;
    private Vector<ClientHandler> clients;
    private Vector<ITransferable> objectQueue;
    private Vector<NetGame> netGames;
    private Vector<String> banList;
    private boolean disposed;
    private ErrorHandler errorHandler;
    private int maxPlayers;
    private int maxGames;

    public ServerDispatcher(ErrorHandler errorHandler) {
        this.maxGames = 2;
        this.maxPlayers = 12;
        this.listeners = new EventListenerList();
        this.errorHandler = errorHandler;
        this.disposed = false;
        this.clients = new Vector<ClientHandler>();
        this.objectQueue = new Vector<ITransferable>();
        this.netGames = new Vector<NetGame>();
        this.banList = new Vector<String>();
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
        boolean found = false;
        NetGame foundGame = null;
        for (NetGame netGame : this.netGames) {
            for (int i = 0; i < netGame.getJoinedPlayers().size(); i++) {
                if (netGame.getJoinedPlayers().get(i) == clientHandler) {
                    netGame.getJoinedPlayers().remove(i);
                    found = true;
                    foundGame = netGame;
                    break;
                }
            }
            if (found) {
                ITransferable disconnect = TransferableObjectFactory.CreateClientInfo(clientHandler.getUsername(), clientHandler.getSocket().getInetAddress().getHostAddress(),clientHandler.getSocket().getPort(),InfoSendingReason.Disconnect);
                clientHandler.dispose();
                multicast(disconnect, foundGame.getJoinedPlayers());
                break;
            }
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

    public synchronized void multicast(ITransferable transferableObject, List<ClientHandler> clients) {
        for (int i = 0; i < clients.size(); i++) {
            ClientHandler clientHandler = clients.get(i);
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
        NetGame netGame = (NetGame) receivedObject;
        this.netGames.add(netGame);
        objectReceived(new EventArgs<ITransferable>(this, netGame));
        this.broadcast(netGame, null);
    }

    public synchronized void deleteGame(ITransferable receivedObject) {
        if (receivedObject.getType() != TransferableType.Game) {
            this.errorHandler.errorHasOccurred(new EventArgs<ITransferable>(this, TransferableObjectFactory.CreateMessage("Couldn't delete game!")));
            return;
        }

        int gameIndex = this.netGames.indexOf(receivedObject);
        if (gameIndex != -1) {
            this.netGames.removeElementAt(gameIndex);
            objectReceived(new EventArgs<ITransferable>(this, receivedObject));
        }
    }

    public synchronized void addTurn(ClientHandler handler, ITransferable receivedObject) {
        Turn turn = (Turn) receivedObject;
        boolean gameFound = false;
        for (NetGame netGame : this.netGames) {
            for (ClientHandler client : netGame.getJoinedPlayers()) {
                if (client == handler) {
                    netGame.addTurn(turn);
                    turn.setGameId(netGame.getId());
                    gameFound = true;
                    break;
                }
            }
            if (gameFound) {
                this.multicast(turn, netGame.getJoinedPlayers());
                break;
            }
        }

        objectReceived(new EventArgs<ITransferable>(this, turn));
    }

    public void assignClientToGame(ClientHandler clientHandler, ITransferable receivedObject) {
        Join join = (Join) receivedObject;
        NetGame jGame = null;
        for (NetGame netGame : this.netGames) {
            if (join.getGameId().equals(netGame.getId())) {
                if (netGame.getJoinedPlayers().size() < netGame.getMaxPlayers()) {
                    netGame.addPlayer(clientHandler);
                    jGame = netGame;
                    ITransferable info = TransferableObjectFactory.CreateClientInfo(clientHandler.getUsername(), clientHandler.getSocket().getInetAddress().getHostAddress(), clientHandler.getSocket().getPort(), InfoSendingReason.Connect);
                    this.multicast(info, netGame.getJoinedPlayers());
                } else {
                    ITransferable msg = TransferableObjectFactory.CreateMessage("Game has no free slot available!");
                    this.unicast(msg, clientHandler);
                }
                break;
            }
        }
        join.setClient(clientHandler.getUsername());
        objectReceived(new EventArgs<ITransferable>(this, join));
        unicast(TransferableObjectFactory.CreateGame(jGame.getName(), jGame.getSettings()), clientHandler);
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

    public void banClient(ClientHandler client) {
        String ip = client.getSocket().getInetAddress().getHostAddress();
        if(!ip.equals("127.0.0.1") || !ip.equals("localhost")){
            this.banList.add(ip);
        }
        this.deleteClient(client);
    }

    public void removeClientFromBanList(String ip) {
        for (int i= 0; i<banList.size(); i++){
            if (ip.equals(banList.get(i))){
                banList.remove(i);
                break;
            }
        }
    }

    public int getMaxGames() {
        return maxGames;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public Vector<NetGame> getNetGames() {
        return netGames;
    }

    public Vector<String> getBanList() {
        return banList;
    }

    public boolean isBanned(String hostAddress) {
        for (int i = 0; i < banList.size(); i++) {
            if (hostAddress.equals(banList.get(i))) {
                return true;
            }
        }
        return false;
    }

    public ClientHandler getClient(String ip, int port) {
        for (ClientHandler client : getClients()) {
            if (client.getSocket().getInetAddress().getHostAddress().equals(ip) && client.getSocket().getPort() == port) {
                return client;
            }
        }
        return null;
    }

    public NetGame getGameById(String id){
        for (int i= 0; i < getNetGames().size(); i++){
            if (getNetGames().get(i).getId().equals(id)){
                return getNetGames().get(i);
            }
        }
        return null;
    }

    public void sendGameList(ClientHandler clientHandler) {
        ITransferable games = TransferableObjectFactory.CreateGameList(getNetGames());
        this.unicast(games, clientHandler);
    }
}

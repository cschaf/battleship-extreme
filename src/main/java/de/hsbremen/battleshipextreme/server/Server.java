package de.hsbremen.battleshipextreme.server;

import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.network.IDisposable;
import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.InfoSendingReason;
import de.hsbremen.battleshipextreme.network.TransferableObjectFactory;
import de.hsbremen.battleshipextreme.network.eventhandling.ErrorHandler;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.eventhandling.listener.IErrorListener;
import de.hsbremen.battleshipextreme.network.transfarableObject.NetGame;
import de.hsbremen.battleshipextreme.server.listener.IClientConnectionListener;
import de.hsbremen.battleshipextreme.server.listener.IClientObjectReceivedListener;
import de.hsbremen.battleshipextreme.server.listener.IServerListener;

import javax.swing.event.EventListenerList;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by cschaf on 26.04.2015.
 */
public class Server implements IDisposable {

    public int port;
    protected EventListenerList listeners;
    private ServerSocket serverSocket;
    private ServerDispatcher serverDispatcher;
    private ClientAccepter clientAccepter;
    private ArrayList<IClientConnectionListener> tempClientConnectionListeners;
    private ArrayList<IClientObjectReceivedListener> tempClientObjectReceivedListeners;
    private ArrayList<IServerListener> tempServerListeners;
    private ErrorHandler errorHandler;
    private boolean isRunning;

    public Server(int port) {
        this.listeners = new EventListenerList();
        this.errorHandler = new ErrorHandler();
        this.tempClientObjectReceivedListeners = new ArrayList<IClientObjectReceivedListener>();
        this.tempClientConnectionListeners = new ArrayList<IClientConnectionListener>();
        this.tempServerListeners = new ArrayList<IServerListener>();
        this.port = port;
        this.isRunning = false;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            this.isRunning = true;
        } catch (IOException e) {
            errorHandler.errorHasOccurred(new EventArgs<ITransferable>(this, TransferableObjectFactory.CreateMessage("Can not start listening on port " + port)));
            this.dispose();
            this.isRunning = false;
            System.exit(-1);
        }
        // Start ServerDispatcher thread
        this.serverDispatcher = new ServerDispatcher(this.errorHandler);
        for (IClientConnectionListener listener : this.tempClientConnectionListeners) {
            this.serverDispatcher.addClientConnectionListener(listener);
        }
        for (IClientObjectReceivedListener listener : this.tempClientObjectReceivedListeners) {
            this.serverDispatcher.addClientObjectReceivedListener(listener);
        }
        for (IServerListener listener : this.tempServerListeners) {
            this.serverDispatcher.addServerListener(listener);
        }

        this.tempClientConnectionListeners.clear();
        this.serverDispatcher.start();
        // Accept and handle client connections
        this.clientAccepter = new ClientAccepter(serverSocket, serverDispatcher);
        this.clientAccepter.start();

        this.serverDispatcher.printInfo(new EventArgs<ITransferable>(this, TransferableObjectFactory.CreateMessage("Server started on port " + port)));
    }

    public void stop() {
        this.serverDispatcher.getNetGames().removeAllElements();
        this.dispose();
        this.isRunning = false;
    }

    public Vector<NetGame> getGames() {
        return this.serverDispatcher.getNetGames();
    }

    public Vector<ClientHandler> getClients() {
        return this.serverDispatcher.getClients();
    }

    public void broadcast(ITransferable object) {
        this.serverDispatcher.broadcast(object, null);
    }

    public void multicast(ITransferable transferableObject, List<ClientHandler> clients) {

    }

    public void addClientObjectReceivedListener(IClientObjectReceivedListener listener) {
        if (this.serverDispatcher == null) {
            tempClientObjectReceivedListeners.add(listener);
        } else {
            this.serverDispatcher.addClientObjectReceivedListener(listener);
        }
    }

    public void removeClientObjectReceivedListener(IClientObjectReceivedListener listener) {
        this.listeners.remove(IClientObjectReceivedListener.class, listener);
    }

    public void addClientConnectionListener(IClientConnectionListener listener) {
        if (this.serverDispatcher == null) {
            tempClientConnectionListeners.add(listener);
        } else {
            this.serverDispatcher.addClientConnectionListener(listener);
        }
    }

    public void removeClientConnectionListener(IClientConnectionListener listener) {
        this.serverDispatcher.removeClientConnectionListener(listener);
    }

    public void addServerListener(IServerListener listener) {
        if (this.serverDispatcher == null) {
            tempServerListeners.add(listener);
        } else {
            this.serverDispatcher.addServerListener(listener);
        }
    }

    public void removeServerListener(IServerListener listener) {
        this.removeServerListener(listener);
    }

    public void addErrorListener(IErrorListener listener) {
        this.errorHandler.addErrorListener(listener);
    }

    public void removeErrorListener(IErrorListener listener) {
        this.errorHandler.removeErrorListener(listener);
    }

    public void dispose() {
        try {
            if (this.clientAccepter != null) {
                this.clientAccepter.dispose();
            }
            if (this.serverDispatcher != null) {
                this.serverDispatcher.dispose();
            }
            if (this.serverSocket != null) {
                this.serverSocket.close();
            }
        } catch (IOException e) {
            errorHandler.errorHasOccurred(new EventArgs<ITransferable>(this, TransferableObjectFactory.CreateMessage("Can not dispose Server")));
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void kickClient(String ip, int port) {
        ClientHandler client = serverDispatcher.getClient(ip, port);
        this.serverDispatcher.deleteClient(client);
    }

    public void banClient(String ip, int port) {
        ClientHandler client = serverDispatcher.getClient(ip, port);
        this.serverDispatcher.banClient(client);
    }

    public void removeGame(NetGame netGame) {
        for (int i = 0; i < serverDispatcher.getNetGames().size(); i++) {
            if (this.serverDispatcher.getNetGames().get(i).getId().equals(netGame.getId())) {
                this.serverDispatcher.getNetGames().remove(i);
                break;
            }
        }
    }

    public void removeClientsFromGame(String gameId) {
        NetGame netGame = this.serverDispatcher.getGameById(gameId);
        if (netGame != null) {
            ITransferable serverInfo = TransferableObjectFactory.CreateServerInfo(InfoSendingReason.GameClosed);
            serverDispatcher.multicast(serverInfo, netGame.getJoinedPlayers());
        }
    }

    public void createStandardGame() {
        if (isRunning()) {
            Settings settings = new Settings(2, 0, 0, 15, 1, 0, 0, 1);
            NetGame game = new NetGame("Server Game 2er", settings);
            //game.setPassword("123456");
            serverDispatcher.addGame(game);

            settings = new Settings(3, 0, 0, 12, 1, 1, 1, 2);
            game = new NetGame("Server Game 3er", settings);
            //game.setPassword("123456");
            serverDispatcher.addGame(game);
        }
    }
}

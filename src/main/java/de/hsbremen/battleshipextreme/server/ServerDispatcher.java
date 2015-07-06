package de.hsbremen.battleshipextreme.server;

import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.exception.ShipAlreadyPlacedException;
import de.hsbremen.battleshipextreme.model.exception.ShipOutOfBoardException;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.network.*;
import de.hsbremen.battleshipextreme.network.eventhandling.ErrorHandler;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.transfarableObject.Join;
import de.hsbremen.battleshipextreme.network.transfarableObject.NetGame;
import de.hsbremen.battleshipextreme.network.transfarableObject.ShipPlacedInformation;
import de.hsbremen.battleshipextreme.network.transfarableObject.Turn;
import de.hsbremen.battleshipextreme.server.listener.IClientConnectionListener;
import de.hsbremen.battleshipextreme.server.listener.IClientObjectReceivedListener;
import de.hsbremen.battleshipextreme.server.listener.IServerListener;

import javax.swing.event.EventListenerList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created on 25.04.2015.
 * Der Serverdispatcher verwaltet alle Clients, Spiele und Spielzüge.
 * Außerdem ist er zuständig, Nachrichten an andere Clients weiterzuleiten (uni-, multi- und broadcast)
 */
public class ServerDispatcher extends Thread implements IDisposable, Serializable {
// ------------------------------ FIELDS ------------------------------

    protected EventListenerList listeners;
    private Vector<ClientHandler> clients;
    private Vector<ITransferable> objectQueue;
    private Vector<NetGame> netGames;
    private Vector<String> banList;
    private boolean disposed;
    private ErrorHandler errorHandler;
    private int maxPlayers;
    private int maxGames;

// --------------------------- CONSTRUCTORS ---------------------------

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

// --------------------- GETTER / SETTER METHODS ---------------------

    public Vector<String> getBanList() {
        return banList;
    }

    public synchronized Vector<ClientHandler> getClients() {
        return this.clients;
    }

    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    public int getMaxGames() {
        return maxGames;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public synchronized Vector<NetGame> getNetGames() {
        return netGames;
    }


// --------------------- Interface IDisposable ---------------------

    /**
     * Beendet den Thread und alle Client-Threads
     */
    public void dispose() {
        this.disposed = true;
        for (ClientHandler clients : this.clients) {
            clients.dispose();
        }
    }

// --------------------- Interface Runnable ---------------------

    /**
     * Hole dir das nächste Objekt von der Warteschlange und versende es an alle Clients
     */
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

// -------------------------- OTHER METHODS --------------------------

    /**
     * Füge einen neuen Client hinzu
     */
    public synchronized void addClient(ClientHandler clientHandler) {
        this.clients.add(clientHandler);
        ITransferable serverMessage = TransferableObjectFactory.CreateMessage(clientHandler.getSocket().getInetAddress().getHostAddress() + ":" + clientHandler.getSocket().getPort() + " has connected");
        clientHasConnected(new EventArgs<ITransferable>(this, serverMessage));
    }

    private void clientHasConnected(EventArgs<ITransferable> eventArgs) {
        Object[] listeners = this.listeners.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == IClientConnectionListener.class) {
                ((IClientConnectionListener) listeners[i + 1]).onClientHasConnected(eventArgs);
            }
        }
    }

    public void addClientConnectionListener(IClientConnectionListener listener) {
        this.listeners.add(IClientConnectionListener.class, listener);
    }

    public void addClientObjectReceivedListener(IClientObjectReceivedListener listener) {
        this.listeners.add(IClientObjectReceivedListener.class, listener);
    }

    /**
     * Füge ein neues Game hinzu
     * @param receivedObject
     */
    public synchronized void addGame(ITransferable receivedObject) {
        if (receivedObject.getType() != TransferableType.Game) {
            return;
        }
        NetGame netGame = (NetGame) receivedObject;
        this.netGames.add(netGame);
        objectReceived(new EventArgs<ITransferable>(this, netGame));
        ITransferable gameList = TransferableObjectFactory.CreateGameList(this.netGames);
        broadcast(gameList, null);
    }

    /**
     * Sende ein Objekt vom Typ ITransferable an alle verdundenen Clients
     * @param transferableObject
     * @param excludedClient
     */
    public synchronized void broadcast(ITransferable transferableObject, ClientHandler excludedClient) {
        for (int i = 0; i < this.clients.size(); i++) {
            ClientHandler clientHandler = this.clients.get(i);
            if (clientHandler != excludedClient) {
                clientHandler.getClientSender().addObjectToQueue(transferableObject);
            }
        }
    }

    public void addServerListener(IServerListener listener) {
        this.listeners.add(IServerListener.class, listener);
    }

    /**
     * Verarbeite Client Informationen über ein neu gesetztes Schiff auf sein Board
     */
    public void addShipPlacedInformationToGame(ClientHandler clientHandler, ITransferable receivedObject) {
        ShipPlacedInformation info = (ShipPlacedInformation) receivedObject;
        NetGame game = getGameByClient(clientHandler);
        if (game != null) {
            Player player = game.getPlayerByName(clientHandler.getUsername());
            player.setCurrentShipByType(info.getShipType());
            if (!game.getReady()) {
                boolean allShipsSet = game.haveAllPlayersSetTheirShips();
                if (!allShipsSet) {
                    if (!player.hasPlacedAllShips()) {
                        try {
                            player.placeShip(info.getX(), info.getY(), info.getOrientation());
                        } catch (ShipAlreadyPlacedException e) {
                            e.printStackTrace();
                        } catch (FieldOutOfBoardException e) {
                            e.printStackTrace();
                        } catch (ShipOutOfBoardException e) {
                            e.printStackTrace();
                        }
                    }
                    allShipsSet = game.haveAllPlayersSetTheirShips();
                    // send to all player all Boards
                    if (allShipsSet) {
                        sendGameReady(game);
                        initializeNextTurn(game);
                    } else {
                        if (player.hasPlacedAllShips()) {
                            // send place your ships to next player
                            initializeNextShipPlacement(game);
                        }
                    }
                }
            }
        }
    }

    /**
     * Sende alles Client in dem Spiel ein GameIsReady danit dieses beginnen kann
     * @param game
     */
    private void sendGameReady(NetGame game) {
        ITransferable rdy = TransferableObjectFactory.CreateServerInfo(InfoSendingReason.GameReady);
        multicast(rdy, game.getJoinedPlayers());

        // sends all Player names to the clients for combobox
        game.setGameToReady();
    }

    /**
     * Füge einen neunen Spielzug dem passenden Game und der globalen Spielzugliste hinzu.
     * Anschließend wird der nächste Client benachrichtig, damit er seinen Zug machen kann
     */
    public synchronized void addTurn(ClientHandler handler, ITransferable receivedObject) {
        Turn turn = (Turn) receivedObject;
        NetGame netGame = getGameByClient(handler);

        if (netGame != null) {
            netGame.addTurn(turn);
            turn.setGameId(netGame.getId());
            ITransferable clientTurn = handleTurn(netGame, turn);
            if (clientTurn != null) {
                this.multicast(clientTurn, netGame.getJoinedPlayers());
                if (!netGame.isGameover()) {
                    initializeNextTurn(netGame);
                }
            }
        }

        objectReceived(new EventArgs<ITransferable>(this, turn));
    }

    /**
     * Liefert das Spiel anhand eines Clients
     * @param client
     * @return
     */
    public NetGame getGameByClient(ClientHandler client) {
        for (NetGame game : netGames) {
            for (ClientHandler clientHandler : game.getJoinedPlayers()) {
                if (clientHandler.equals(client)) {
                    return game;
                }
            }
        }
        return null;
    }

    /**
     * Verarbeitet einen Spielzug des Clients
     */
    private ITransferable handleTurn(NetGame netGame, ITransferable t) {
        Turn turn = (Turn) t;
        Orientation orientation = turn.isHorizontal() ? Orientation.HORIZONTAL : Orientation.VERTICAL;
        ITransferable clientTurn = null;
        try {
            if (turn.isReloading()) {
                clientTurn = TransferableObjectFactory.CreateClientTurn(null, true, turn.getAttackingPlayerName(), null);
            } else {
                netGame.getCurrentPlayer().setCurrentShipByType(turn.getShipType());
                netGame.makeTurn(netGame.getPlayerByName(turn.getAttackedPlayerName()), turn.getFieldX(), turn.getFieldY(), orientation);
                if (!netGame.isGameover()) {
                    if (netGame.isReady()) {
                        //  make client turn for other clients
                        clientTurn = TransferableObjectFactory.CreateClientTurn(netGame.getMarkedFieldOfLastTurn(), false, turn.getAttackingPlayerName(), turn.getAttackedPlayerName());
                    }
                } else {
                    // game is over one player has won the game
                    clientTurn = TransferableObjectFactory.CreateClientTurn(netGame.getMarkedFieldOfLastTurn(), false, turn.getAttackingPlayerName(), turn.getAttackedPlayerName(), netGame.getWinner().getName());
                    netGames.remove(netGame);
                }
            }
            if (!netGame.isGameover()) {
                netGame.nextPlayer();
            }
        } catch (FieldOutOfBoardException e) {
            e.printStackTrace();
        }
        return clientTurn;
    }

    /**
     * Stößt den nächsten Spielzug an
     * @param game
     */
    public void initializeNextTurn(NetGame game) {
        // get next client id for ship placement
        int nextPlayer = game.getClientTurnOrder().next();
        ClientHandler client = game.getPlayersMap().get(nextPlayer);

        // Allow client to make his turn
        this.sendMakeTurn(client);

        // add client id again to the queue for next round
        game.getClientTurnOrder().add(nextPlayer);
    }

    /**
     * Sendet ein Objekt, das den Client bemächtig seinen Spielzug zu machen
     */
    private void sendMakeTurn(ClientHandler client) {
        ITransferable info = TransferableObjectFactory.CreateServerInfo(InfoSendingReason.MakeTurn);
        unicast(info, client);
    }

    /**
     * Weisst ein Client einem Spiel zu
     */
    public synchronized void assignClientToGame(ClientHandler clientHandler, ITransferable receivedObject) {
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
                    ITransferable msg = TransferableObjectFactory.CreateError("Game has no free slot available!");
                    this.unicast(msg, clientHandler);
                    return;
                }
                break;
            }
        }
        join.setClient(clientHandler.getUsername());
        objectReceived(new EventArgs<ITransferable>(this, join));
        unicast(TransferableObjectFactory.CreateGame(jGame.getName(), "", jGame.getSettings()), clientHandler);

        if (jGame.getJoinedPlayers().size() == jGame.getMaxPlayers()) {
            jGame.updatePlayerNames();
            this.sendReadyForPlacement(jGame);
            this.initializeNextShipPlacement(jGame);
        }
    }

    /**
     * Initialisiert das Schiffesetzten des nächst möglichgen Spielers in dem Spiel
     * @param game
     */
    private void initializeNextShipPlacement(NetGame game) {
        // get next client id for ship placement
        int nextPlayer = game.getClientTurnOrder().next();
        ClientHandler client = game.getPlayersMap().get(nextPlayer);

        // Allow client to olace his ships
        this.sendPlaceYourShips(client);

        // add client id again to the queue for next round
        game.getClientTurnOrder().add(nextPlayer);
    }

    /**
     * Sendet die bemächtigung zum Setzten seiner Schiffe
     * @param client
     */
    private void sendPlaceYourShips(ClientHandler client) {
        ITransferable info = TransferableObjectFactory.CreateServerInfo(InfoSendingReason.PlaceYourShips);
        this.unicast(info, client);
    }

    /**
     * Sendet alles Client in einem Spiel das es losgehen kann mit Schiffe platzieren, da alle benötigten
     * Spieler im Spiel sind
     * @param game
     */
    private void sendReadyForPlacement(NetGame game) {
        ITransferable games = TransferableObjectFactory.CreateServerInfo(InfoSendingReason.ReadyForPlacement);
        multicast(games, game.getJoinedPlayers());
        sendNameList(game);
    }

    /**
     * Sendet allen Clients in einem Spiel die Namen alles Spieler im Spiel zu
     * @param game
     */
    public void sendNameList(NetGame game) {
        ArrayList<String> names = new ArrayList<String>();
        for (ClientHandler handler : game.getJoinedPlayers()) {
            names.add(handler.getUsername());
        }
        ITransferable object = TransferableObjectFactory.CreatePlayerNames(names);
        this.multicast(object, game.getJoinedPlayers());
    }

    /**
     * Sendet eine private Nachricht zu einen einzelnen Client
     */
    public synchronized void unicast(ITransferable transferableObject, ClientHandler client) {
        for (int i = 0; i < this.clients.size(); i++) {
            ClientHandler clientHandler = this.clients.get(i);
            if (clientHandler == client) {
                clientHandler.getClientSender().addObjectToQueue(transferableObject);
                break;
            }
        }
    }

    /**
     * Bannt einen Client vom Server
     */
    public void banClient(ClientHandler client) {
        String ip = client.getSocket().getInetAddress().getHostAddress();
        if (!ip.equals("127.0.0.1") || !ip.equals("localhost")) {
            this.banList.add(ip);
        }
        this.removeClient(client);
    }

    /**
     * Entfernt einen Client vom Server und benachrichtigt alle beteiligen Clients
     * @param clientHandler
     */
    public synchronized void removeClient(ClientHandler clientHandler) {
        int clientIndex = this.clients.indexOf(clientHandler);
        if (clientIndex != -1) {
            this.clients.removeElementAt(clientIndex);
            ITransferable user = TransferableObjectFactory.CreateClientInfo(clientHandler.getUsername(), clientHandler.getSocket().getInetAddress().getHostAddress(), clientHandler.getSocket().getPort());
            clientHasDisconnected(new EventArgs<ITransferable>(this, user));
            boolean found = false;
            NetGame foundGame = null;
            for (NetGame netGame : this.netGames) {
                for (int i = 0; i < netGame.getJoinedPlayers().size(); i++) {
                    if (netGame.getJoinedPlayers().get(i) == clientHandler) {
                        netGame.removePlayer(clientHandler);
                        found = true;
                        foundGame = netGame;
                        break;
                    }
                }
                if (found) {
                    ITransferable disconnect = TransferableObjectFactory.CreateClientInfo(clientHandler.getUsername(), clientHandler.getSocket().getInetAddress().getHostAddress(), clientHandler.getSocket().getPort(), InfoSendingReason.Disconnect);
                    multicast(disconnect, foundGame.getJoinedPlayers());
                    break;
                }
            }
            clientHandler.dispose();
        }
    }

    private void clientHasDisconnected(EventArgs<ITransferable> eventArgs) {
        Object[] listeners = this.listeners.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == IClientConnectionListener.class) {
                ((IClientConnectionListener) listeners[i + 1]).onClientHasDisconnected(eventArgs);
            }
        }
    }

    /**
     * Sendet ein Objekt nur an eine bestimmte Anzahl von Clients
     */
    public synchronized void multicast(ITransferable transferableObject, List<ClientHandler> clients) {
        for (int i = 0; i < clients.size(); i++) {
            ClientHandler clientHandler = clients.get(i);
            clientHandler.getClientSender().addObjectToQueue(transferableObject);
        }
    }

    /**
     * Entfernt ein Spiel vom Server
     */
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

    /**
     * Fügt ein Objekt der Warteschlange hinzu
     */
    public synchronized void dispatchObject(ITransferable transferableObject) {
        this.objectQueue.add(transferableObject);
        objectReceived(new EventArgs<ITransferable>(this, transferableObject));
        notify();
    }

    public void objectReceived(EventArgs<ITransferable> eventArgs) {
        Object[] listeners = this.listeners.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == IClientObjectReceivedListener.class) {
                ((IClientObjectReceivedListener) listeners[i + 1]).onObjectReceived(eventArgs);
            }
        }
    }

    /**
     * Gibt einen Client anhand seiner IP und Port zurück
     * @param ip
     * @param port
     * @return
     */
    public synchronized ClientHandler getClient(String ip, int port) {
        for (ClientHandler client : getClients()) {
            if (client.getSocket().getInetAddress().getHostAddress().equals(ip) && client.getSocket().getPort() == port) {
                return client;
            }
        }
        return null;
    }

    /**
     * Gibt ein Spiel anhand seiner ID zurück
     * @param id
     * @return
     */
    public synchronized NetGame getGameById(String id) {
        for (int i = 0; i < getNetGames().size(); i++) {
            if (getNetGames().get(i).getId().equals(id)) {
                return getNetGames().get(i);
            }
        }
        return null;
    }

    /**
     * Gibt das nächst mögliche Objekt von der Warteschlange zurück
     * @return
     * @throws InterruptedException
     */
    private synchronized ITransferable getNextObjectFromQueue() throws InterruptedException {
        while (this.objectQueue.size() == 0) {
            wait();
        }
        ITransferable object = this.objectQueue.get(0);
        this.objectQueue.removeElementAt(0);
        return object;
    }

    public boolean isBanned(String hostAddress) {
        for (int i = 0; i < banList.size(); i++) {
            if (hostAddress.equals(banList.get(i))) {
                return true;
            }
        }
        return false;
    }

    public boolean isClientNameAvailable(String clientName) {
        for (ClientHandler client : clients) {
            if (client.hasUsername()) {
                if (client.getUsername().equals(clientName)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void printInfo(EventArgs<ITransferable> eventArgs) {
        Object[] listeners = this.listeners.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == IServerListener.class) {
                ((IServerListener) listeners[i + 1]).onInfo(eventArgs);
            }
        }
    }

    public void removeClientConnectionListener(IClientConnectionListener listener) {
        this.listeners.remove(IClientConnectionListener.class, listener);
    }

    public void removeClientFromBanList(String ip) {
        for (int i = 0; i < banList.size(); i++) {
            if (ip.equals(banList.get(i))) {
                banList.remove(i);
                break;
            }
        }
    }

    public void removeClientObjectReceivedListener(IClientObjectReceivedListener listener) {
        this.listeners.remove(IClientObjectReceivedListener.class, listener);
    }

    public void removeServerListener(IServerListener listener) {
        this.listeners.remove(IServerListener.class, listener);
    }

    /**
     * Sendet einem Client die Liste aller eröffneten Spiele
     */
    public synchronized void sendGameList(ClientHandler clientHandler) {
        ITransferable games = TransferableObjectFactory.CreateGameList(this.netGames);
        this.unicast(games, clientHandler);
    }

    /**
     * Sendet einem Client eine Liste aller Namen in seinem aktuellen Spiel
     */
    public void sendNameList(ClientHandler client) {
        NetGame game = getGameByClient(client);
        ArrayList<String> names = new ArrayList<String>();
        for (ClientHandler handler : game.getJoinedPlayers()) {
            names.add(handler.getUsername());
        }
        ITransferable object = TransferableObjectFactory.CreatePlayerNames(names);
        this.unicast(object, client);
    }
}

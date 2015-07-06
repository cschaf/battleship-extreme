package de.hsbremen.battleshipextreme.server;

import de.hsbremen.battleshipextreme.network.IDisposable;
import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.InfoSendingReason;
import de.hsbremen.battleshipextreme.network.TransferableObjectFactory;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.transfarableObject.ClientInfo;
import de.hsbremen.battleshipextreme.network.transfarableObject.Message;
import de.hsbremen.battleshipextreme.network.transfarableObject.NetGame;
import de.hsbremen.battleshipextreme.network.transfarableObject.ServerInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * Created on 25.04.2015.
 * Thread für das Empfangen von Objekten, die über das Netzwerk vom Client kommen
 */
public class ClientListener extends Thread implements IDisposable, Serializable {
// ------------------------------ FIELDS ------------------------------

    private ServerDispatcher serverDispatcher;
    private ClientHandler clientHandler;
    private ObjectInputStream in;
    private boolean disposed;

// --------------------------- CONSTRUCTORS ---------------------------

    public ClientListener(ClientHandler clientHandler, ServerDispatcher serverDispatcher) throws IOException {
        this.disposed = false;
        this.clientHandler = clientHandler;
        this.serverDispatcher = serverDispatcher;
        Socket socket = clientHandler.getSocket();
        this.in = new ObjectInputStream(socket.getInputStream());
    }


// --------------------- Interface IDisposable ---------------------

    /**
     * Beendet den Thread
     */
    public void dispose() {
        try {
            this.disposed = true;
            this.in.close();
        } catch (IOException e) {
            serverDispatcher.getErrorHandler().errorHasOccurred(new EventArgs<ITransferable>(this, TransferableObjectFactory.CreateMessage("Clientlistner of " + clientHandler.getUsername() + " couldnt be disposed")));
        }
    }

// --------------------- Interface Runnable ---------------------

    /**
     * Horcht auf ankommende Objekte vom Client und prüft sie und leitet per Event weiter
     */
    public void run() {
        try {
            while (!isInterrupted() && !this.disposed) {
                ITransferable receivedObject = null;
                try {
                    // ließe Objekt und veruche es zu casten
                    receivedObject = (ITransferable) this.in.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                // Wenn es null ist mache nichts
                if (receivedObject == null) {
                    break;
                }
                // prüfe Objekttypen
                switch (receivedObject.getType()) {
                    case Join:
                        this.serverDispatcher.assignClientToGame(this.clientHandler, receivedObject);
                        break;
                    case ShipPlacedInformation:
                        this.serverDispatcher.addShipPlacedInformationToGame(clientHandler, receivedObject);
                        break;
                    case Game:
                        if (serverDispatcher.getNetGames().size() < serverDispatcher.getMaxGames()) {
                            this.serverDispatcher.addGame(receivedObject);
                        } else {
                            ITransferable obj = TransferableObjectFactory.CreateError("Maximum of games has reached, You could not create a new game!");
                            serverDispatcher.unicast(obj, clientHandler);
                        }
                        break;
                    case ServerInfo:
                        ServerInfo serverInfo = (ServerInfo) receivedObject;
                        NetGame game;
                        switch (serverInfo.getReason()) {
                            case GameList:
                                this.serverDispatcher.sendGameList(clientHandler);
                                break;
                            case PlayerNames:
                                this.serverDispatcher.sendNameList(clientHandler);
                                break;

                            case PlayerWon:
                                game = serverDispatcher.getGameByClient(clientHandler);
                                serverDispatcher.multicast(serverInfo, game.getJoinedPlayers());
                                serverDispatcher.deleteGame(game);
                                break;
                        }
                        break;

                    case Turn:
                        this.serverDispatcher.addTurn(this.clientHandler, receivedObject);
                        break;

                    case ClientInfo:
                        ClientInfo info = (ClientInfo) receivedObject;
                        switch (info.getReason()) {
                            case Connect:
                                boolean isNameAvailable = this.serverDispatcher.isClientNameAvailable(info.getUsername());
                                if (!isNameAvailable) {
                                    ITransferable error = TransferableObjectFactory.CreateError("Username is already taken! Please take an other one.");
                                    this.serverDispatcher.unicast(error, clientHandler);
                                } else {
                                    clientHandler.setUsername(info.getUsername());
                                    this.serverDispatcher.unicast(TransferableObjectFactory.CreateServerInfo(InfoSendingReason.Connect), clientHandler);
                                    this.serverDispatcher.printInfo(new EventArgs<ITransferable>(this, new Message(info.getIp() + ":" + info.getPort() + " has named to " + info.getUsername() + "(" + info.getPort() + ")")));
                                    this.serverDispatcher.objectReceived(new EventArgs<ITransferable>(this, info));
                                }
                                break;
                        }

                        break;
                    default:
                        this.serverDispatcher.dispatchObject(receivedObject);
                }
            }
        } catch (IOException e) {
            this.serverDispatcher.getErrorHandler().errorHasOccurred(new EventArgs<ITransferable>(this, TransferableObjectFactory.CreateMessage("Connection to " + clientHandler.getSocket().getInetAddress().getHostAddress() + ":" + clientHandler.getSocket().getPort() + " -> " + clientHandler.getUsername() + " has closed")));
            this.dispose();
        }

        // Communication is broken. Interrupt both listener and sender threads
        this.clientHandler.getClientSender().interrupt();
        this.serverDispatcher.removeClient(this.clientHandler);
    }
}

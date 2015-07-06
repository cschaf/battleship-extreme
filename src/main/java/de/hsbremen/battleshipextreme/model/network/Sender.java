package de.hsbremen.battleshipextreme.model.network;

import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.model.ship.ShipType;
import de.hsbremen.battleshipextreme.network.IDisposable;
import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.InfoSendingReason;
import de.hsbremen.battleshipextreme.network.TransferableObjectFactory;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created on 15.05.2015.
 */
public class Sender extends Thread implements IDisposable {
    private ObjectOutputStream out;
    private Socket socket;
    private boolean disposed;

    public Sender(Socket socket, ObjectOutputStream out) {
        this.setName("Client-Senderthread");
        this.disposed = false;
        this.socket = socket;
        this.out = out;
    }

    public void run() {
        try {
            while (!isInterrupted() && !this.disposed) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            this.dispose();
        }
    }

    public void sendMessage(String username, String message) {
        ITransferable sender = TransferableObjectFactory.CreateClientInfo(username, socket.getInetAddress().getHostAddress(), socket.getLocalPort());
        ITransferable object = TransferableObjectFactory.CreateClientMessage(message, sender);
        send(object);
    }

    public void sendLogin(String username) {
        ITransferable object = TransferableObjectFactory.CreateClientInfo(username, socket.getInetAddress().getHostAddress(), socket.getLocalPort(), InfoSendingReason.Connect);
        send(object);
    }

    public void requestGameList() {
        ITransferable object = TransferableObjectFactory.CreateServerInfo(InfoSendingReason.GameList);
        send(object);
    }

    public void requestNameList() {
        ITransferable object = TransferableObjectFactory.CreateServerInfo(InfoSendingReason.PlayerNames);
        send(object);
    }

    public void sendJoin(String id) {
        ITransferable object = TransferableObjectFactory.CreateJoin(id);
        send(object);
    }

    public void send(ITransferable object) {
        try {
            this.out.reset();
            this.out.writeObject(object);
            this.out.flush();
        } catch (IOException e) {
            this.dispose();
        }
        catch (Exception e) {
            this.dispose();
        }
    }

    public void dispose() {
        this.disposed = true;
    }

    public void sendTurn(String attackingPlayerName, String attackedPlayerName, int xPos, int yPos, boolean isHorizontal, ShipType shipType) {
        ITransferable object = TransferableObjectFactory.CreateTurn(attackingPlayerName, attackedPlayerName, xPos, yPos, isHorizontal, shipType);
        send(object);
    }

    public void sendTurn(ITransferable turn) {
        send(turn);
    }

    public void sendPlayerIsReloading() {
        ITransferable object =  TransferableObjectFactory.CreateServerInfo(InfoSendingReason.PlayerIsReloading);
        send(object);
    }

    public void sendPlayerWon() {
        ITransferable object =  TransferableObjectFactory.CreateServerInfo(InfoSendingReason.PlayerWon);
        send(object);
    }

    public void sendGame(String name, String password, Settings settings) {
        ITransferable object = TransferableObjectFactory.CreateGame(name, password, settings);
        send(object);
    }

    public void sendShipPlacedInformation(int xPos, int yPos, Orientation orientation, ShipType type) {
        ITransferable object = TransferableObjectFactory.CreateShipPlacedInformation(xPos, yPos, orientation, type);
        send(object);
    }
}

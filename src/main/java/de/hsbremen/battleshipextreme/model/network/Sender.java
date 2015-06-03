package de.hsbremen.battleshipextreme.model.network;

import de.hsbremen.battleshipextreme.network.IDisposable;
import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.InfoSendingReason;
import de.hsbremen.battleshipextreme.network.TransferableObjectFactory;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by cschaf on 15.05.2015.
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
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            this.dispose();
        }
    }

    public void sendMessage(String username, String message) {
        ITransferable sender = TransferableObjectFactory.CreateClientInfo(username, socket.getInetAddress().getHostAddress(), socket.getLocalPort());
        try {
            this.out.writeObject(TransferableObjectFactory.CreateClientMessage(message, sender));
            this.out.flush();
        } catch (IOException e) {
            this.dispose();
        }
    }

    public void sendLogin(String username) {
        try {
            this.out.writeObject(TransferableObjectFactory.CreateClientInfo(username, socket.getInetAddress().getHostAddress(), socket.getLocalPort(), InfoSendingReason.Connect));
            this.out.flush();
        } catch (IOException e) {
            this.dispose();
        }
    }

    public void requestGameList() {
        try {
            this.out.writeObject(TransferableObjectFactory.CreateServerInfo(InfoSendingReason.GameList));
            this.out.flush();
        } catch (IOException e) {
            this.dispose();
        }
    }

    public void dispose() {
        this.disposed = true;
    }

    public void sendJoin(String id) {
        try {
            this.out.writeObject(TransferableObjectFactory.CreateJoin(id));
            this.out.flush();
        } catch (IOException e) {
            this.dispose();
        }
    }
}
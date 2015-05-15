package de.hsbremen.battleshipextreme.server.TestClient;

import de.hsbremen.battleshipextreme.network.IDisposable;
import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.InfoSendingReason;
import de.hsbremen.battleshipextreme.network.TransferableObjectFactory;

import java.io.*;
import java.net.Socket;

/**
 * Created by cschaf on 15.05.2015.
 * Handles user inputs for stdin
 */
public class Sender extends Thread implements IDisposable {
    private ObjectOutputStream out;
    private Socket socket;
    private boolean disposed;
    private String username;

    public Sender(Socket socket, ObjectOutputStream out, String username) {
        this.username = username;
        this.disposed = false;
        this.socket = socket;
        this.out = out;
    }

    /**
     * Until interrupted reads messages from the standard input (keyboard)
     * and sends them to the chat server through the socket.
     */
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            this.out.writeObject(TransferableObjectFactory.CreateClientInfo(this.username, socket.getInetAddress().getHostAddress(), socket.getLocalPort(), InfoSendingReason.Connect));
            this.out.flush();
            while (!isInterrupted() && !this.disposed) {
                String message = in.readLine();
                if(message != null){
                    ITransferable sender = TransferableObjectFactory.CreateClientInfo(this.username, socket.getInetAddress().getHostAddress(), socket.getLocalPort());
                    this.out.writeObject(TransferableObjectFactory.CreateClientMessage(message, sender));
                    this.out.flush();
                }
            }
        } catch (IOException ioe) {
            this.dispose();
            // Communication is broken
        }
    }

    @Override
    public void dispose() {
        this.disposed = true;
    }
}

package de.hsbremen.battleshipextreme.server;

import de.hsbremen.battleshipextreme.network.IDisposable;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by cschaf on 25.04.2015.
 * ClientHandler class contains information about a client, connected to the server.
 */
public class ClientHandler implements IDisposable {
    private Socket socket;
    private ClientSender clientSender;
    private ClientListener clientListener;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    /**
     * Gets the socket object of the client
     * @return socket object
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Sets the socket object of the client
     * @param socket
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * Gets the ClientSender object
     * @return ClientSender object
     */
    public ClientSender getClientSender() {
        return clientSender;
    }

    /**
     * Sets the ClientSender for sending data to the client
     * @param sender
     */
    public void setClientSender(ClientSender sender) {
        this.clientSender = sender;
    }

    /**
     * Gets the ClientListener
     * @return ClientListener object
     */
    public ClientListener getClientListener() {
        return clientListener;
    }

    /**
     * Sets the ClientListener for receive data from the client
     * @param listener
     */
    public void setClientListener(ClientListener listener) {
        this.clientListener = listener;
    }

    public void dispose() {
        try {
            this.socket.close();
            this.clientListener.dispose();
            this.clientSender.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

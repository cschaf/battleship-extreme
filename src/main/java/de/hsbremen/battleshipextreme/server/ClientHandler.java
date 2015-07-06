package de.hsbremen.battleshipextreme.server;

import de.hsbremen.battleshipextreme.network.IDisposable;
import de.hsbremen.battleshipextreme.network.TransferableType;
import de.hsbremen.battleshipextreme.network.transfarableObject.TransferableObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

/**
 * Created on 25.04.2015.
 * ClientHandler enthält alle Informationen über den Client der mit dem Server verbunden ist
 * Enthalten sind auch der Thread für das Senden und Empfangen von Daten
 */
public class ClientHandler extends TransferableObject implements IDisposable, Serializable {
// ------------------------------ FIELDS ------------------------------

    private transient Socket socket;
    private transient ClientSender clientSender;
    private transient ClientListener clientListener;
    private String username;

// --------------------------- CONSTRUCTORS ---------------------------

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    /**
     * Gets the ClientListener
     * @return ClientListener object
     */
    public ClientListener getClientListener() {
        return clientListener;
    }

    /**
     * Sets the ClientListener for receive data from the client
     */
    public void setClientListener(ClientListener listener) {
        this.clientListener = listener;
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
     */
    public void setClientSender(ClientSender sender) {
        this.clientSender = sender;
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
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


// --------------------- Interface IDisposable ---------------------

    public void dispose() {
        try {
            this.socket.close();
            this.clientListener.dispose();
            this.clientSender.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

// --------------------- Interface ITransferable ---------------------


    public TransferableType getType() {
        return TransferableType.ClientHandler;
    }

// -------------------------- OTHER METHODS --------------------------

    public boolean hasUsername() {
        return username != null && !username.isEmpty();
    }
}

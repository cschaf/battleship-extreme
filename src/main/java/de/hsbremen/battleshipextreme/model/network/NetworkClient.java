package de.hsbremen.battleshipextreme.model.network;

import de.hsbremen.battleshipextreme.network.IDisposable;
import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.TransferableObjectFactory;
import de.hsbremen.battleshipextreme.network.eventhandling.ErrorHandler;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.eventhandling.listener.IErrorListener;

import javax.swing.event.EventListenerList;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class NetworkClient implements IDisposable {
    protected EventListenerList listeners = new EventListenerList();
    private String serverIp = "localhost";
    private int serverPort = 1337;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;
    private Socket socket = null;

    public Sender getSender() {
        return sender;
    }

    private Sender sender = null;
    private Listener listener = null;
    private String username = "Player";
    private ErrorHandler errorHandler = null;
    private ArrayList<IServerObjectReceivedListener> tempServerObjectReceivedListeners;
    private boolean isConnected;

    public NetworkClient(String serverIp, int serverPort, String username) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.username = username;
        this.errorHandler = new ErrorHandler();
        this.tempServerObjectReceivedListeners = new ArrayList<IServerObjectReceivedListener>();
    }

    public NetworkClient() {
        this.errorHandler = new ErrorHandler();
        this.tempServerObjectReceivedListeners = new ArrayList<IServerObjectReceivedListener>();
    }

    public void addErrorListener(IErrorListener listener) {
        this.errorHandler.addErrorListener(listener);
    }

    public void removeErrorListener(IErrorListener listener) {
        this.errorHandler.removeErrorListener(listener);
    }

    public void addServerObjectReceivedListener(IServerObjectReceivedListener listener) {
        if (this.listener == null) {
            tempServerObjectReceivedListeners.add(listener);
        } else {
            this.listener.addServerObjectReceivedListener(listener);
        }
    }

    public void removeServerObjectReceivedListener(IServerObjectReceivedListener listener) {
        this.listener.removeServerObjectReceivedListener(listener);
    }

    /**
     * Connect to Server
     */
    public void connect(){
        try {
            socket = new Socket(serverIp, serverPort);
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            isConnected = true;
        } catch (Exception e) {
            isConnected = false;
            errorHandler.errorHasOccurred(new EventArgs<ITransferable>(this, TransferableObjectFactory.CreateMessage("Can not establish connection to " + serverIp + ":" + serverPort)));
        }

        // Create and start Sender thread
        this.sender = new Sender(socket, out);
        this.sender.start();

        this.listener = new Listener(in, this.errorHandler);
        for (IServerObjectReceivedListener tempListener : this.tempServerObjectReceivedListeners) {
            this.listener.addServerObjectReceivedListener(tempListener);
        }
        this.tempServerObjectReceivedListeners.clear();
        this.listener.start();
    }

    public void disconnect() {
        this.dispose();
        isConnected = false;
    }

    public void dispose() {
        try {
            if (this.listener != null) {
                this.listener.dispose();
            }
            if (this.sender != null) {
                this.sender.dispose();
            }
            if (this.out != null) {
                this.out.close();
            }
            if (this.in != null) {
                this.in.close();
            }
            if (this.socket != null) {
                this.socket.close();
            }
        } catch (IOException e) {
            errorHandler.errorHasOccurred(new EventArgs<ITransferable>(this, TransferableObjectFactory.CreateMessage("Could not dispose clientobject")));
        }
    }

    public void setIp(String ip) {
        this.serverIp = ip;
    }

    public void setPort(int port) {
        this.serverPort = port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isConnected() {
        return isConnected;
    }
}

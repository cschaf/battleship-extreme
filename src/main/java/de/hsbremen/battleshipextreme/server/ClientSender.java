package de.hsbremen.battleshipextreme.server;

import de.hsbremen.battleshipextreme.network.IDisposable;
import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.TransferableObjectFactory;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by cschaf on 26.04.2015.
 */
public class ClientSender extends Thread implements IDisposable, Serializable {
    private Vector<ITransferable> objectQueue;
    private ServerDispatcher serverDispatcher;
    private ClientHandler clientHandler;
    private ObjectOutputStream out;
    private boolean disposed;

    public ClientSender(ClientHandler clientHandler, ServerDispatcher serverDispatcher) throws IOException {
        this.disposed = false;
        this.objectQueue = new Vector<ITransferable>();
        this.clientHandler = clientHandler;
        this.serverDispatcher = serverDispatcher;
        Socket socket = clientHandler.getSocket();
        this.out = new ObjectOutputStream(socket.getOutputStream());
    }

    public synchronized void addObjectToQueue(ITransferable transferableObject) {
        this.objectQueue.add(transferableObject);
        notify();
    }


    private synchronized ITransferable getNextObjectFromQueue() throws InterruptedException {
        while (this.objectQueue.size() == 0) {
            wait();
        }
        ITransferable transferableObject = this.objectQueue.get(0);
        this.objectQueue.removeElementAt(0);
        return transferableObject;
    }

    private void send(ITransferable transferableObject) {
        try {
            this.out.reset();
            this.out.writeObject(transferableObject);
            this.out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (!isInterrupted() && !this.disposed) {
                ITransferable message = this.getNextObjectFromQueue();
                this.send(message);
            }
        } catch (Exception e) {
            this.dispose();
        }

        // Communication is broken. Interrupt both listener and sender threads
        this.clientHandler.getClientListener().interrupt();
        this.serverDispatcher.deleteClient(this.clientHandler);
    }

    public void dispose() {
        this.disposed = true;
        try {
            this.out.close();
        } catch (IOException e) {
            this.serverDispatcher.getErrorHandler().errorHasOccurred(new EventArgs<ITransferable>(this, TransferableObjectFactory.CreateMessage("ClientSender outputstream could not been closed!")));
        }
    }
}

package de.hsbremen.battleshipextreme.server;

import de.hsbremen.battleshipextreme.network.IDisposable;
import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.TransferableObjectFactory;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by cschaf on 26.04.2015.
 */
public class ClientSender extends Thread implements IDisposable {
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

    /**
     * Adds given message to the message queue and notifies this thread
     * (actually getNextObjectFromQueue method) that a message is arrived.
     * addObjectToQueue is called by other threads (ServeDispatcher).
     */
    public synchronized void addObjectToQueue(ITransferable transferableObject) {
        this.objectQueue.add(transferableObject);
        notify();
    }

    /**
     * @return and deletes the next message from the message queue. If the queue
     * is empty, falls in sleep until notified for message arrival by addObjectToQueue
     * method.
     */
    private synchronized ITransferable getNextObjectFromQueue() throws InterruptedException {
        while (this.objectQueue.size() == 0) {
            wait();
        }
        ITransferable transferableObject = this.objectQueue.get(0);
        this.objectQueue.removeElementAt(0);
        return transferableObject;
    }

    /**
     * Sends given message to the client's socket.
     */
    private void send(ITransferable transferableObject) {
        try {
            this.out.writeObject(transferableObject);
            this.out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Until interrupted, reads messages from the message queue
     * and sends them to the client's socket.
     */
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

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
 * Created on 26.04.2015.
 * Wird zum Senden vom Objekten an den Client verwendet, dabei werden alle Objekte in eine Warteschlange
 * eingereiht und und run nach und nach abgearbeitet
 */
public class ClientSender extends Thread implements IDisposable, Serializable {
// ------------------------------ FIELDS ------------------------------

    private Vector<ITransferable> objectQueue;
    private ServerDispatcher serverDispatcher;
    private ClientHandler clientHandler;
    private ObjectOutputStream out;
    private boolean disposed;

// --------------------------- CONSTRUCTORS ---------------------------

    public ClientSender(ClientHandler clientHandler, ServerDispatcher serverDispatcher) throws IOException {
        this.disposed = false;
        this.objectQueue = new Vector<ITransferable>();
        this.clientHandler = clientHandler;
        this.serverDispatcher = serverDispatcher;
        Socket socket = clientHandler.getSocket();
        this.out = new ObjectOutputStream(socket.getOutputStream());
    }


// --------------------- Interface Runnable ---------------------

    /**
     * Hole dir nach und nach die Items aus der Warteschlange
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
        this.serverDispatcher.removeClient(this.clientHandler);
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * Für ein Objekt in die Warteschlange hinzu
     */
    public synchronized void addObjectToQueue(ITransferable transferableObject) {
        this.objectQueue.add(transferableObject);
        notify();
    }

    /**
     * Liefert das nächste Objekt von der Warteschlange, welches versendet werden soll
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
     * Sendet on Objekt vom type ITransferable zum CLient
     */
    private void send(ITransferable transferableObject) {
        try {
            this.out.reset(); // wird benötig damit alte Objekte vom Stream gelöscht werden
            this.out.writeObject(transferableObject);
            this.out.flush();
        } catch (IOException e) {
            dispose();
        }
    }

    /**
     * Beendet den Thread
     */
    public void dispose() {
        this.disposed = true;
        try {
            this.out.close();
        } catch (IOException e) {
            this.serverDispatcher.getErrorHandler().errorHasOccurred(new EventArgs<ITransferable>(this, TransferableObjectFactory.CreateMessage("ClientSender outputstream could not been closed!")));
        }
    }
}

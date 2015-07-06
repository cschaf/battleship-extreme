package de.hsbremen.battleshipextreme.server;

import de.hsbremen.battleshipextreme.network.IDisposable;
import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.TransferableObjectFactory;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created on 25.04.2015.
 * Wartet auf Client Verbindungsanfragen. Erstellt dann für jeden Client einen Thread für das
 * Senden und Empfangen von Objekten. Anschließend wird er der Clientlist des Server hinzugefügt.
 */
public class ClientAccepter extends Thread implements IDisposable {
// ------------------------------ FIELDS ------------------------------

    private ServerSocket serverSocket;
    private ServerDispatcher serverDispatcher;
    private boolean disposed;

// --------------------------- CONSTRUCTORS ---------------------------

    public ClientAccepter(ServerSocket serverSocket, ServerDispatcher serverDispatcher) {
        this.disposed = false;
        this.serverSocket = serverSocket;
        this.serverDispatcher = serverDispatcher;
    }

// --------------------- Interface IDisposable ---------------------

    /**
     * Beendet den Thread
     */
    public void dispose() {
        this.disposed = true;
    }

// --------------------- Interface Runnable ---------------------

    /**
     * Läuft solange nicht disposed wurde und wartet auf Clientverbindungsanfragen
     */
    public void run() {
        while (!this.disposed) {
            try {
                Socket socket = serverSocket.accept();
                // Prüfe ob die Maximale Anzahl von Clients auf dem Server erreicht wurde
                if (serverDispatcher.getClients().size() >= serverDispatcher.getMaxPlayers()) {
                    socket.close();
                }
                ClientHandler clientHandler = new ClientHandler(socket);
                // Erstellt Thread für das Senden von Daten an den Server
                ClientSender clientSender = new ClientSender(clientHandler, serverDispatcher);
                // Erstellt Thread für das Empfangen von Daten vom Server
                ClientListener clientListener = new ClientListener(clientHandler, serverDispatcher);

                clientHandler.setClientListener(clientListener);
                clientHandler.setClientSender(clientSender);
                // Starte Threads
                clientListener.start();
                clientSender.start();

                // Füge den neunen Client dem Server hinzu
                serverDispatcher.addClient(clientHandler);
                // Prüfe ob der Client gebannt ist
                boolean isBanned = serverDispatcher.isBanned(socket.getInetAddress().getHostAddress());
                if (isBanned) {
                    serverDispatcher.unicast(TransferableObjectFactory.CreateError("You are banned from this server!"), clientHandler);
                    serverDispatcher.removeClient(clientHandler);
                }
            } catch (IOException e) {
                this.serverDispatcher.getErrorHandler().errorHasOccurred(new EventArgs<ITransferable>(this, TransferableObjectFactory.CreateMessage("Stopped listening for clients")));
            }
        }
    }
}

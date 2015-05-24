package de.hsbremen.battleshipextreme.server.TestClient;

import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.model.exception.BoardTooSmallException;
import de.hsbremen.battleshipextreme.model.exception.InvalidNumberOfShipsException;
import de.hsbremen.battleshipextreme.model.exception.InvalidPlayerNumberException;
import de.hsbremen.battleshipextreme.model.player.HumanPlayer;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.model.ship.Destroyer;
import de.hsbremen.battleshipextreme.model.ship.Ship;
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
            // Connect Client
            this.out.writeObject(TransferableObjectFactory.CreateClientInfo(this.username, socket.getInetAddress().getHostAddress(), socket.getLocalPort(), InfoSendingReason.Connect));
            this.out.flush();

            // Game
/*            Settings settings = new Settings(1,1,0,5,1,0,0,0);
            ITransferable game = TransferableObjectFactory.CreateGame("Game 1", settings);
            this.out.writeObject(game);
            this.out.flush();*/

            // Turn
            Player from = new HumanPlayer(5,1,0,0,0);
            Player to = new HumanPlayer(5,1,0,0,0);
            Ship ship = new Destroyer();
            Field field = new Field(0,0);
            ITransferable game = TransferableObjectFactory.CreateTurn("ds5165156dada2sdasd", from, to, ship, field);
            this.out.writeObject(game);
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

    public void dispose() {
        this.disposed = true;
    }
}

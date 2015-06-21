package de.hsbremen.battleshipextreme.network;

import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.model.ship.ShipType;
import de.hsbremen.battleshipextreme.network.transfarableObject.*;
import de.hsbremen.battleshipextreme.network.transfarableObject.Error;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by cschaf on 26.04.2015.
 */
public class TransferableObjectFactory {
    /**
     * Creates a ClientInfo
     **/
    public static ITransferable CreateClientMessage(String message, ITransferable sender) {
        return new ClientMessage(message, sender);
    }

    /**
     * Creates a ClientInfo
     **/
    public static ITransferable CreateClientInfo(String username, String ip, int port) {
        return new ClientInfo(username, ip, port);
    }

    /**
     * Creates a ClientInfo with InfoSendingReason
     **/
    public static ITransferable CreateClientInfo(String username, String ip, int port, InfoSendingReason reason) {
        return new ClientInfo(username, ip, port, reason);
    }

    /**
     * Creates a Message
     **/
    public static ITransferable CreateMessage(String message) {
        return new Message(message);
    }

    /**
     * Creates a Error
     **/
    public static ITransferable CreateError(String message) {
        return new Error(message);
    }

    /**
     * Creates a NetGame
     **/
    public static ITransferable CreateGame(String name, String password, Settings settings) {
        return new NetGame(name, password, settings);
    }

    /**
     * Creates a Join
     **/
    public static ITransferable CreateJoin(String id) {
        return new Join(id);
    }

    /**
     * Creates a GameList
     **/
    public static ITransferable CreateGameList(Vector<NetGame> gameList) {
        return new GameList(gameList);
    }

    /**
     * Creates an Turn
     **/
    public static ITransferable CreateTurn(String attackingPlayerName, String attackedPlayerName, int fieldX, int fieldY, boolean isHorizontal, ShipType shipType) {
        return new Turn(attackingPlayerName, attackedPlayerName, fieldX, fieldY, isHorizontal, shipType);
    }

    /**
     * Creates an Turn
     **/
    public static ITransferable CreateClientTurn(Field[] fields, boolean isReloading, String attackingPlayerName, String attackedPlayerName) {
        return new ClientTurn(fields, isReloading, attackingPlayerName, attackedPlayerName);
    }

    /**
     * Creates an Turn
     **/
    public static ITransferable CreateClientTurn(Field[] fields, boolean isReloading, String attackingPlayerName, String attackedPlayerName, String winnerName) {
        ClientTurn turn = new ClientTurn(fields, isReloading, attackingPlayerName, attackedPlayerName);
        turn.setWinnerName(winnerName);
        turn.setIsWinner(true);
        return turn;
    }

    /**
     * Creates an Turn
     **/
    public static ITransferable CreateClientTurn(String winnerName) {
        return new ClientTurn(winnerName);
    }

    /**
     * Creates a Turn with property isReloading = true
     **/
    public static ITransferable CreateTurn() {
        return new Turn();
    }

    /**
     * Creates a ServerInfo
     **/
    public static ITransferable CreateServerInfo(InfoSendingReason reason) {
        return new ServerInfo(reason);
    }

    /**
     * Creates a PlayerNames
     **/
    public static ITransferable CreatePlayerNames(ArrayList<String> names) {
        return new PlayerNames(names);
    }

    /**
     * Creates a ShipPlacedInformation
     **/
    public static ITransferable CreateShipPlacedInformation(int xPos, int yPos, Orientation orientation, ShipType type) {
        return new ShipPlacedInformation(xPos, yPos, orientation, type);
    }
}

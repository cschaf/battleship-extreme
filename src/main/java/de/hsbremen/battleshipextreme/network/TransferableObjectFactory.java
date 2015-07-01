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
 * Ist zentral für die Erstellung aller Objekte, die über das Netzwerk übertragen werden sollen, zuständig
 */
public class TransferableObjectFactory {
    /**
     * Erzeugt ein ClientInfo
     **/
    public static ITransferable CreateClientMessage(String message, ITransferable sender) {
        return new ClientMessage(message, sender);
    }

    /**
     * Erzeugt ein ClientInfo
     **/
    public static ITransferable CreateClientInfo(String username, String ip, int port) {
        return new ClientInfo(username, ip, port);
    }

    /**
     * Erzeugt ein ClientInfo with InfoSendingReason
     **/
    public static ITransferable CreateClientInfo(String username, String ip, int port, InfoSendingReason reason) {
        return new ClientInfo(username, ip, port, reason);
    }

    /**
     * Erzeugt eine Message
     **/
    public static ITransferable CreateMessage(String message) {
        return new Message(message);
    }

    /**
     * Erzeugt ein Error
     **/
    public static ITransferable CreateError(String message) {
        return new Error(message);
    }

    /**
     * Erzeugt ein NetGame
     **/
    public static ITransferable CreateGame(String name, String password, Settings settings) {
        return new NetGame(name, password, settings);
    }

    /**
     * Erzeugt ein Join
     **/
    public static ITransferable CreateJoin(String id) {
        return new Join(id);
    }

    /**
     * Erzeugt eine GameList
     **/
    public static ITransferable CreateGameList(Vector<NetGame> gameList) {
        return new GameList(gameList);
    }

    /**
     * Erzeugt einen Turn
     **/
    public static ITransferable CreateTurn(String attackingPlayerName, String attackedPlayerName, int fieldX, int fieldY, boolean isHorizontal, ShipType shipType) {
        return new Turn(attackingPlayerName, attackedPlayerName, fieldX, fieldY, isHorizontal, shipType);
    }

    /**
     * Erzeugt einen Turn
     **/
    public static ITransferable CreateClientTurn(Field[] fields, boolean isReloading, String attackingPlayerName, String attackedPlayerName) {
        return new ClientTurn(fields, isReloading, attackingPlayerName, attackedPlayerName);
    }

    /**
     * Erzeugt einen Turn
     **/
    public static ITransferable CreateClientTurn(Field[] fields, boolean isReloading, String attackingPlayerName, String attackedPlayerName, String winnerName) {
        ClientTurn turn = new ClientTurn(fields, isReloading, attackingPlayerName, attackedPlayerName);
        turn.setWinnerName(winnerName);
        turn.setIsWinner(true);
        return turn;
    }

    /**
     * Erzeugt einen Turn mit der Property isReloading = true
     **/
    public static ITransferable CreateTurn(String reloadingPlayer) {
        return new Turn(reloadingPlayer);
    }

    /**
     * Erzeugt eine ServerInfo
     **/
    public static ITransferable CreateServerInfo(InfoSendingReason reason) {
        return new ServerInfo(reason);
    }

    /**
     * Erzeugt PlayerNames
     **/
    public static ITransferable CreatePlayerNames(ArrayList<String> names) {
        return new PlayerNames(names);
    }

    /**
     * Erzeugt ShipPlacedInformation
     **/
    public static ITransferable CreateShipPlacedInformation(int xPos, int yPos, Orientation orientation, ShipType type) {
        return new ShipPlacedInformation(xPos, yPos, orientation, type);
    }
}

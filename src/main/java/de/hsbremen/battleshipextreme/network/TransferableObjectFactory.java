package de.hsbremen.battleshipextreme.network;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.model.ship.Ship;
import de.hsbremen.battleshipextreme.model.ship.ShipType;
import de.hsbremen.battleshipextreme.network.transfarableObject.*;
import de.hsbremen.battleshipextreme.network.transfarableObject.Error;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by cschaf on 26.04.2015.
 */
public class TransferableObjectFactory {
    public static ITransferable CreateClientMessage(String message, ITransferable sender) {
        return new ClientMessage(message, sender);
    }

    public static ITransferable CreateClientInfo(String username, String ip, int port) {
        return new ClientInfo(username, ip, port);
    }

    public static ITransferable CreateClientInfo(String username, String ip, int port, InfoSendingReason reason) {
        return new ClientInfo(username, ip, port, reason);
    }

    public static ITransferable CreateMessage(String message) {
        return new Message(message);
    }

    public static ITransferable CreateError(String message) {
        return new Error(message);
    }

    public static ITransferable CreateGame(String name, String password, Settings settings) {
        return new NetGame(name, password, settings);
    }

    public static ITransferable CreateJoin(String id) {
        return new Join(id);
    }

    public static ITransferable CreateGameList(Vector<NetGame> gameList) {
        return new GameList(gameList);
    }

    public static ITransferable CreatePlayerBoards(ArrayList<Board> boards) {
        return new PlayerBoards(boards);
    }

    public static ITransferable CreateTurn(String attackingPlayerName, String attackedPlayerName, int fieldX, int fieldY, boolean isHorizontal, Ship currentShip) {
        return new Turn(attackingPlayerName, attackedPlayerName, fieldX, fieldY, isHorizontal, currentShip);
    }

    public static ITransferable CreateServerInfo(InfoSendingReason reason) {
        return new ServerInfo(reason);
    }

    public static ITransferable CreatePlayerNames(ArrayList<String> names) {
        return new PlayerNames(names);
    }

    public static ITransferable CreateShipPlacedInformation(int xPos, int yPos, Orientation orientation, ShipType type) {
        return new ShipPlacedInformation(xPos, yPos, orientation, type);
    }
}

package de.hsbremen.battleshipextreme.network;

import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.network.transfarableObject.*;

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

    public static ITransferable CreateGame(String name, Settings settings) {
        return new NetGame(name, settings);
    }

    public static ITransferable CreateJoin(String id) {
        return new Join(id);
    }

    public static ITransferable CreateGameList(Vector<NetGame> gameList) {
        return new GameList(gameList);
    }

    public static ITransferable CreateTurn(Player from, Player to, int fieldX, int fieldY, Orientation orientation) {
        return new Turn(from, to, fieldX, fieldY, orientation);
    }

    public static ITransferable CreateServerInfo(InfoSendingReason reason) {
        return new ServerInfo(reason);
    }
}

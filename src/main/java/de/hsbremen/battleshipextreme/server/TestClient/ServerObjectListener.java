package de.hsbremen.battleshipextreme.server.TestClient;

import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.transfarableObject.*;

/**
 * Created by cschaf on 15.05.2015.
 */
public class ServerObjectListener implements IServerObjectReceivedListener {

    public void onObjectReceived(EventArgs<ITransferable> eventArgs) {

    }

    public void onMessageObjectReceived(EventArgs<Message> eventArgs) {
        System.out.println(eventArgs.getItem());
    }

    public void onClientInfoObjectReceived(EventArgs<ClientInfo> eventArgs) {
        switch (eventArgs.getItem().getReason()) {
            case Connect:
                System.out.println("--- New User for user list");
                System.out.println(eventArgs.getItem().getUsername() + "(" + eventArgs.getItem().getPort() + ")");
                System.out.println("--- end ---");
                break;
            case Disconnect:
                System.out.println("--- Remove User from user list");
                System.out.println(eventArgs.getItem().getUsername() + "(" + eventArgs.getItem().getPort() + ")");
                System.out.println("--- end ---");
                break;
        }
    }

    public void onGameObjectReceived(EventArgs<NetGame> eventArgs) {
        NetGame netGame = eventArgs.getItem();
        System.out.println("--- New Game");
        System.out.println("Name: " + netGame.getName());
        System.out.println("Destroyers: " + netGame.getSettings().getDestroyers());
        System.out.println("Corvettes: " + netGame.getSettings().getCorvettes());
        System.out.println("Frigates: " + netGame.getSettings().getFrigates());
        System.out.println("Submarines: " + netGame.getSettings().getSubmarines());
        System.out.println("Boardsize: " + netGame.getSettings().getBoardSize());
        System.out.println("--- end ---");
    }

    public void onTurnObjectReceived(EventArgs<Turn> eventArgs) {
        Turn turn = eventArgs.getItem();
        System.out.println("--- New Turn");
        System.out.println("GameID: : " + turn.getGameId());
        System.out.println("From-Name: " + turn.getAttackingPlayerName());
        System.out.println("To-Name: " + turn.getAttackedPlayerName());
        System.out.println("Field X: " + turn.getFieldX());
        System.out.println("Field Y : " + turn.getFieldY());
        String orientation = turn.isHorizontal() ? "Horizontal" : "Vertical";
        System.out.println("Orientation : " + orientation);
        System.out.println("--- end ---");
    }
}

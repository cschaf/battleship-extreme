package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.network.TransferableType;

import java.util.Vector;

/**
 * Created by cschaf on 02.06.2015.
 */
public class GameList extends TransferableObject {
    private Vector<Vector> gameList;

    public GameList(Vector<Vector> gameList) {

        this.gameList = gameList;
    }

    public TransferableType getType() {
        return TransferableType.GameList;
    }

    public Vector<Vector> getGameList() {
        return gameList;
    }
}

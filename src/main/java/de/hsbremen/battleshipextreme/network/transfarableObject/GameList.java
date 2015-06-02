package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.network.TransferableType;

import java.util.Vector;

/**
 * Created by cschaf on 02.06.2015.
 */
public class GameList extends TransferableObject {
    private Vector<NetGame> netGameList;

    public GameList(Vector<NetGame> netGameList) {

        this.netGameList = netGameList;
    }

    public TransferableType getType() {
        return TransferableType.GameList;
    }

    public Vector<NetGame> getNetGameList() {
        return netGameList;
    }
}

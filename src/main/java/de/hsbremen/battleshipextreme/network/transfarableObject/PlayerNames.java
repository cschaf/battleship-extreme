package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.network.TransferableType;

import java.util.ArrayList;

/**
 * Created on 06.06.2015.
 * beinhaltet eine Liste mit Spielernamen, die für den Client ben�tigt wird
 */
public class PlayerNames extends TransferableObject {

    ArrayList<String> names;

    public PlayerNames(ArrayList<String> names) {
        this.names = names;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public TransferableType getType() {
        return TransferableType.PlayerNames;
    }
}

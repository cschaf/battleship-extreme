package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.network.TransferableType;

/**
 * Created by cschaf on 07.05.2015.
 */
public class Join extends TransferableObject {
    private String gameId;

    public Join(String gameId) {
        this.gameId = gameId;
    }

    public TransferableType getType() {
        return TransferableType.Join;
    }

    public String getGameId() {
        return gameId;
    }
}

package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.network.TransferableType;

/**
 * Created by cschaf on 07.05.2015.
 * Beinhaltet alle Informationen die für das Joinen eines Clients in ein Spiel notwendig sind
 */
public class Join extends TransferableObject {
    private String client; // Name des Clients
    private String gameId; // ID des Spiels, in welches gejoint werden soll

    public Join(String gameId) {
        this.gameId = gameId;
    }

    public TransferableType getType() {
        return TransferableType.Join;
    }

    public String getGameId() {
        return gameId;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }
}

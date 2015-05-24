package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.model.ship.Ship;
import de.hsbremen.battleshipextreme.network.TransferableType;

/**
 * Created by cschaf on 30.04.2015.
 */
public class Turn extends TransferableObject {
    private String gameId;
    private Player from;
    private Player to;
    private Ship ship;
    private Field field;

    public Turn(String gameId, Player from, Player to, Ship ship, Field field) {
        this.gameId = gameId;
        this.from = from;
        this.to = to;
        this.ship = ship;
        this.field = field;
    }

    public TransferableType getType() {
        return TransferableType.Turn;
    }

    public Player getFrom() {
        return from;
    }

    public Player getTo() {
        return to;
    }

    public Ship getShip() {
        return ship;
    }

    public Field getField() {
        return field;
    }

    public String getGameId() {
        return gameId;
    }
}

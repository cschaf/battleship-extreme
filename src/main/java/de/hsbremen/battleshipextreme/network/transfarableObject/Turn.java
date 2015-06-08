package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.model.ship.Ship;
import de.hsbremen.battleshipextreme.network.TransferableType;

/**
 * Created by cschaf on 30.04.2015.
 */
public class Turn extends TransferableObject {
    private String gameId;
    private String attackingPlayerName;
    private String attackedPlayerName;
    private int fieldX;
    private int fieldY;
    private boolean isHorizontal;
    private Ship currentShip;

    public Turn(String attackingPlayerName, String attackedPlayerName, int fieldX, int fieldY, boolean isHorizontal, Ship currentShip) {
        this.attackingPlayerName = attackingPlayerName;
        this.attackedPlayerName = attackedPlayerName;
        this.fieldX = fieldX;
        this.fieldY = fieldY;
        this.isHorizontal = isHorizontal;
        this.currentShip = currentShip;
    }

    public TransferableType getType() {
        return TransferableType.Turn;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getFieldX() {
        return fieldX;
    }

    public int getFieldY() {
        return fieldY;
    }

    public String getAttackedPlayerName() {
        return attackedPlayerName;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public Ship getCurrentShip() {
        return currentShip;
    }

    public String getAttackingPlayerName() {
        return attackingPlayerName;
    }
}

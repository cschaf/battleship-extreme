package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.model.ship.ShipType;
import de.hsbremen.battleshipextreme.network.TransferableType;

/**
 * Created by cschaf on 30.04.2015.
 */
public class Turn extends TransferableObject {
// ------------------------------ FIELDS ------------------------------

    private String gameId;
    private String attackingPlayerName;
    private String attackedPlayerName;
    private int fieldX;
    private int fieldY;
    private boolean isHorizontal;
    private ShipType shipType;
    private boolean isReloading;

// --------------------------- CONSTRUCTORS ---------------------------

    public Turn(String reloadingPlayer) {
        this.attackingPlayerName = reloadingPlayer;
        this.attackedPlayerName = "";
        this.fieldX = -1;
        this.fieldY = -1;
        this.isHorizontal = false;
        this.shipType = null;
        this.isReloading = true;
    }

    public Turn(String attackingPlayerName, String attackedPlayerName, int fieldX, int fieldY, boolean isHorizontal, ShipType shipType) {
        this.attackingPlayerName = attackingPlayerName;
        this.attackedPlayerName = attackedPlayerName;
        this.fieldX = fieldX;
        this.fieldY = fieldY;
        this.isHorizontal = isHorizontal;
        this.shipType = shipType;
        this.isReloading = false;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getAttackedPlayerName() {
        return attackedPlayerName;
    }

    public String getAttackingPlayerName() {
        return attackingPlayerName;
    }

    public int getFieldX() {
        return fieldX;
    }

    public int getFieldY() {
        return fieldY;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public ShipType getShipType() {
        return shipType;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface ITransferable ---------------------

    public TransferableType getType() {
        return TransferableType.Turn;
    }

// -------------------------- OTHER METHODS --------------------------

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public boolean isReloading() {
        return isReloading;
    }
}

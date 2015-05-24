package de.hsbremen.battleshipextreme.model;

import de.hsbremen.battleshipextreme.model.ship.Ship;

import java.io.Serializable;

public class Field implements Serializable {
    private int xPos;
    private int yPos;
    private Ship ship;
    private boolean isHit;

    public Field(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public boolean hasShip() {
        return ship != null;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public boolean isHit() {
        return isHit;
    }

    public void setHit(boolean isHit) {
        this.isHit = isHit;
    }

    /**
     * Provides a field state for the field to facilitate the displaying of a
     * field.
     * @return the field state.
     */
    public FieldState getState() {
        // erleichtert das Ausgeben der Felder
        if (this.isHit()) {
            if (this.hasShip()) {
                if (this.getShip().isDestroyed()) {
                    return FieldState.Destroyed;
                } else {
                    return FieldState.Hit;
                }
            } else {
                return FieldState.Missed;
            }
        } else {
            if (this.hasShip()) {
                return FieldState.HasShip;
            } else {
                return FieldState.IsEmpty;
            }
        }
    }
}

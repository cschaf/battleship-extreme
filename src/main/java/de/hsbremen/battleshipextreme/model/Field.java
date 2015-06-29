package de.hsbremen.battleshipextreme.model;

import java.io.Serializable;

import de.hsbremen.battleshipextreme.model.ship.Ship;

/**
 * Die Klasse stellt ein einzelnes Feld in einem Board dar.
 *
 */

public class Field implements Serializable {
	private static final long serialVersionUID = -2978683421156263930L;
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
		if (!hasShip()) {
			this.ship = ship;
		}
	}

	public boolean isHit() {
		return isHit;
	}

	public void mark() {
		isHit = true;
	}

	/**
	 * Liefert den Zustand eines Feldes.
	 *
	 * @return FieldState
	 */
	public FieldState getState() {
		if (this.isHit()) {
			if (this.hasShip()) {
				if (this.getShip().isDestroyed()) {
					return FieldState.DESTROYED;
				} else {
					return FieldState.HIT;
				}
			} else {
				return FieldState.MISSED;
			}
		} else {
			if (this.hasShip()) {
				return FieldState.HAS_SHIP;
			} else {
				return FieldState.IS_EMPTY;
			}
		}
	}
}

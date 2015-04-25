package de.hsbremen.battleshipextreme.model;

import de.hsbremen.battleshipextreme.model.exception.FieldOccupiedException;
import de.hsbremen.battleshipextreme.model.ship.Ship;

public class Field {
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
	
	public boolean isHit() {
		return isHit;
	}

	public void setHit(boolean isHit) {
		this.isHit = isHit;
	}

	public void setShip(Ship ship) throws FieldOccupiedException {
		if (this.hasShip()) {
			throw new FieldOccupiedException(this); // hat Schiff Exception
		}
		this.ship = ship;
	}
}

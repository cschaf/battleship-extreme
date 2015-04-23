package de.hsbremen.battleshipextreme.model;

import de.hsbremen.battleshipextreme.model.ship.Ship;

public class Field {
	private int xPos;
	private int yPos;
	private Ship ship;
	
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
		if (this.hasShip()) {
			// hat Schiff Exception
		}
		this.ship = ship;
	}
}

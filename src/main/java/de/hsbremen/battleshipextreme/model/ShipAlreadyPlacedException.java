package de.hsbremen.battleshipextreme.model;

import de.hsbremen.battleshipextreme.model.ship.Ship;

public class ShipAlreadyPlacedException extends Exception {
	public ShipAlreadyPlacedException(Ship ship) {
		super("Ship " + ship + " is already placed!");
		this.ship = ship;
	}
	
	private Ship ship;
	
	public Ship getShip() {
		return ship;
	}
}

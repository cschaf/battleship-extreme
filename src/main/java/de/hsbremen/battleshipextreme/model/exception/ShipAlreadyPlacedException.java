package de.hsbremen.battleshipextreme.model.exception;

import de.hsbremen.battleshipextreme.model.ship.Ship;

public class ShipAlreadyPlacedException extends Exception {
	private Ship ship;
	
	public ShipAlreadyPlacedException(Ship ship) {
		super("Ship " + ship + " is already placed!");
		this.ship = ship;
	}
	
	public Ship getShip() {
		return ship;
	}
}

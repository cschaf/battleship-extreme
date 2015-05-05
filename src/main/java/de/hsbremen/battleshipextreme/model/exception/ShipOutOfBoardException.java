package de.hsbremen.battleshipextreme.model.exception;

import de.hsbremen.battleshipextreme.model.ship.Ship;

public class ShipOutOfBoardException extends Exception {
	private Ship ship;

	public ShipOutOfBoardException(Ship ship) {
		super("Ship " + ship + " is not within the allowed board area!");
		this.ship = ship;
	}
	
	public Ship getShip() {
		return ship;
	}
}

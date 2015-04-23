package de.hsbremen.battleshipextreme.model;

import de.hsbremen.battleshipextreme.model.ship.Ship;

public class ShipAlreadyPlacedException extends Exception {
	public ShipAlreadyPlacedException(Ship ship) {
		super("Ship " + ship + " is already placed!");
	}
	
	public ShipAlreadyPlacedException() {
		super("The ship is already placed!");
	}
}

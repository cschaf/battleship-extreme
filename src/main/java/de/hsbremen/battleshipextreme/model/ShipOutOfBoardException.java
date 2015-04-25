package de.hsbremen.battleshipextreme.model;

import de.hsbremen.battleshipextreme.model.ship.Ship;

public class ShipOutOfBoardException extends Exception {
	public ShipOutOfBoardException(Ship ship) {
		super("Ship " + ship + " is not within the allowed board area!");
	}
}

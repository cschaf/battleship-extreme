package de.hsbremen.battleshipextreme.model.ship;

import de.hsbremen.battleshipextreme.model.ShipType;

public class Corvette extends Ship {

	public Corvette() {
		this.size = 3;
		this.shootingRange = 1;
		this.maxReloadTime = 1;
		this.shipType = ShipType.Corvette;
	}

}

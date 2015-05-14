package de.hsbremen.battleshipextreme.model.ship;

import de.hsbremen.battleshipextreme.model.ShipType;

public class Destroyer extends Ship {
	
	public Destroyer() {
		this.size = 5;
		this.shootingRange = 3;
		this.maxReloadTime = 3;
		this.type = ShipType.Destroyer;
	}
}

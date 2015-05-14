package de.hsbremen.battleshipextreme.model.ship;

import de.hsbremen.battleshipextreme.model.ShipType;

public class Submarine extends Ship {

	public Submarine() {
		this.size = 2;
		this.shootingRange = 1;
		this.maxReloadTime = 1;
		this.type = ShipType.Submarine;
	}
}

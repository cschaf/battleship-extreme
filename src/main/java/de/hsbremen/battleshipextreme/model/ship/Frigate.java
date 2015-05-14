package de.hsbremen.battleshipextreme.model.ship;

import de.hsbremen.battleshipextreme.model.ShipType;

public class Frigate extends Ship {

	public Frigate() {
		this.size = 4;
		this.shootingRange = 2;
		this.maxReloadTime = 2;
		this.type = ShipType.Frigate;
	}

}

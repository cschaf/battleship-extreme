package de.hsbremen.battleshipextreme.model.ship;

import de.hsbremen.battleshipextreme.model.Settings;

public class Submarine extends Ship {

	public Submarine() {
		this.size = Settings.SUBMARINE_SIZE;
		this.shootingRange = 1;
		this.maxReloadTime = 1;
		this.type = ShipType.SUBMARINE;
	}
}

package de.hsbremen.battleshipextreme.model.ship;

import de.hsbremen.battleshipextreme.model.Settings;

public class Frigate extends Ship {

	public Frigate() {
		this.size = Settings.FRIGATE_SIZE;
		this.shootingRange = 2;
		this.maxReloadTime = 2;
		this.type = ShipType.FRIGATE;
	}

}

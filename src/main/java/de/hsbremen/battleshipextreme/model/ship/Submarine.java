package de.hsbremen.battleshipextreme.model.ship;

import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.model.ShipType;

public class Submarine extends Ship {

	public Submarine() {
		this.size = Settings.SUBMARINE_SIZE;
		this.shootingRange = 1;
		this.maxReloadTime = 1;
		this.type = ShipType.Submarine;
	}
}

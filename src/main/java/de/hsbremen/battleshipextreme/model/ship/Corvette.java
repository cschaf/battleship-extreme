package de.hsbremen.battleshipextreme.model.ship;

import de.hsbremen.battleshipextreme.model.Settings;

public class Corvette extends Ship {

	public Corvette() {
		this.size = Settings.CORVETTE_SIZE;
		this.shootingRange = 1;
		this.maxReloadTime = 1;
		this.type = ShipType.Corvette;
	}

}

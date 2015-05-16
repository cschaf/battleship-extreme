package de.hsbremen.battleshipextreme.model.ship;

import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.model.ShipType;

public class Destroyer extends Ship {

	public Destroyer() {
		this.size = Settings.DESTROYER_SIZE;
		this.shootingRange = 3;
		this.maxReloadTime = 3;
		this.type = ShipType.Destroyer;
	}
}

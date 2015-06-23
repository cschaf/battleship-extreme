package de.hsbremen.battleshipextreme.model.ship;

import de.hsbremen.battleshipextreme.model.Settings;

public class Destroyer extends Ship {
	private static final long serialVersionUID = 1668124704540481822L;

	public Destroyer() {
		this.size = Settings.DESTROYER_SIZE;
		this.shootingRange = 3;
		this.maxReloadTime = 3;
		this.type = ShipType.DESTROYER;
	}
}

package de.hsbremen.battleshipextreme.model.ship;

import de.hsbremen.battleshipextreme.model.Settings;

public class Frigate extends Ship {
	private static final long serialVersionUID = 6105040590106431616L;

	public Frigate() {
		this.size = Settings.FRIGATE_SIZE;
		this.shootingRange = 2;
		this.maxReloadTime = 2;
		this.type = ShipType.FRIGATE;
	}

}

package de.hsbremen.battleshipextreme.model.ship;

import de.hsbremen.battleshipextreme.model.ShipType;

public abstract class Ship {
	protected int size;
	protected int shootingRange;
	protected int maxReloadTime;
	protected int currentReloadTime;
	protected ShipType type;
	
	public boolean isDestroyed() {
		return size <= 0;
	}
	
	public int getSize() {
		return size;
	}

	public int getShootingRange() {
		return shootingRange;
	}

	public int getMaxReloadTime() {
		return maxReloadTime;
	}

	public int getCurrentReloadTime() {
		return currentReloadTime;
	}
	
	public ShipType getType() {
		return type;
	}
	
	public String toString() {
		return type.toString();
	}
}

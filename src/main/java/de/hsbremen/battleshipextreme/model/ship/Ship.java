package de.hsbremen.battleshipextreme.model.ship;

import java.io.Serializable;

public abstract class Ship implements Serializable {
	protected int size;
	protected int shootingRange;
	protected int maxReloadTime;
	protected int currentReloadTime;
	protected ShipType type;
	protected boolean isPlaced;

	public void setReloadTimeToMax() {
		currentReloadTime = maxReloadTime;
	}

	public void decreaseCurrentReloadTime() {
		if (this.currentReloadTime > 0)
			this.currentReloadTime--;
	}

	public boolean isReloading() {
		return currentReloadTime > 0;
	}

	public void setPlaced() {
		this.isPlaced = true;
	}

	public boolean isPlaced() {
		return isPlaced;
	}

	public boolean isDestroyed() {
		return size <= 0;
	}

	public void setSize(int size) {
		this.size = size;
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

	public void setPlaced(boolean isPlaced) {
		this.isPlaced = isPlaced;

	}
}

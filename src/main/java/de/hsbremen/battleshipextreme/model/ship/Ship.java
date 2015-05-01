package de.hsbremen.battleshipextreme.model.ship;

import de.hsbremen.battleshipextreme.model.ShipType;
import de.hsbremen.battleshipextreme.network.TransferableType;
import de.hsbremen.battleshipextreme.network.transfarableObject.TransferableObject;

public abstract class Ship extends TransferableObject{
	protected int size;
	protected int shootingRange;
	protected int maxReloadTime;
	protected int currentReloadTime;
	protected ShipType shipType;
	protected boolean isPlaced;
	
	public void setPlaced() {
		this.isPlaced = true;
	}
	
	public boolean isPlaced() {
		return isPlaced;
	}
	
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
	
	public ShipType getShipType() {
		return shipType;
	}

	@Override
	public TransferableType getType() {
		return TransferableType.Ship;
	}
	
	public String toString() {
		return shipType.toString();
	}
}

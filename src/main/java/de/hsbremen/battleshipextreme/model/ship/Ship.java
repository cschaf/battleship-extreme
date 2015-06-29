package de.hsbremen.battleshipextreme.model.ship;

import java.io.Serializable;

/**
 * Die abstrakte Klasse Ship dient als Grundlage für spezielle Schiffklassen.
 *
 */

public abstract class Ship implements Serializable {
	private static final long serialVersionUID = 4170976318179394728L;
	protected int size;
	protected int shootingRange;
	protected int maxReloadTime;
	protected int currentReloadTime;
	protected ShipType type;
	protected boolean isPlaced;

	/**
	 * Setzt die Nachladezeit auf den Maximalwert.
	 */
	public void shoot() {
		currentReloadTime = maxReloadTime + 1;
	}

	/**
	 * Zählt die Nachladezeit herunter.
	 */
	public void decreaseCurrentReloadTime() {
		if (currentReloadTime > 0)
			currentReloadTime--;
	}

	/**
	 * Gibt an, ob das Schiff gerade nachlädt.
	 * 
	 * @return true, wenn das Schiff nachlädt, false wenn nicht.
	 */
	public boolean isReloading() {
		return currentReloadTime > 0;
	}

	public void place() {
		this.isPlaced = true;
	}

	public boolean isPlaced() {
		return isPlaced;
	}

	public boolean isDestroyed() {
		return size <= 0;
	}

	public void decreaseSize() {
		if (size > 0)
			size--;
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

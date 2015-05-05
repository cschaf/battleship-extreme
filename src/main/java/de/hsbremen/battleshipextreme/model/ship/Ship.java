package de.hsbremen.battleshipextreme.model.ship;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.ShipType;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;

public abstract class Ship {
	protected int size;
	protected int shootingRange;
	protected int maxReloadTime;
	protected int currentReloadTime;
	protected ShipType type;
	protected boolean isPlaced;

	public boolean shoot(Board boardShotAt, Field field, Orientation orientation) throws FieldOutOfBoardException {
		if (!field.isHit()) {
			fireShot(field, orientation, boardShotAt);
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param field
	 *            the targeted field.
	 * @param orientation
	 *            the orientation of the shot (horizontal/vertical)
	 * @param boardShotAt
	 *            the board to shoot at.
	 * @throws FieldOutOfBoardException
	 */
	private void fireShot(Field field, Orientation orientation, Board boardShotAt) throws FieldOutOfBoardException {
		int xDirection = orientation == Orientation.Horizontal ? 1 : 0;
		int yDirection = orientation == Orientation.Vertical ? 1 : 0;
		int x;
		int y;
		for (int i = 0; i < this.shootingRange; i++) {
			x = field.getXPos() + i * xDirection;
			y = field.getYPos() + i * yDirection;
			// Schüsse ignorieren, die außerhalb des Feldes liegen
			if (boardShotAt.containsFieldAtPosition(x, y)) {
				Field fieldShotAt = boardShotAt.getField(x, y);
				// wenn Board schon beschossen wurde, dann Schuss ignorieren
				if (!fieldShotAt.isHit()) {

					// wenn das Feld auf das geschossen wurde ein Schiff hat,
					// dann ein Leben vom Schiff abziehen
					if (fieldShotAt.hasShip()) {
						Ship ship = field.getShip();
						ship.setSize(ship.getSize() - 1);
					}
					fieldShotAt.setHit(true);
				}
				this.currentReloadTime = this.maxReloadTime;
			}
		}
	}

	public boolean canShipBeSelected() {
		return !(this.isReloading() || this.isDestroyed());
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
}

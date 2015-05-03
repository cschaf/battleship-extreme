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

	/**
	 * 
	 * @param player
	 *            the player to attack
	 * @param startX
	 *            the x-coordinate the attacked players' board.
	 * @param startY
	 *            the y-coordinate the attacked players' board.
	 * @param orientation
	 *            the orientation of the shot (vertical / horizontal).
	 * @return true if the shot was successfully fired, else false.
	 * @throws FieldOutOfBoardException
	 *             if the shot does not start within the field.
	 */
	public boolean shoot(Board boardShotAt, Field field, Orientation orientation) throws FieldOutOfBoardException {
		int xDirection = orientation == Orientation.Horizontal ? 1 : 0;
		int yDirection = orientation == Orientation.Vertical ? 1 : 0;
		int startX = field.getXPos();
		int startY = field.getYPos();
		if (isShotPossible(startX, startY, boardShotAt)) {
			fireShot(startX, startY, xDirection, yDirection, boardShotAt);
			return true;
		}
		return false;
	}

	/**
	 * Checks if the starting position of the shot is within the board. Also
	 * checks if the field was already shot at.
	 * 
	 * @param x
	 *            the x-coordinate of the shot.
	 * @param y
	 *            the y-coordinate of the shot.
	 * @param boardShotAt
	 *            the board to shoot at.
	 * @return true if the shot is possible, else false
	 * @throws FieldOutOfBoardException
	 */
	private boolean isShotPossible(int x, int y, Board boardShotAt) throws FieldOutOfBoardException {
		return isFieldWithinBoard(x, y, boardShotAt) && (!boardShotAt.getField(x, y).isHit());
	}

	private boolean isFieldWithinBoard(int x, int y, Board boardShotAt) {
		return (x < boardShotAt.getSize()) && (y < boardShotAt.getSize()) && (x >= 0) && (y >= 0);
	}

	/**
	 * 
	 * @param startX
	 *            the start x-coordinate of the shot.
	 * @param startY
	 *            the start y-coordinate of the shot.
	 * @param xDirection
	 *            the horizontal direction of the shot.
	 * @param yDirection
	 *            the vertical direction of the shot.
	 * @param boardShotAt
	 *            the board to shoot at.
	 * @throws FieldOutOfBoardException
	 */
	private void fireShot(int startX, int startY, int xDirection, int yDirection, Board boardShotAt) throws FieldOutOfBoardException {
		int x;
		int y;
		for (int i = 0; i < this.shootingRange; i++) {
			x = startX + i * xDirection;
			y = startY + i * yDirection;
			// Schüsse ignorieren, die außerhalb des Feldes liegen
			if (isFieldWithinBoard(x, y, boardShotAt)) {
				Field fieldShotAt = boardShotAt.getField(x, y);
				// wenn Board schon beschossen wurde, dann Schuss ignorieren
				if (!fieldShotAt.isHit()) {
					// wenn das Feld auf das geschossen wurde ein Schiff hat,
					// dann ein Leben vom Schiff abziehen
					if (fieldShotAt.hasShip()) {
						Ship ship = fieldShotAt.getShip();
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

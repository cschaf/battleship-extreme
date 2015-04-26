package de.hsbremen.battleshipextreme.model.ship;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.ShipType;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.player.Player;

public abstract class Ship {
	protected int size;
	protected int shootingRange;
	protected int maxReloadTime;
	protected int currentReloadTime;
	protected ShipType type;
	protected boolean isPlaced;
	
	public boolean shoot(Player player, int startX, int startY, Orientation orientation) throws FieldOutOfBoardException {
		int xDirection = orientation == Orientation.Horizontal ? 1 : 0;
		int yDirection = orientation == Orientation.Vertical ? 1 : 0 ;	
		Board boardShotAt = player.getBoard();			
		if (isShotPossible(startX, startY, xDirection, yDirection, boardShotAt)) {
			fireShot(startX, startY, xDirection, yDirection, boardShotAt);
			return true;
		}
		return false;		
	}
	
	private boolean isShotPossible(int startX, int startY, int xDirection, int yDirection, Board boardShotAt) {
		int x;
		int y;
		for (int i = 0; i < this.shootingRange; i ++) {
			x = startX + i * xDirection;
			y = startY + i * yDirection;	
			if ((x >= boardShotAt.getSize()) || (y >= boardShotAt.getSize()) || (x < 0) || (y < 0)) {
				return false;
			}
		}
		return true;
	}
	
	private void fireShot(int startX, int startY, int xDirection, int yDirection, Board boardShotAt) throws FieldOutOfBoardException {
		for (int i = 0; i < this.shootingRange; i++) {
			Field fieldShotAt = boardShotAt.getField(startX + i * xDirection, startY + i * yDirection);
			if (!fieldShotAt.isHit()) {
				if (fieldShotAt.hasShip()) {
					Ship ship =	fieldShotAt.getShip();
					ship.setSize(ship.getSize()-1);
				}
				fieldShotAt.setHit(true);
			}			
			this.currentReloadTime = this.maxReloadTime;
		}
	}
		
	public void decreaseCurrentReloadTime() {
		if (this.currentReloadTime > 0)	this.currentReloadTime --;
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

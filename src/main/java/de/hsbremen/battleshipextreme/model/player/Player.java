package de.hsbremen.battleshipextreme.model.player;

import java.io.Serializable;
import java.util.Arrays;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.exception.FieldOccupiedException;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.exception.ShipAlreadyPlacedException;
import de.hsbremen.battleshipextreme.model.exception.ShipOutOfBoardException;
import de.hsbremen.battleshipextreme.model.ship.Corvette;
import de.hsbremen.battleshipextreme.model.ship.Destroyer;
import de.hsbremen.battleshipextreme.model.ship.Frigate;
import de.hsbremen.battleshipextreme.model.ship.Ship;
import de.hsbremen.battleshipextreme.model.ship.Submarine;

public class Player implements Serializable {
	protected static int currentId = 1;
	protected int id;
	protected String name;
	protected Board board;
	protected Ship[] ships;
	protected Ship selectedShip;

	public Player(int boardSize, int destroyers, int frigates, int corvettes, int submarines) {
		this.id = this.currentId++;
		this.name = "Player " + this.id;
		this.board = new Board(boardSize);
		this.ships = new Ship[destroyers + frigates + corvettes + submarines];

		for (int i = 0; i < ships.length; i++) {
			if (i < destroyers)
				ships[i] = new Destroyer();
			else if (i < destroyers + frigates)
				ships[i] = new Frigate();
			else if (i < destroyers + frigates + corvettes)
				ships[i] = new Corvette();
			else
				ships[i] = new Submarine();
		}
	}

	/**
	 * 
	 * @param ship
	 *            the ship to attack.
	 * @param xPos
	 *            the first x-coordinate of the field to place the ship on.
	 * @param yPos
	 *            the first y-coordinate of the field to place the ship on.
	 * @param orientation
	 *            the orientation of the ship (horizontal/vertical).
	 * @throws ShipAlreadyPlacedException
	 *             if the ship has already been placed.
	 * @throws FieldOutOfBoardException
	 *             if the players' board does not contain the field.
	 * @throws ShipOutOfBoardException
	 *             if the ships boundaries exceed the board.
	 * @throws FieldOccupiedException
	 *             if the ship or the ships' radius collides with another ship.
	 */
	public void placeShip(Ship ship, int xPos, int yPos, Orientation orientation) throws ShipAlreadyPlacedException, FieldOutOfBoardException, ShipOutOfBoardException, FieldOccupiedException {

		if (ship.isPlaced())
			throw new ShipAlreadyPlacedException(ship);

		if (!this.board.containsFieldAtPosition(xPos, yPos))
			throw new FieldOutOfBoardException(new Field(xPos, yPos));

		if (isShipPartiallyOutOfBoard(ship, xPos, yPos, orientation))
			throw new ShipOutOfBoardException(ship);

		Field field = findOccupiedField(ship, xPos, yPos, orientation);
		if (field != null)
			throw new FieldOccupiedException(field);

		placeShipOnBoard(ship, xPos, yPos, orientation);
	}

	private Field findOccupiedField(Ship ship, int xPos, int yPos, Orientation orientation) {
		Field[][] fields = this.board.getFields();
		// Orientation Horizontal
		if (orientation == Orientation.Horizontal) {

			// Felder prüfen ob bereits belegt
			for (int y = yPos - 1; y <= yPos + 1; y++)
				for (int x = xPos - 1; x <= xPos + ship.getSize(); x++)
					// x und y innerhalb des Spielfeldes
					if (x >= 0 && y >= 0 && x < fields.length && y < fields.length)
						if (fields[y][x].getShip() != null)
							return (fields[y][x]);
		}

		// Orientation Vertical
		if (orientation == Orientation.Vertical) {
			// Felder prüfen ob bereits belegt
			for (int y = yPos - 1; y <= yPos + ship.getSize(); y++)
				for (int x = xPos - 1; x <= xPos + 1; x++)
					// x und y innerhalb des Spielfeldes
					if (x >= 0 && y >= 0 && x < fields.length && y < fields.length)
						if (fields[y][x].getShip() != null) // Feld hat Schiff
							return (fields[y][x]);

		}
		return null;
	}

	private boolean isShipPartiallyOutOfBoard(Ship ship, int xPos, int yPos, Orientation orientation) {
		int xDirection = orientation == Orientation.Horizontal ? 1 : 0;
		int yDirection = orientation == Orientation.Vertical ? 1 : 0;
		int x = xPos + ship.getSize() * xDirection - 1;
		int y = yPos + ship.getSize() * yDirection - 1;
		return (x >= board.getSize()) || (y >= board.getSize());
	}

	private void placeShipOnBoard(Ship ship, int xPos, int yPos, Orientation orientation) throws FieldOccupiedException {
		int xDirection = orientation == Orientation.Horizontal ? 1 : 0;
		int yDirection = orientation == Orientation.Vertical ? 1 : 0;
		for (int i = 0; i < ship.getSize(); i++) {
			this.board.getFields()[yPos + i * yDirection][xPos + i * xDirection].setShip(ship);
		}
		ship.setPlaced();
	}

	public boolean hasPlacedAllShips() {
		boolean arePlaced = true;

		for (Ship ship : this.ships) {
			if (!ship.isPlaced()) {
				arePlaced = false;
				break;
			}
		}

		return arePlaced;
	}

	public boolean hasToSkip() {
		return (this.hasLost() || this.areAllShipsReloading());
	}

	private boolean doesPlayerPossessShip(Ship ship) {
		return Arrays.asList(this.getShips()).contains(ship);
	}

	/**
	 * Tries to set the selected ship of the player.
	 * 
	 * @param ship
	 *            the ship the player tries to select
	 * @return true if the ship was selected, false if not
	 */
	public boolean selectShip(Ship ship) {
		if (ship.canShipBeSelected() && (doesPlayerPossessShip(ship))) {
			this.selectedShip = ship;
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param player
	 *            the player to attack
	 * @param xPos
	 *            the x-coordinate the attacked players' board.
	 * @param yPos
	 *            the y-coordinate the attacked players' board.
	 * @param orientation
	 *            the orientation of the shot (vertical / horizontal).
	 * 
	 * @return true if the turn has been made, false if the turn was not
	 *         possible
	 * @throws Exception
	 *             if the player tries to attack himself or if the player is
	 *             already dead.
	 */
	public boolean makeTurn(Player player, int xPos, int yPos, Orientation orientation) throws Exception {
		boolean hasTurnBeenMade = true;
		if (this.equals(player)) {
			throw new Exception("The player can't attack himself!");
		}
		if (player.hasLost()) {
			throw new Exception("Player is already dead!");
		}
		Board board = player.getBoard();
		Field field = board.getField(xPos, yPos);
		hasTurnBeenMade = this.selectedShip.shoot(board, field, orientation);
		return hasTurnBeenMade;
	}

	public boolean areAllShipsReloading() {
		for (Ship ship : this.ships) {
			if (!ship.isReloading()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Decreases the reload time of the ships, except for the ship that just
	 * shot.
	 */
	public void decreaseCurrentReloadTimeOfShips() {
		for (Ship ship : this.ships) {
			if (!ship.equals(this.selectedShip))
				ship.decreaseCurrentReloadTime();
		}
		this.selectedShip = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return this.id;
	}

	public Board getBoard() {
		return board;
	}

	public Ship[] getShips() {
		return ships;
	}

	public static void resetCurrentId() {
		currentId = 1;
	}

	public String toString() {
		return this.name;
	}

	public boolean hasLost() {
		for (Ship ship : ships) {
			if (!ship.isDestroyed())
				return false;
		}
		return true;
	}
}
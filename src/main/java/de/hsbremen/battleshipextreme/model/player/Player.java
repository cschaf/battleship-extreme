package de.hsbremen.battleshipextreme.model.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.exception.ShipAlreadyPlacedException;
import de.hsbremen.battleshipextreme.model.exception.ShipOutOfBoardException;
import de.hsbremen.battleshipextreme.model.ship.Corvette;
import de.hsbremen.battleshipextreme.model.ship.Destroyer;
import de.hsbremen.battleshipextreme.model.ship.Frigate;
import de.hsbremen.battleshipextreme.model.ship.Ship;
import de.hsbremen.battleshipextreme.model.ship.ShipType;
import de.hsbremen.battleshipextreme.model.ship.Submarine;

public abstract class Player implements Serializable {
	protected String name;
	protected Ship[] ships;
	protected Ship currentShip;
	protected PlayerType type;
	protected Board board;

	public Player(int boardSize, int destroyers, int frigates, int corvettes, int submarines) {
		initShips(destroyers, frigates, corvettes, submarines);
		this.board = new Board(boardSize);
		this.currentShip = this.ships[0];
	}

	private void initShips(int destroyers, int frigates, int corvettes, int submarines) {
		ships = new Ship[destroyers + frigates + corvettes + submarines];
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

	public void resetBoard() {
		int size = board.getSize();
		board = new Board(size);
		for (Ship ship : ships) {
			ship.setPlaced(false);
		}
		currentShip = ships[0];
	}

	public FieldState[][] getFieldStates(boolean isOwnBoard) throws FieldOutOfBoardException {
		int size = board.getSize();
		FieldState[][] fieldStates = new FieldState[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				FieldState state = board.getField(j, i).getState();
				if ((state == FieldState.HAS_SHIP || state == FieldState.IS_EMPTY) && (!isOwnBoard)) {
					fieldStates[i][j] = null;
				} else {
					fieldStates[i][j] = state;
				}
			}
		}
		return fieldStates;
	}

	/**
	 * Check if its possible to place the ship. If not, throw exception. If yes,
	 * place the ship and call the nextShip-method.
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
	 * @return false if the field is already occupied, true if the ship could be
	 *         placed.
	 */
	public boolean placeShip(int xPos, int yPos, Orientation orientation) throws ShipAlreadyPlacedException, FieldOutOfBoardException, ShipOutOfBoardException {

		if (this.currentShip.isPlaced())
			throw new ShipAlreadyPlacedException(this.currentShip);

		if (!this.board.containsFieldAtPosition(xPos, yPos))
			throw new FieldOutOfBoardException(new Field(xPos, yPos));

		if (isShipPartiallyOutOfBoard(this.currentShip, xPos, yPos, orientation))
			throw new ShipOutOfBoardException(this.currentShip);

		Field occupiedField = findOccupiedField(this.currentShip, xPos, yPos, orientation);
		if (occupiedField != null)
			return false;

		placeShipOnBoard(this.currentShip, xPos, yPos, orientation);
		return true;
	}

	private Field findOccupiedField(Ship ship, int xPos, int yPos, Orientation orientation) {
		Field[][] fields = this.board.getFields();
		// Orientation Horizontal
		if (orientation == Orientation.HORIZONTAL) {

			// Felder prüfen ob bereits belegt
			for (int y = yPos - 1; y <= yPos + 1; y++)
				for (int x = xPos - 1; x <= xPos + ship.getSize(); x++)
					// x und y innerhalb des Spielfeldes
					if (x >= 0 && y >= 0 && x < fields.length && y < fields.length)
						if (fields[y][x].getShip() != null)
							return (fields[y][x]);
		}

		// Orientation Vertical
		if (orientation == Orientation.VERTICAL) {
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
		int xDirection = orientation == Orientation.HORIZONTAL ? 1 : 0;
		int yDirection = orientation == Orientation.VERTICAL ? 1 : 0;
		int x = xPos + ship.getSize() * xDirection - 1;
		int y = yPos + ship.getSize() * yDirection - 1;
		return (x >= board.getSize()) || (y >= board.getSize());
	}

	private void placeShipOnBoard(Ship ship, int xPos, int yPos, Orientation orientation) {
		int xDirection = orientation == Orientation.HORIZONTAL ? 1 : 0;
		int yDirection = orientation == Orientation.VERTICAL ? 1 : 0;
		for (int i = 0; i < ship.getSize(); i++) {
			board.getFields()[yPos + i * yDirection][xPos + i * xDirection].setShip(ship);
		}
		ship.place();
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

	private boolean possessesShip(Ship ship) {
		return Arrays.asList(this.getShips()).contains(ship);
	}

	/**
	 * Set the currentShip to next ship in array of ships. Its purpose is to
	 * keep track of the ship to place.
	 */
	public void nextShip() {
		int currentShipIndex = Arrays.asList(this.ships).indexOf(this.currentShip);
		if (currentShipIndex < ships.length - 1) {
			currentShipIndex++;
			currentShip = ships[currentShipIndex];
		}
	}

	/**
	 * Provides a way to retrieve all ships that are not destroyed.
	 * 
	 * @return a list of all ships that are not destroyed.
	 */
	public ArrayList<Ship> getAvailableShips(boolean excludeReloadingShips) {
		ArrayList<Ship> availableShips = new ArrayList<Ship>();
		for (Ship ship : ships) {
			if (!ship.isDestroyed()) {
				if (excludeReloadingShips) {
					if (!ship.isReloading()) {
						availableShips.add(ship);
					}
				} else {
					availableShips.add(ship);
				}
			}
		}
		return availableShips;
	}

	/**
	 * Tries to set the current ship of the player.
	 * 
	 * @param ship
	 *            the ship the player tries to select
	 * @return true if the ship was selected, false if not
	 * @throws Exception
	 */
	public void selectShip(Ship ship) throws Exception {
		if (!possessesShip(ship)) {
			throw new Exception("Player does not possess ship!");
		}
		this.currentShip = ship;
	}

	public boolean markBoard(int x, int y) throws FieldOutOfBoardException {
		// Schüsse ignorieren, die außerhalb des Feldes liegen
		if (board.containsFieldAtPosition(x, y)) {
			Field fieldShotAt = board.getField(x, y);
			// wenn Board schon beschossen wurde, dann Schuss ignorieren
			if (!fieldShotAt.isHit()) {
				board.getField(x, y).mark();
				// wenn das Feld auf das geschossen wurde ein Schiff hat,
				// dann ein Leben vom Schiff abziehen
				if (fieldShotAt.hasShip()) {
					Ship ship = fieldShotAt.getShip();
					ship.decreaseSize();
				}
			} else {
				// Feld bereits beschossen
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if all ships (that are not destroyed) are reloading.
	 * 
	 * @return true if all available ships are reloading, else false.
	 */
	public boolean areAllShipsReloading() {
		ArrayList<Ship> availableShips = this.getAvailableShips(true);
		return availableShips.size() <= 0;
	}

	public Ship getCurrentShip() {
		return this.currentShip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Ship[] getShips() {
		return ships;
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

	public PlayerType getType() {
		return type;
	}

	public void setType(PlayerType type) {
		this.type = type;
	}

	public int getShipCount(ShipType shipType) {
		int numberOfOccurences = 0;
		for (Ship ship : ships) {
			if (ship.getType() == shipType) {
				if (!ship.isDestroyed() && ship.isPlaced()) {
					numberOfOccurences++;
				}
			}
		}
		return numberOfOccurences;
	}

	public boolean setCurrentShipByType(ShipType shipType) {
		ArrayList<Ship> availableShips = getAvailableShips(true);
		for (Ship ship : availableShips) {
			if (ship.getType() == shipType) {
				currentShip = ship;
				return true;
			}
		}
		return false;
	}

	public boolean isShipOfTypeAvailable(ShipType shipType) {
		for (Ship ship : ships) {
			if (ship.getType() == shipType) {
				return true;
			}
		}
		return false;
	}

}
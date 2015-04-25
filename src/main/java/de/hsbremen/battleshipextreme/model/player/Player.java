package de.hsbremen.battleshipextreme.model.player;

import java.util.HashMap;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.FieldOccupiedException;
import de.hsbremen.battleshipextreme.model.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.ShipAlreadyPlacedException;
import de.hsbremen.battleshipextreme.model.ShipOutOfBoardException;
import de.hsbremen.battleshipextreme.model.ship.Corvette;
import de.hsbremen.battleshipextreme.model.ship.Destroyer;
import de.hsbremen.battleshipextreme.model.ship.Frigate;
import de.hsbremen.battleshipextreme.model.ship.Ship;
import de.hsbremen.battleshipextreme.model.ship.Submarine;

public class Player {
	private static int currentId = 1;
	private int id;
	private String name;
	private Board board;
	private Ship[] ships;

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

	public void placeShip(Ship ship, int xPos, int yPos, Orientation orientation) throws Exception, ShipAlreadyPlacedException, FieldOutOfBoardException {
		
		Field[][] fields = this.board.getFields();
		
		// Schiff bereits gesetzt
		if (ship.isPlaced())
			throw new ShipAlreadyPlacedException(ship);

		// Feld au�erhalb des Spielfeldes
		if (!(xPos >= 0 && yPos >= 0 && xPos < fields.length && yPos < fields.length)) 
			throw new FieldOutOfBoardException(new Field(xPos, yPos));
		
		// Orientation Horizontal
		if (orientation == Orientation.Horizontal) {
			
			// Teil des Schiffes au�erhalb des Spielfeldes
			if (!(xPos + ship.getSize() - 1 < fields.length))
				throw new ShipOutOfBoardException(ship); // Schiff au�erhalb Exception

			// Felder pr�fen ob bereits belegt
			for (int y = yPos - 1; y <= yPos + 1; y++)
				for (int x = xPos - 1; x <= xPos + ship.getSize(); x++)
					if (x >= 0 && y >= 0 && x < fields.length && y < fields.length) // x und y innerhalb des Spielfeldes
						if (fields[y][x].getShip() != null) // Feld hat Schiff
							throw new FieldOccupiedException(fields[y][x]); // Feld belegt Exception
			
			for (int x = xPos; x < xPos + ship.getSize(); x++)
				fields[yPos][x].setShip(ship);
		}
		
		// Orientation Vertical
		if (orientation == Orientation.Vertical) {
			
			// Teil des Schiffes au�erhalb des Spielfeldes
			if (!(yPos + ship.getSize() - 1 < fields.length))
				throw new ShipOutOfBoardException(ship); // Schiff au�erhalb Exception

			// Felder pr�fen ob bereits belegt
			for (int y = yPos - 1; y <= yPos + ship.getSize(); y++)
				for (int x = xPos - 1; x <= xPos + 1; x++)
					if (x >= 0 && y >= 0 && x < fields.length && y < fields.length) // x und y innerhalb des Spielfeldes
						if (fields[y][x].getShip() != null) // Feld hat Schiff
							throw new FieldOccupiedException(fields[y][x]); // Feld belegt Exception
			
			for (int y = xPos; y < yPos + ship.getSize(); y++)
				fields[y][xPos].setShip(ship);
		}
		
		ship.setPlaced();
	}

	public boolean hasPlacedAllShips() {
		boolean arePlaced = true;

		for (Ship ship : this.ships) {
			if (ship.isPlaced()) {
				arePlaced = false;
				break;
			}
		}

		return arePlaced;
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

}
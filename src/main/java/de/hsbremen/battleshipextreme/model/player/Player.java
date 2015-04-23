package de.hsbremen.battleshipextreme.model.player;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.FieldOccupiedException;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.ShipAlreadyPlacedException;
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
	
	public void placeShip(Ship ship, Orientation orientation, Field field) throws Exception, ShipAlreadyPlacedException {
		
//		if (ship.isPlaced()) {
//			// Schiff bereits gesetzt Exception
//			throw new ShipAlreadyPlacedException(ship);
//		}
		
		Field[][] fields = this.board.getFields();
		int x = field.getXPos();
		int y = field.getYPos();
		
		switch (orientation) {
		case Horizontal:
			
			break;
		case Vertical:
			break;
		default:
			break;
		}
		
		if (orientation == Orientation.Horizontal) {

			for (int row = y - 1; row <= y + 1; row++) { // Zeile (y)
				for (int column = x - 1; column <= x + ship.getSize(); column++){ // Spalte (x)
					if (((row < 0 || row >= fields.length) && row != y) || ((column < 0 || column >= fields.length) && column != x)) {
						// Feld auﬂerhalb des Spielfeldes Exception
					} else if (fields[row][column].getShip() != null) {
						// Feld hat bereits ein Schiff Exception
						throw new FieldOccupiedException(field);
					}
				}
			}
			
			for (int column = x; column < x + ship.getSize(); column++)
				fields[y][column].setShip(ship);
			
		} else if (orientation == Orientation.Vertical) {
			
			for (int row = y - 1; row <= y + ship.getSize(); row++) { // Zeile (y)
				for (int column = x - 1; column <= x + 1; column++){ // Spalte (x)
					if (row < 0 || column < 0 || row >= fields.length || column >= fields.length) {
						// Feld auﬂerhalb des Spielfeldes Exception
					} else if (fields[row][column].getShip() != null) {
						// Feld hat bereits ein Schiff Exception
						throw new FieldOccupiedException(field);
					}
				}
			}
			
			for (int row = y; row < y + ship.getSize(); row++)
				fields[row][x].setShip(ship);
			
		}
	}
	
	private boolean isPositionOK(Orientation orientation) {
		boolean isOK = true;
		
		
		
		return isOK;
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

package de.hsbremen.battleshipextreme.model.player;

import java.util.Arrays;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.exception.FieldOccupiedException;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.exception.ShipAlreadyPlacedException;
import de.hsbremen.battleshipextreme.model.exception.ShipOutOfBoardException;
import de.hsbremen.battleshipextreme.model.ship.*;
import de.hsbremen.battleshipextreme.network.TransferableType;
import de.hsbremen.battleshipextreme.network.transfarableObject.TransferableObject;

public class Player extends TransferableObject {
    protected static int currentId = 1;
    protected int id;
    protected String name;
    protected Board board;
    protected Ship[] ships;

    public Player(int boardSize, int destroyers, int frigates, int corvettes, int submarines) {
        this.id = this.currentId++;
        this.name = "Player " + this.id;
        this.board = new Board(boardSize);
        this.ships = new Ship[destroyers + frigates + corvettes + submarines];

        for (int i = 0; i < ships.length; i++) {
            if (i < destroyers) {
                ships[i] = new Destroyer();
            } else if (i < destroyers + frigates) {
                ships[i] = new Frigate();
            } else if (i < destroyers + frigates + corvettes) {
                ships[i] = new Corvette();
            } else {
                ships[i] = new Submarine();
            }
        }
    }

    public static void resetCurrentId() {
        currentId = 1;
    }

    public void placeShip(Ship ship, int xPos, int yPos, Orientation orientation) throws Exception, ShipAlreadyPlacedException, FieldOutOfBoardException {

        Field[][] fields = this.board.getFields();

        // Schiff bereits gesetzt
        if (ship.isPlaced()) {
            throw new ShipAlreadyPlacedException(ship);
        }

        // Feld au�erhalb des Spielfeldes
        if (!(xPos >= 0 && yPos >= 0 && xPos < fields.length && yPos < fields.length)) {
            throw new FieldOutOfBoardException(new Field(xPos, yPos));
        }

		for (Ship ship : this.ships) {
			if (!ship.isPlaced()) {
				arePlaced = false;
				break;
			}
		}
		
		return arePlaced;
	}
	
	public boolean shoot(Ship ship, Player player, int xPos, int yPos, Orientation orientation) throws Exception {
		//besitzt der Player das �bergebene Schiff?
		if (!Arrays.asList(this.getShips()).contains(ship)) {
			throw new Exception("The player does not possess the ship that has been handed over!");
		}
		//greift der Player sich selbst an?
		if (this.equals(player)) {
			throw new Exception("The player can't attack himself!");
		}
		return ship.shoot(player, xPos, yPos, orientation);	

	}
	
	public boolean AreAllShipsReloading() {
		for (Ship ship : this.ships) {
			if (!ship.isReloading()) {
				return false;
			}
		}
		return true;		
	}

            // Teil des Schiffes au�erhalb des Spielfeldes
            if (!(xPos + ship.getSize() - 1 < fields.length)) {
                throw new ShipOutOfBoardException(ship); // Schiff au�erhalb Exception
            }

            // Felder pr�fen ob bereits belegt
            for (int y = yPos - 1; y <= yPos + 1; y++)
                for (int x = xPos - 1; x <= xPos + ship.getSize(); x++)
                    if (x >= 0 && y >= 0 && x < fields.length && y < fields.length) // x und y innerhalb des Spielfeldes
                    {
                        if (fields[y][x].getShip() != null) // Feld hat Schiff
                        {
                            throw new FieldOccupiedException(fields[y][x]); // Feld belegt Exception
                        }
                    }

            for (int x = xPos; x < xPos + ship.getSize(); x++)
                fields[yPos][x].setShip(ship);
        }

        // Orientation Vertical
        if (orientation == Orientation.Vertical) {

            // Teil des Schiffes au�erhalb des Spielfeldes
            if (!(yPos + ship.getSize() - 1 < fields.length)) {
                throw new ShipOutOfBoardException(ship); // Schiff au�erhalb Exception
            }

            // Felder pr�fen ob bereits belegt
            for (int y = yPos - 1; y <= yPos + ship.getSize(); y++)
                for (int x = xPos - 1; x <= xPos + 1; x++)
                    if (x >= 0 && y >= 0 && x < fields.length && y < fields.length) // x und y innerhalb des Spielfeldes
                    {
                        if (fields[y][x].getShip() != null) // Feld hat Schiff
                        {
                            throw new FieldOccupiedException(fields[y][x]); // Feld belegt Exception
                        }
                    }

            for (int y = xPos; y < yPos + ship.getSize(); y++)
                fields[y][xPos].setShip(ship);
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

    public void makeTurn(Ship ship, Player player, int xPos, int yPos, Orientation orientation) {

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

    public String toString() {
        return this.name;
    }

    @Override
    public TransferableType getType() {
        return TransferableType.Player;
    }
}
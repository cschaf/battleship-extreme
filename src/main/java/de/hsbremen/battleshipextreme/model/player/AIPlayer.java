package de.hsbremen.battleshipextreme.model.player;

import java.util.ArrayList;
import java.util.Random;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.exception.ShipAlreadyPlacedException;
import de.hsbremen.battleshipextreme.model.exception.ShipOutOfBoardException;
import de.hsbremen.battleshipextreme.model.ship.Ship;

/**
 * This class provides methods that are used by all AIPlayers.
 *
 */

public class AIPlayer extends Player {

	protected Player currentEnemy;
	private static final int MAX_TRIES_TO_PLACE_SHIP = 1000;

	public AIPlayer(Board board, int destroyers, int frigates, int corvettes, int submarines) {
		super(board, destroyers, frigates, corvettes, submarines);
		this.type = PlayerType.AI;
	}

	public void placeShips() throws ShipAlreadyPlacedException, FieldOutOfBoardException, ShipOutOfBoardException {
		boolean isItPossibleToPlaceShip;
		int i = 0;
		do {
			int counter = 0;
			do {
				currentShip = ships[i];
				isItPossibleToPlaceShip = false;
				// zuf‰llige Position generieren
				Orientation orientation;
				orientation = (createRandomNumber(0, 1) == 0) ? Orientation.Horizontal : Orientation.Vertical;
				int xMax;
				int yMax;
				if (orientation == Orientation.Horizontal) {
					xMax = this.board.getSize() - this.getCurrentShip().getSize();
					yMax = this.board.getSize() - 1;
				} else {
					xMax = this.board.getSize() - 1;
					yMax = this.board.getSize() - this.getCurrentShip().getSize();

				}
				Field field = createRandomField(0, xMax, 0, yMax);
				counter++;
				isItPossibleToPlaceShip = placeShip(field.getXPos(), field.getYPos(), orientation);
			} while ((counter <= MAX_TRIES_TO_PLACE_SHIP) && (!isItPossibleToPlaceShip));

			if (counter >= MAX_TRIES_TO_PLACE_SHIP) {
				resetBoard();
				counter = 0;
				i = 0;
			} else {
				i++;
				nextShip();
			}
		} while (i < ships.length);
	}

	public void makeAiTurn(ArrayList<Player> availablePlayers) throws Exception {

	}

	protected Field createRandomField(int xMin, int xMax, int yMin, int yMax) {
		int xPos;
		int yPos;
		xPos = createRandomNumber(xMin, xMax);
		yPos = createRandomNumber(yMin, yMax);
		return new Field(xPos, yPos);
	}

	protected int createRandomNumber(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min + 1) + min;
	}

	protected void chooseShipToShootWithRandomly() {
		// zuf‰lliges freies Schiff zum Schieﬂen w‰hlen
		ArrayList<Ship> availableShips = this.getAvailableShipsToShoot();
		int randomShipIndex = createRandomNumber(0, availableShips.size() - 1);
		this.currentShip = availableShips.get(randomShipIndex);
	}

}

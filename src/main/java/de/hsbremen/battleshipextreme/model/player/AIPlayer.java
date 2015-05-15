package de.hsbremen.battleshipextreme.model.player;

import java.util.ArrayList;
import java.util.Random;

import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.ship.Ship;

/**
 * This class provides methods that are used by all AIPlayers.
 *
 */

public abstract class AIPlayer extends Player {

	protected Player currentEnemy;

	public AIPlayer(int boardSize, int destroyers, int frigates, int corvettes,
			int submarines) {
		super(boardSize, destroyers, frigates, corvettes, submarines);
		this.type = PlayerType.AI;
	}

	// TODO: keine Exception abfangen
	public void placeShips() {
		boolean isItPossibleToPlaceShip;
		for (int i = 0; i < this.ships.length; i++) {
			do {
				isItPossibleToPlaceShip = false;
				// zuf‰llige Position generieren
				Orientation orientation;
				orientation = (generateRandomNumber(0, 1) == 0) ? Orientation.Horizontal
						: Orientation.Vertical;
				Field field = generateField(orientation, this.getCurrentShip()
						.getSize());
				try {
					placeShip(field.getXPos(), field.getYPos(), orientation);
					isItPossibleToPlaceShip = true;
				} catch (Exception e) {
				}
			} while (!isItPossibleToPlaceShip);
			nextShip();
		}
	}

	public void makeAiTurn(ArrayList<Player> availablePlayers)
			throws Exception {
		Orientation orientation;
		boolean hasTurnBeenMade = false;

		chooseShipToShootWithRandomly();

		// Gegner zuf‰llig w‰hlen
		int randomEnemyIndex = generateRandomNumber(0,
				availablePlayers.size() - 1);
		this.currentEnemy = availablePlayers.get(randomEnemyIndex);

		// zuf‰llig schieﬂen
		do {
			orientation = (generateRandomNumber(0, 1) == 0) ? Orientation.Horizontal
					: Orientation.Vertical;
			Field field = generateField(orientation, this.currentShip.getSize());
			hasTurnBeenMade = makeTurn(this.currentEnemy, field.getXPos(),
					field.getYPos(), orientation);
		} while (!hasTurnBeenMade);

	}

	protected Field generateField(Orientation orientation, int shipSize) {
		int xPos;
		int yPos;
		xPos = generateRandomNumber(0, this.board.getSize() - 1);
		yPos = generateRandomNumber(0, this.board.getSize() - 1);
		return new Field(xPos, yPos);
	}

	protected int generateRandomNumber(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min + 1) + min;
	}

	public Player getCurrentEnemy() {
		return this.currentEnemy;
	}

	protected void chooseShipToShootWithRandomly() {
		// zuf‰lliges freies Schiff zum Schieﬂen w‰hlen
		ArrayList<Ship> availableShips = this.getAvailableShipsToShoot();
		int randomShipIndex = generateRandomNumber(0, availableShips.size() - 1);
		this.currentShip = availableShips.get(randomShipIndex);
	}

}

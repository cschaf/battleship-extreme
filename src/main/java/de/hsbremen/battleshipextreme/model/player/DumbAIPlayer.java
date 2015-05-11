package de.hsbremen.battleshipextreme.model.player;

import java.util.ArrayList;

import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.Orientation;

/**
 * Dumb AI - shoots randomly
 * 
 * AI-Benchmark: ~ 70 rounds
 *
 */

public class DumbAIPlayer extends AIPlayer {
	Field[] nextTargetsArray;

	public DumbAIPlayer(int boardSize, int destroyers, int frigates, int corvettes, int submarines) {
		super(boardSize, destroyers, frigates, corvettes, submarines);
		this.name = "Dumme KI" + currentId;
		this.type = PlayerType.AI;
	}

	public void placeShipsAutomatically() {
		boolean isItPossibleToPlaceShip;
		for (int i = 0; i < super.getShips().length; i++) {
			do {
				isItPossibleToPlaceShip = false;
				// zuf‰llige Position generieren
				Orientation orientation;
				orientation = (generateRandomNumber(0, 1) == 0) ? Orientation.Horizontal : Orientation.Vertical;
				Field field = generateField(orientation, this.getCurrentShip().getSize());
				try {
					super.placeShip(field.getXPos(), field.getYPos(), orientation);
					isItPossibleToPlaceShip = true;
				} catch (Exception e) {
				}
			} while (!isItPossibleToPlaceShip);
			super.nextShip();
		}
	}

	public void makeTurnAutomatically(ArrayList<Player> availablePlayers) throws Exception {
		Orientation orientation;
		boolean hasTurnBeenMade = false;

		chooseShipToShootWithRandomly();

		// Gegner zuf‰llig w‰hlen
		int randomEnemyIndex = generateRandomNumber(0, availablePlayers.size() - 1);
		this.currentEnemy = availablePlayers.get(randomEnemyIndex);

		// zuf‰llig schieﬂen
		do {
			orientation = (generateRandomNumber(0, 1) == 0) ? Orientation.Horizontal : Orientation.Vertical;
			Field field = generateField(orientation, this.currentShip.getSize());
			try {
				hasTurnBeenMade = super.makeTurn(this.currentEnemy, field.getXPos(), field.getYPos(), orientation);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (!hasTurnBeenMade);

	}

}

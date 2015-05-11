package de.hsbremen.battleshipextreme.model.player;

import java.util.ArrayList;
import java.util.Random;

import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.ship.Ship;

//AI-Benchmark: ~ 70 rounds

public class DumbAIPlayer extends Player {

	private Player currentEnemy;

	Field[] nextTargetsArray;

	public DumbAIPlayer(int boardSize, int destroyers, int frigates, int corvettes, int submarines) {
		super(boardSize, destroyers, frigates, corvettes, submarines);
		this.name = "AI" + currentId;
		this.type = PlayerType.AI;
	}

	public void placeShipsAutomatically() {
		boolean isItPossibleToPlaceShip;
		for (int i = 0; i < super.getShips().length; i++) {
			do {
				isItPossibleToPlaceShip = false;
				// zuf�llige Position generieren
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

		// Gegner zuf�llig w�hlen
		int randomEnemyIndex = generateRandomNumber(0, availablePlayers.size() - 1);
		this.currentEnemy = availablePlayers.get(randomEnemyIndex);

		// zuf�llig schie�en
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

	private void chooseShipToShootWithRandomly() {
		// zuf�lliges freies Schiff zum Schie�en w�hlen
		ArrayList<Ship> availableShips = this.getAvailableShips();
		int randomShipIndex = generateRandomNumber(0, availableShips.size() - 1);
		this.currentShip = this.getAvailableShips().get(randomShipIndex);
	}

	private Field generateField(Orientation orientation, int shipSize) {
		int xPos;
		int yPos;
		xPos = generateRandomNumber(0, this.board.getSize() - 1);
		yPos = generateRandomNumber(0, this.board.getSize() - 1);
		return new Field(xPos, yPos);
	}

	private int generateRandomNumber(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min + 1) + min;
	}

	public Player getCurrentEnemy() {
		return currentEnemy;
	}
}

package de.hsbremen.battleshipextreme.model.player;

import java.util.ArrayList;
import java.util.Random;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.ship.Ship;

public class AIPlayer extends Player {
	private Player currentEnemy;
	private Player nextEnemy;

	Field[] nextTargetsArray;

	public AIPlayer(int boardSize, int destroyers, int frigates, int corvettes, int submarines) {
		super(boardSize, destroyers, frigates, corvettes, submarines);
		this.name = "AI" + currentId;
		this.type = PlayerType.AI;
	}

	public void placeShipsAutomatically() {
		boolean isItPossibleToPlaceShip;
		for (int i = 0; i < super.getShips().length; i++) {
			do {
				isItPossibleToPlaceShip = false;
				// zufällige Position generieren
				Orientation orientation;
				orientation = (generateRandomNumber(0, 1) == 0) ? Orientation.Horizontal : Orientation.Vertical;
				Field field = generateField(orientation, super.getCurrentShip().getSize());
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
		int currentDirection;
		boolean hasTurnBeenMade = false;
		Field fieldShotAt = null;
		chooseShipToShootWithRandomly();

		if (!this.hasTargets()) {
			// wenn keine Ziele vorhanden sind
			// zufälligen Player zum angreifen auswählen
			// aktuellen Gegner nicht mehr merken
			this.nextEnemy = null;
			int randomEnemyIndex = generateRandomNumber(0, availablePlayers.size() - 1);
			this.currentEnemy = availablePlayers.get(randomEnemyIndex);

			// zufällig schießen
			do {
				orientation = (generateRandomNumber(0, 1) == 0) ? Orientation.Horizontal : Orientation.Vertical;
				fieldShotAt = generateField(orientation, this.currentShip.getSize());
				try {
					hasTurnBeenMade = super.makeTurn(this.currentEnemy, fieldShotAt.getXPos(), fieldShotAt.getYPos(), orientation);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (!hasTurnBeenMade);

			Field hitField = getFirstHitOfShot(fieldShotAt, orientation);
			// wenn Treffer, dann Gegner merken und nächste Schüsse planen
			if (hitField != null) {
				this.nextEnemy = this.currentEnemy;
				planNextShots(hitField);
			} else {
				// currentEnemy vergessen, wenn kein Feld getroffen
				// wurde
				this.nextEnemy = null;
			}
		} else {
			// wenn KI Spur verfolgt
			// den letzten Spieler ermitteln
			this.currentEnemy = this.nextEnemy;

			// Richtung ermitteln
			currentDirection = getCurrentDirection();
			System.out.println();
			switch (currentDirection) {
			case 0:
				System.out.println("NORDEN");
				break;
			case 1:
				System.out.println("OSTEN");
				break;
			case 2:
				System.out.println("SÜDEN");
				break;
			case 3:
				System.out.println("WESTEN");
				break;
			}

			// wenn Ziel vorhanden ist, dann auf Ziel schießen
			Field target = this.nextTargetsArray[currentDirection];

			// Ausrichtung beibehalten
			orientation = (generateRandomNumber(0, 1) == 0) ? Orientation.Horizontal : Orientation.Vertical;
			hasTurnBeenMade = super.makeTurn(this.currentEnemy, target.getXPos(), target.getYPos(), orientation);
			// wenn Treffer, dann nach nächstem unbeschossenen Feld in
			// selbe Richtung suchen und als nächstes Ziel speichern
			Field field = getFirstHitOfShot(target, orientation);
			if ((field != null) && (field.getState() != FieldState.Destroyed)) {
				int[] directionArray = determineDirection(currentDirection);
				int xDirection = directionArray[0];
				int yDirection = directionArray[1];
				Field newTarget = findNextFreeField(field, xDirection, yDirection);
				this.nextTargetsArray[currentDirection] = newTarget;
			} else {
				// wenn kein Treffer, dann Target löschen
				this.nextTargetsArray[currentDirection] = null;
			}
		}

	}

	private void chooseShipToShootWithRandomly() {
		// zufälliges freies Schiff zum Schießen wählen
		ArrayList<Ship> availableShips = this.getAvailableShips();
		int randomShipIndex = generateRandomNumber(0, availableShips.size() - 1);
		this.currentShip = this.getAvailableShips().get(randomShipIndex);
	}

	private boolean hasTargets() {
		// Ziele zum anvisieren übrig
		// lebt der anvisierte Gegner noch?
		boolean hasTargets;

		int i = 0;
		if (this.nextTargetsArray != null) {
			while ((i < 4) && (this.nextTargetsArray[i] == null)) {
				i++;
			}
		}
		hasTargets = (i < 4) && this.nextTargetsArray != null && this.nextEnemy != null && !this.nextEnemy.hasLost();
		return hasTargets;
	}

	private int getCurrentDirection() {
		int currentDirection = 0;
		while ((currentDirection < 4) && (this.nextTargetsArray[currentDirection] == null)) {
			// Finde nächstes Ziel
			currentDirection++;
		}
		return currentDirection;
	}

	private void planNextShots(Field hitField) throws FieldOutOfBoardException {

		this.nextTargetsArray = new Field[4];

		Field f;
		// finde Ziel nördlich vom Treffer
		f = findNextFreeField(hitField, 0, 1);
		if (f != null)
			this.nextTargetsArray[0] = f;

		// finde Ziel östlich vom Treffer
		f = findNextFreeField(hitField, 1, 0);
		if (f != null)
			this.nextTargetsArray[1] = f;

		// finde Ziel südlich vom Treffer
		f = findNextFreeField(hitField, 0, -1);
		if (f != null)
			this.nextTargetsArray[2] = f;

		// finde Ziel westlich vom Treffer
		f = findNextFreeField(hitField, -1, 0);
		if (f != null)
			this.nextTargetsArray[3] = f;
	}

	private int[] determineDirection(int direction) {
		// wenn Treffer, dann in die selbe Richtung
		// weitergehen
		switch (direction) {
		case 0:
			// Norden
			return new int[] { 0, 1 };
		case 1:
			// Osten
			return new int[] { -1, 0 };
		case 2:
			// Süden
			return new int[] { 0, -1 };
		case 3:
			// Westen
			return new int[] { 1, 0 };
		default:
			break;
		}
		return null;
	}

	private Field findNextFreeField(Field field, int xDirection, int yDirection) throws FieldOutOfBoardException {
		Board enemyBoard = this.currentEnemy.getBoard();
		int step = 0;
		int x;
		int y;
		boolean endLoop = false;
		Field f = null;
		do {
			x = field.getXPos() + step * xDirection;
			y = field.getYPos() + step * yDirection;
			if (enemyBoard.containsFieldAtPosition(x, y)) {
				f = enemyBoard.getField(x, y);
				if ((f.getState() == FieldState.Missed) || (f.getState() == FieldState.Destroyed)) {
					// wenn Schiff verfehlt oder zerstört wurde, Ziel nicht
					// merken
					f = null;
					endLoop = true;
				} else if (!f.isHit()) {
					// wenn Feld noch nicht beschossen wurde, Feld merken
					endLoop = true;
				}
				step++;
			} else {
				f = null;
				endLoop = true;
			}
		} while (!endLoop);
		return f;
	}

	private Field getFirstHitOfShot(Field field, Orientation orientation) throws FieldOutOfBoardException {
		int xDirection = orientation == Orientation.Horizontal ? 1 : 0;
		int yDirection = orientation == Orientation.Vertical ? 1 : 0;
		int x;
		int y;
		for (int i = 0; i < this.currentShip.getShootingRange(); i++) {
			x = field.getXPos() + i * xDirection;
			y = field.getYPos() + i * yDirection;
			if (this.currentEnemy.getBoard().containsFieldAtPosition(x, y)) {
				if (this.currentEnemy.getBoard().getField(x, y).hasShip()) {
					return new Field(x, y);
				}
			}
		}
		return null;
	}

	private Field generateField(Orientation orientation, int shipSize) {
		int xPos;
		int yPos;
		int xMax;
		int yMax;

		if (orientation == Orientation.Horizontal) {
			xMax = this.board.getSize() - shipSize;
			yMax = this.board.getSize() - 1;
		} else {
			xMax = this.board.getSize() - 1;
			yMax = this.board.getSize() - shipSize;
		}

		xPos = generateRandomNumber(0, xMax);
		yPos = generateRandomNumber(0, yMax);

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

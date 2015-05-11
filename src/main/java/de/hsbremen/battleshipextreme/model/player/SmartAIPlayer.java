package de.hsbremen.battleshipextreme.model.player;

import java.util.ArrayList;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;

/**
 * Smart AI - uses basic strategy
 * 
 * AI-Benchmark: ~ 50 rounds (70 rounds = random)
 *
 */
public class SmartAIPlayer extends AIPlayer {
	private Player nextEnemy;
	Field[] nextTargetsArray;

	public SmartAIPlayer(int boardSize, int destroyers, int frigates, int corvettes, int submarines) {
		super(boardSize, destroyers, frigates, corvettes, submarines);
		this.name = "Schlaue KI" + currentId;
	}

	@Override
	public void makeTurnAutomatically(ArrayList<Player> availablePlayers) throws Exception {
		Orientation orientation;
		int currentDirection;
		boolean hasTurnBeenMade = false;
		Field fieldShotAt = null;
		chooseShipToShootWithRandomly();

		if (!this.hasTargets()) {
			// wenn keine Ziele vorhanden sind
			// zuf�lligen Player zum angreifen ausw�hlen
			// aktuellen Gegner nicht mehr merken
			this.nextEnemy = null;
			int randomEnemyIndex = generateRandomNumber(0, availablePlayers.size() - 1);
			this.currentEnemy = availablePlayers.get(randomEnemyIndex);

			// zuf�llig schie�en
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
			// wenn Treffer, dann Gegner merken und n�chste Sch�sse planen
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

			// wenn Ziel vorhanden ist, dann auf Ziel schie�en
			Field target = this.nextTargetsArray[currentDirection];

			// Ausrichtung beibehalten
			orientation = (generateRandomNumber(0, 1) == 0) ? Orientation.Horizontal : Orientation.Vertical;
			hasTurnBeenMade = super.makeTurn(this.currentEnemy, target.getXPos(), target.getYPos(), orientation);
			// wenn Treffer, dann nach n�chstem unbeschossenen Feld in
			// selbe Richtung suchen und als n�chstes Ziel speichern
			if (target.hasShip()) {
				int[] directionArray = getDirectionArray(currentDirection);
				int xDirection = directionArray[0];
				int yDirection = directionArray[1];
				Field newTarget = findNextPotentialTarget(target, xDirection, yDirection);
				// neues Ziel in gleiche Richtung setzen
				this.nextTargetsArray[currentDirection] = newTarget;
			} else {
				// wenn kein Treffer, dann Target l�schen
				this.nextTargetsArray[currentDirection] = null;
			}
		}

	}

	private boolean hasTargets() {
		// Ziele zum anvisieren �brig?
		boolean hasTargets;
		int i = 0;
		if (this.nextTargetsArray != null) {
			while ((i < 4) && (this.nextTargetsArray[i] == null)) {
				i++;
			}
		}
		// Index innerhalb des targetArrays? Lebt der gemerkte Spieler noch?
		hasTargets = (i < 4) && this.nextTargetsArray != null && this.nextEnemy != null && !this.nextEnemy.hasLost();
		return hasTargets;
	}

	private int getCurrentDirection() {
		int currentDirection = 0;
		while ((currentDirection < 4) && (this.nextTargetsArray[currentDirection] == null)) {
			// Finde n�chstes Ziel
			currentDirection++;
		}
		return currentDirection;
	}

	private void planNextShots(Field hitField) throws FieldOutOfBoardException {
		// bekommt ein getroffenes Feld �bergeben und guckt in alle
		// Himmelsrichtungen nach potenziellen Zielen
		this.nextTargetsArray = new Field[4];
		Field target;
		int[] directions = new int[2];
		// Norden=0 Osten=1 S�den=2 Westen=3
		for (int i = 0; i < 4; i++) {
			directions = getDirectionArray(i);
			target = findNextPotentialTarget(hitField, directions[0], directions[1]);
			if (target != null)
				// wenn potenzielles Ziel gefunden, dann Feld merken
				this.nextTargetsArray[i] = target;
		}
	}

	private int[] getDirectionArray(int direction) {
		// wenn Treffer, dann in die selbe Richtung
		// weitergehen
		switch (direction) {
		case 0:
			// Norden
			return new int[] { 0, -1 };
		case 1:
			// Osten
			return new int[] { -1, 0 };
		case 2:
			// S�den
			return new int[] { 0, 1 };
		case 3:
			// Westen
			return new int[] { 1, 0 };
		default:
			break;
		}
		return null;
	}

	private Field findNextPotentialTarget(Field field, int xDirection, int yDirection) throws FieldOutOfBoardException {
		// sucht nach dem n�chsten Feld als potenzielles Ziel, ausgehend vom
		// �bergebenen Feld
		Board enemyBoard = this.currentEnemy.getBoard();
		int step = 0;
		int x;
		int y;
		boolean endLoop = false;
		Field target = null;
		do {
			x = field.getXPos() + step * xDirection;
			y = field.getYPos() + step * yDirection;
			if (enemyBoard.containsFieldAtPosition(x, y)) {
				target = enemyBoard.getField(x, y);
				if ((target.getState() == FieldState.Missed) || (target.getState() == FieldState.Destroyed)) {
					// wenn Schiff verfehlt oder zerst�rt wurde, Ziel nicht
					// merken
					target = null;
					endLoop = true;
				} else if (!target.isHit()) {
					// wenn Feld noch nicht beschossen wurde, Feld merken
					endLoop = true;
				}
				step++;
			} else {
				// Feld nicht mehr im Board
				target = null;
				endLoop = true;
			}
		} while (!endLoop);
		return target;
	}

	private Field getFirstHitOfShot(Field field, Orientation orientation) throws FieldOutOfBoardException {
		// pr�ft ob ein zuf�lliger Schuss (teilweise) getroffen hat
		// gibt den ersten Treffer zur�ck
		// gibt null zur�ck, wenn es keinen Treffer gab
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
}
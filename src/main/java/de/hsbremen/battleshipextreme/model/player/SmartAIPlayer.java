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
 * AI-Benchmark: ~ 46 rounds (70 rounds = random)
 *
 */
public class SmartAIPlayer extends AIPlayer {
	private Player nextEnemy;
	private Field[] nextTargetsArray;
	private Field currentFieldShotAt;
	private Orientation currentShotOrientation;

	private final int NORTH = 0;
	private final int SOUTH = 1;
	private final int EAST = 2;
	private final int WEST = 3;

	public SmartAIPlayer(int boardSize, int destroyers, int frigates, int corvettes, int submarines) {
		super(boardSize, destroyers, frigates, corvettes, submarines);
		this.name = "Schlaue KI" + currentId;
	}

	@Override
	public void makeTurnAutomatically(ArrayList<Player> availablePlayers) throws Exception {
		Orientation orientation;
		int currentDirection = 0;

		chooseShipToShootWithRandomly();

		if (!this.hasTargets()) {
			// wenn keine Ziele vorhanden sind
			// zufälligen Player zum angreifen auswählen
			// aktuellen Gegner nicht mehr merken
			this.nextEnemy = null;
			int randomEnemyIndex = generateRandomNumber(0, availablePlayers.size() - 1);
			this.currentEnemy = availablePlayers.get(randomEnemyIndex);

			// zufällig schießen, Schuss merken
			shootRandomly();

			// prüfen ob ein Feld getroffen wurde, wenn ja dann Feld merken
			Field hitField = getFirstHitOfShot(this.currentFieldShotAt, this.currentShotOrientation);

			// wenn Treffer, dann Gegner merken und nächsten Schüsse planen
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

			// wenn Ziel vorhanden ist, dann auf Ziel schießen
			Field target = this.nextTargetsArray[currentDirection];

			// wenn Richtung Osten oder Westen, dann Ausrichtung horizontal,
			// ansonsten vertikal
			orientation = (currentDirection == EAST || currentDirection == WEST) ? Orientation.Horizontal : Orientation.Vertical;

			// x und y abhängig von der Schussweite korrigieren, so dass
			// möglichst
			// viele Felder getroffen werden
			int range = this.currentShip.getShootingRange() - 1;
			int adjustedX = adjustX(target, currentDirection, range);
			int adjustedY = adjustY(target, currentDirection, range);

			// schießen
			makeTurn(this.currentEnemy, adjustedX, adjustedY, orientation);

			// wenn Treffer, dann nach nächstem unbeschossenen Feld in
			// selbe Richtung suchen und als nächstes Ziel speichern
			if (target.hasShip()) {
				// wenn Treffer
				if (target.getState() != FieldState.Destroyed) {
					int[] directionArray = getDirectionArray(currentDirection);
					Field newTarget = findNextTarget(target, directionArray[0], directionArray[1]);
					// neues Ziel in gleiche Richtung setzen
					this.nextTargetsArray[currentDirection] = newTarget;
				} else {
					// Schiff zerstört, komplettes targetArray löschen,
					// da die Spur nicht mehr verfolgt werden muss
					this.nextTargetsArray = null;
					this.nextEnemy = null;
				}
			} else {
				// wenn kein Treffer, dann Target löschen
				this.nextTargetsArray[currentDirection] = null;
			}
		}

	}

	private void shootRandomly() throws FieldOutOfBoardException {
		Orientation orientation;
		Field fieldShotAt;
		boolean hasTurnBeenMade = false;
		int boardSize = this.currentEnemy.getBoard().getSize();
		// zufällig schießen
		do {

			do {
				orientation = (generateRandomNumber(0, 1) == 0) ? Orientation.Horizontal : Orientation.Vertical;
				fieldShotAt = generateField(orientation, this.currentShip.getSize());
			} while (surroundingFieldContainsShip(fieldShotAt));

			// wenn möglich, den Schuss so ausrichten, dass alle
			// Schussfelder im Board sind
			int adjustedX = fieldShotAt.getXPos();
			int adjustedY = fieldShotAt.getYPos();
			// versuche x-Koordinate anzupassen
			if (fieldShotAt.getXPos() >= boardSize) {
				int overhang = fieldShotAt.getXPos() - boardSize;
				adjustedX = adjustX(fieldShotAt, WEST, overhang);
			}
			// versuche y-Koordinate anzupassen
			if (fieldShotAt.getYPos() >= boardSize) {
				int overhang = fieldShotAt.getXPos() - boardSize;
				adjustedY = adjustY(fieldShotAt, NORTH, overhang);
			}

			try {
				hasTurnBeenMade = makeTurn(this.currentEnemy, adjustedX, adjustedY, orientation);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (!hasTurnBeenMade);

		// Feld und Ausrichtung merken
		// TODO: Shot-Objekt?
		this.currentFieldShotAt = fieldShotAt;
		this.currentShotOrientation = orientation;

	}

	private boolean surroundingFieldContainsShip(Field fieldShotAt) throws FieldOutOfBoardException {
		Board enemyBoard = this.currentEnemy.getBoard();
		int[] directions = new int[2];
		int x;
		int y;
		for (int i = 0; i < 4; i++) {
			directions = getDirectionArray(i);
			x = fieldShotAt.getXPos() + directions[0];
			y = fieldShotAt.getYPos() + directions[1];
			if (enemyBoard.containsFieldAtPosition(x, y)) {
				FieldState actualFieldState = enemyBoard.getField(x, y).getState();
				if (actualFieldState == FieldState.Destroyed) {
					return true;
				}
			}
		}
		return false;
	}

	private int adjustX(Field target, int currentDirection, int range) throws FieldOutOfBoardException {
		// wenn Richtung Westen , dann gehe Schussweite nach links um mehr
		// Felder zu treffen
		Board enemyBoard = this.currentEnemy.getBoard();
		int adjustedX = target.getXPos();
		int targetYPos = target.getYPos();

		if (currentDirection == WEST) {
			for (int i = 0; i < range; i++) {
				if (enemyBoard.containsFieldAtPosition(adjustedX - 1, targetYPos)) {
					if (!enemyBoard.getField(adjustedX - 1, target.getYPos()).isHit()) {
						adjustedX -= 1;

					} else
						break;
				}
			}
		}
		return adjustedX;
	}

	private int adjustY(Field target, int currentDirection, int range) throws FieldOutOfBoardException {
		// wenn Richtung Norden, um Schussweite hoch gehen, um mehr Felder zu
		// treffen
		Board enemyBoard = this.currentEnemy.getBoard();
		int adjustedY = target.getYPos();
		int targetXPos = target.getXPos();
		if (currentDirection == NORTH) {
			for (int i = 0; i < range; i++) {
				if (enemyBoard.containsFieldAtPosition(targetXPos, adjustedY - 1)) {
					if (!enemyBoard.getField(targetXPos, adjustedY - 1).isHit()) {
						adjustedY -= 1;
					} else
						break;
				}
			}
		}
		return adjustedY;
	}

	private boolean hasTargets() {
		// Ziele zum anvisieren übrig?
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
		// ermittle aktuelle Himmelsrichtung
		int currentDirection = 0;
		while ((currentDirection < 4) && (this.nextTargetsArray[currentDirection] == null)) {
			currentDirection++;
		}
		return currentDirection;
	}

	private void planNextShots(Field hitField) throws FieldOutOfBoardException {
		// bekommt ein getroffenes Feld übergeben und guckt in alle
		// Himmelsrichtungen nach potenziellen Zielen
		this.nextTargetsArray = new Field[4];
		Field target;
		int[] directions = new int[2];
		for (int i = 0; i < 4; i++) {
			directions = getDirectionArray(i);
			target = findNextTarget(hitField, directions[0], directions[1]);
			if (target != null)
				// wenn potenzielles Ziel gefunden, dann Feld merken
				this.nextTargetsArray[i] = target;
		}
	}

	private int[] getDirectionArray(int direction) {
		// liefert ein Array mit x- und y-Richtung
		switch (direction) {
		case NORTH:
			return new int[] { 0, -1 };
		case SOUTH:
			return new int[] { 0, 1 };
		case EAST:
			return new int[] { 1, 0 };
		case WEST:
			return new int[] { -1, 0 };
		default:
			break;
		}
		return null;
	}

	private Field findNextTarget(Field field, int xDirection, int yDirection) throws FieldOutOfBoardException {
		// sucht nach dem nächsten Feld als potenzielles Ziel, ausgehend vom
		// übergebenen Feld
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
					// wenn Schiff verfehlt oder zerstört wurde, Ziel nicht
					// merken, Schleife abbrechen
					target = null;
					endLoop = true;
				} else if (!target.isHit()) {
					// wenn Feld noch nicht beschossen wurde, Schleife
					// abbrechen, Feld zurückgeben
					endLoop = true;
				}
			} else {
				// Feld nicht mehr im Board
				// Ziel nicht merken
				target = null;
				endLoop = true;
			}
			step++;
		} while (!endLoop);
		return target;
	}

	private Field getFirstHitOfShot(Field field, Orientation orientation) throws FieldOutOfBoardException {
		// prüft ob ein zufälliger Schuss (teilweise) getroffen hat
		// gibt den ersten gefundenen Treffer zurück
		// gibt null zurück, wenn es keinen Treffer gab
		int xDirection = orientation == Orientation.Horizontal ? 1 : 0;
		int yDirection = orientation == Orientation.Vertical ? 1 : 0;
		int x;
		int y;
		Field hitField = null;
		Board enemyBoard = this.currentEnemy.getBoard();
		for (int i = 0; i < this.currentShip.getShootingRange(); i++) {
			x = field.getXPos() + i * xDirection;
			y = field.getYPos() + i * yDirection;
			if (enemyBoard.containsFieldAtPosition(x, y)) {
				hitField = enemyBoard.getField(x, y);
				if (hitField.hasShip()) {
					return hitField;
				}
			}
		}
		return null;
	}
}

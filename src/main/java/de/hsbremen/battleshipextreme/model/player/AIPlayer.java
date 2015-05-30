package de.hsbremen.battleshipextreme.model.player;

import java.util.ArrayList;
import java.util.Random;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.exception.ShipAlreadyPlacedException;
import de.hsbremen.battleshipextreme.model.exception.ShipOutOfBoardException;
import de.hsbremen.battleshipextreme.model.ship.Submarine;

/**
 * This class provides methods that are used by all AIPlayers.
 *
 */

public class AIPlayer extends Player {

	private int currentEnemyIndex;
	private Board enemyBoardRepresentation;
	private Field[] nextTargetsArray;
	boolean attacksDirectionFirstTime;

	private static final int NORTH = 0;
	private static final int SOUTH = 1;
	private static final int EAST = 2;
	private static final int WEST = 3;
	private static final int NORTH_EAST = 4;
	private static final int SOUTH_EAST = 5;
	private static final int NORTH_WEST = 6;
	private static final int SOUTH_WEST = 7;

	private static final int MAX_TRIES_TO_PLACE_SHIP = 1000;

	public AIPlayer(int boardSize, int destroyers, int frigates, int corvettes, int submarines, PlayerType aiType) {
		super(boardSize, destroyers, frigates, corvettes, submarines);
		this.type = aiType;
		this.name = aiType.toString();
		attacksDirectionFirstTime = true;
	}

	public void placeShips() throws ShipAlreadyPlacedException, FieldOutOfBoardException, ShipOutOfBoardException {
		boolean isItPossibleToPlaceShip;
		int i = 0;
		do {
			int counter = 0;
			do {
				currentShip = ships[i];
				isItPossibleToPlaceShip = false;
				Target shot = getRandomShipPlacementTarget();
				counter++;
				isItPossibleToPlaceShip = placeShip(shot.getX(), shot.getY(), shot.getOrientation());
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

	public Target getTarget(FieldState[][] fieldStates) throws Exception {
		if (type == PlayerType.DUMB_AI)
			return getRandomShot();

		enemyBoardRepresentation = buildBoardRepresentation(fieldStates);
		if (hasTargets()) {
			return getNextTarget();
		} else {
			return getNewTarget();
		}
	}

	private Target getNewTarget() throws Exception {
		Target target;
		ArrayList<Field> hitFields = lookForHitFields();
		// wenn ein getroffenes Schiff gefunden wurde, dann plane die nächsten
		// Schüsse und greife das gefundene Ziel an
		if (hitFields.size() > 0) {
			planNextShots(hitFields);
			target = getNextTarget();
		} else {
			// wenn keine getroffenen Schiffe gefunden wurden
			// zufällig schießen, Schuss merken
			target = getRandomShot();
		}
		return target;
	}

	private Target getNextTarget() throws Exception {
		int currentDirection = getCurrentDirection();

		// wird eine Richtung zum ersten mal angegriffen?
		if (!attacksDirectionFirstTime) {
			// wenn nicht, dann prüfe, ob das letzte Ziel ein Treffer war oder
			// das Schiff bereits zerstört wurde
			Field lastFieldShotAt = enemyBoardRepresentation.getField(nextTargetsArray[currentDirection].getXPos(), nextTargetsArray[currentDirection].getYPos());
			if (lastFieldShotAt.getState() != FieldState.DESTROYED) {
				if (lastFieldShotAt.getState() == FieldState.HIT) {
					// wenn das letzte Ziel ein Treffer war, dann neues Ziel in
					// gleiche Richtung setzen
					int[] directionArray = getDirectionArray(currentDirection);
					Field newTarget = findNextTarget(lastFieldShotAt, directionArray[0], directionArray[1]);
					nextTargetsArray[currentDirection] = newTarget;

				} else {
					// wenn kein Treffer, dann Target löschen
					nextTargetsArray[currentDirection] = null;
					attacksDirectionFirstTime = true;
				}
			} else {
				// wenn Schiff zerstört wurde, dann neues Ziel suchen
				nextTargetsArray = null;
				attacksDirectionFirstTime = true;
				return getNewTarget();
			}

		}

		Target target;
		Field targetField;
		currentDirection = getCurrentDirection();
		// aktuelles Ziel ermitteln
		targetField = nextTargetsArray[currentDirection];
		// wenn Richtung Osten oder Westen, dann Ausrichtung horizontal,
		// ansonsten vertikal
		Orientation orientation = (currentDirection == EAST || currentDirection == WEST) ? Orientation.HORIZONTAL : Orientation.VERTICAL;

		// x und y abhängig von der Schussweite korrigieren, so dass
		// möglichst viele Felder getroffen werden
		int range = this.currentShip.getShootingRange() - 1;
		int adjustedX = adjustX(targetField, currentDirection, range);
		int adjustedY = adjustY(targetField, currentDirection, range);
		target = new Target(adjustedX, adjustedY, orientation);
		attacksDirectionFirstTime = false;

		return target;
	}

	private ArrayList<Field> lookForHitFields() throws FieldOutOfBoardException {
		ArrayList<Field> hitFields = new ArrayList<Field>();
		for (int i = 0; i < enemyBoardRepresentation.getSize(); i++) {
			for (int j = 0; j < enemyBoardRepresentation.getSize(); j++) {
				Field f = enemyBoardRepresentation.getField(j, i);
				if (f.getState() == FieldState.HIT) {
					hitFields.add(f);
				}
			}
		}
		return hitFields;
	}

	private Target getRandomShot() throws Exception {
		Orientation orientation;
		Field fieldShotAt;
		int boardSize = this.board.getSize();

		// zufällig schießen
		if (type == PlayerType.DUMB_AI) {
			orientation = (createRandomNumber(0, 1) == 0) ? Orientation.HORIZONTAL : Orientation.VERTICAL;
			fieldShotAt = createRandomField(0, boardSize - 1, 0, boardSize - 1);
			return new Target(fieldShotAt.getXPos(), fieldShotAt.getYPos(), orientation);
		}

		// wiederhole die Erzeugung von zufälligen Koordinaten
		// bis ein Feld gefunden wird, an welches kein zerstörtes
		// Schiff angrenzt,
		// (zwischen den Schiffen muss immer ein Feld frei sein)
		do {
			orientation = (createRandomNumber(0, 1) == 0) ? Orientation.HORIZONTAL : Orientation.VERTICAL;
			fieldShotAt = createRandomField(0, boardSize - 1, 0, boardSize - 1);
		} while (surroundingFieldContainsShip(fieldShotAt));

		// wenn möglich, den Schuss so ausrichten, dass alle
		// Schussfelder im Board sind
		int adjustedX = fieldShotAt.getXPos();
		int adjustedY = fieldShotAt.getYPos();
		// versuche x-Koordinate anzupassen
		if ((fieldShotAt.getXPos() + this.currentShip.getShootingRange() >= (boardSize)) && (orientation == Orientation.HORIZONTAL)) {
			int overhang = (fieldShotAt.getXPos() + this.currentShip.getShootingRange()) - (boardSize);
			System.out.println("o" + (overhang > 0));
			adjustedX = adjustX(fieldShotAt, WEST, overhang);
		}
		// versuche y-Koordinate anzupassen
		if ((fieldShotAt.getYPos() + this.currentShip.getShootingRange() >= (boardSize)) && (orientation == Orientation.VERTICAL)) {
			int overhang = (fieldShotAt.getYPos() + this.currentShip.getShootingRange()) - (boardSize);
			System.out.println("o" + (overhang > 0));
			adjustedY = adjustY(fieldShotAt, NORTH, overhang);
		}

		return new Target(adjustedX, adjustedY, orientation);
	}

	private boolean surroundingFieldContainsShip(Field fieldShotAt) throws FieldOutOfBoardException {
		// prüft ob ein umliegendes Feld schon ein zerstörtes Schiff beinhaltet
		int[] directions = new int[2];
		int x;
		int y;
		for (int i = 0; i < 8; i++) {
			directions = getDirectionArray(i);
			x = fieldShotAt.getXPos() + directions[0];
			y = fieldShotAt.getYPos() + directions[1];
			if (enemyBoardRepresentation.containsFieldAtPosition(x, y)) {
				FieldState actualFieldState = enemyBoardRepresentation.getField(x, y).getState();
				if (actualFieldState == FieldState.DESTROYED) {
					return true;
				}
			}
		}

		return false;
	}

	private int adjustX(Field target, int currentDirection, int range) throws FieldOutOfBoardException {
		// wenn Richtung Westen, dann gehe Schussweite nach links um mehr
		// Felder zu treffen
		int adjustedX = target.getXPos();
		int targetYPos = target.getYPos();

		if (currentDirection == WEST) {
			for (int i = 0; i < range; i++) {
				if (enemyBoardRepresentation.containsFieldAtPosition(adjustedX - 1, targetYPos)) {
					if (!enemyBoardRepresentation.getField(adjustedX - 1, target.getYPos()).isHit()) {
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
		int adjustedY = target.getYPos();
		int targetXPos = target.getXPos();
		if (currentDirection == NORTH) {
			for (int i = 0; i < range; i++) {
				if (enemyBoardRepresentation.containsFieldAtPosition(targetXPos, adjustedY - 1)) {
					if (!enemyBoardRepresentation.getField(targetXPos, adjustedY - 1).isHit()) {
						adjustedY -= 1;
					} else
						break;
				}
			}
		}
		return adjustedY;
	}

	public boolean hasTargets() {
		// Ziele zum anvisieren übrig?
		boolean hasTargets;
		int i = 0;
		if (this.nextTargetsArray != null) {
			while ((i < 4) && (this.nextTargetsArray[i] == null)) {
				i++;
			}
		}
		hasTargets = (i < 4) && this.nextTargetsArray != null;
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

	private void planNextShots(ArrayList<Field> hitFields) throws FieldOutOfBoardException {
		boolean isHorizontalHit = false;
		boolean isVerticalHit = false;

		// wenn mehrere Felder getroffen wurden, gucken ob die Schiffausrichtung
		// horizontal oder vertikal ist
		if (hitFields.size() > 1) {

			// liegen die Felder horizontal aneinander?
			isHorizontalHit = hitFields.get(0).getYPos() == hitFields.get(1).getYPos() && ((Math.abs(hitFields.get(0).getXPos() - hitFields.get(1).getXPos()) == 1));
			// liegen die Felder vertikal aneinander
			isVerticalHit = hitFields.get(0).getXPos() == hitFields.get(1).getXPos() && ((Math.abs(hitFields.get(0).getYPos() - hitFields.get(1).getYPos()) == 1));
		}
		// bekommt ein getroffenes Feld übergeben und guckt in alle
		// Himmelsrichtungen nach potenziellen Zielen
		nextTargetsArray = new Field[4];
		Field target = null;
		int[] directions = new int[2];
		for (int i = 0; i < 4; i++) {
			directions = getDirectionArray(i);
			if (!isHorizontalHit && !isVerticalHit) {
				target = findNextTarget(hitFields.get(0), directions[0], directions[1]);
			}
			if (isHorizontalHit && (i == EAST || i == WEST)) {
				target = findNextTarget(hitFields.get(0), directions[0], directions[1]);
			}
			if (isVerticalHit && (i == NORTH || i == SOUTH)) {
				target = findNextTarget(hitFields.get(0), directions[0], directions[1]);
			}
			// wenn potenzielles Ziel gefunden, dann Feld merken
			nextTargetsArray[i] = target;
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
		case NORTH_EAST:
			return new int[] { 1, -1 };
		case SOUTH_EAST:
			return new int[] { 1, 1 };
		case NORTH_WEST:
			return new int[] { -1, 1 };
		case SOUTH_WEST:
			return new int[] { -1, -1 };
		default:
			break;
		}
		return null;
	}

	private Field findNextTarget(Field field, int xDirection, int yDirection) throws FieldOutOfBoardException {
		// sucht nach dem nächsten Feld als potenzielles Ziel, ausgehend vom
		// übergebenen Feld
		int step = 0;
		int x;
		int y;
		boolean endLoop = false;
		Field target = null;
		do {
			x = field.getXPos() + step * xDirection;
			y = field.getYPos() + step * yDirection;
			if (enemyBoardRepresentation.containsFieldAtPosition(x, y)) {
				target = enemyBoardRepresentation.getField(x, y);
				if ((target.getState() == FieldState.MISSED) || (target.getState() == FieldState.DESTROYED)) {
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

	private Target getRandomShipPlacementTarget() {
		// zufällige Position generieren
		Orientation orientation;
		orientation = (createRandomNumber(0, 1) == 0) ? Orientation.HORIZONTAL : Orientation.VERTICAL;
		int xMax;
		int yMax;
		if (orientation == Orientation.HORIZONTAL) {
			xMax = this.board.getSize() - this.getCurrentShip().getSize();
			yMax = this.board.getSize() - 1;
		} else {
			xMax = this.board.getSize() - 1;
			yMax = this.board.getSize() - this.getCurrentShip().getSize();

		}
		int xPos = createRandomNumber(0, xMax);
		int yPos = createRandomNumber(0, yMax);
		return new Target(xPos, yPos, orientation);
	}

	private Field createRandomField(int xMin, int xMax, int yMin, int yMax) {
		int xPos;
		int yPos;
		xPos = createRandomNumber(xMin, xMax);
		yPos = createRandomNumber(yMin, yMax);
		return new Field(xPos, yPos);
	}

	private int createRandomNumber(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min + 1) + min;
	}

	// baut anhand der bekannten Fieldstates eine Nachbildung des Boards
	private Board buildBoardRepresentation(FieldState[][] fieldStates) throws FieldOutOfBoardException {
		enemyBoardRepresentation = new Board(fieldStates.length);
		for (int i = 0; i < fieldStates.length; i++) {
			for (int j = 0; j < fieldStates[i].length; j++) {
				FieldState state = fieldStates[i][j];
				if (state != null) {
					Field f = enemyBoardRepresentation.getField(j, i);
					switch (state) {
					case DESTROYED:
						Submarine submarine = new Submarine();
						submarine.decreaseSize();
						submarine.decreaseSize();
						f.setShip(submarine);
						f.mark();
						break;
					case MISSED:
						f.mark();
						break;
					case HIT:
						f.mark();
						f.setShip(new Submarine());
						break;
					default:
						break;
					}
				}
			}
		}
		return enemyBoardRepresentation;
	}

	public int getCurrentEnemyIndex() {
		return currentEnemyIndex;
	}

	public void setCurrentEnemyIndex(int currentEnemyIndex) {
		this.currentEnemyIndex = currentEnemyIndex;
	}

	public void setRandomEnemyIndex(int max) {
		this.currentEnemyIndex = createRandomNumber(0, max);
	}

}

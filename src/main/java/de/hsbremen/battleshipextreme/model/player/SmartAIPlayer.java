package de.hsbremen.battleshipextreme.model.player;

import java.util.ArrayList;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.ship.Submarine;

public class SmartAIPlayer extends AIPlayer {
	private int currentEnemyIndex;
	private Board enemyBoardRepresentation;

	// enthält 4 Felder für jede Himmelsrichtung
	private Field[] nextTargetsArray;

	private Shot lastShot;

	private final int NORTH = 0;
	private final int SOUTH = 1;
	private final int EAST = 2;
	private final int WEST = 3;
	private final int NORTH_EAST = 4;
	private final int SOUTH_EAST = 5;
	private final int NORTH_WEST = 6;
	private final int SOUTH_WEST = 7;

	public SmartAIPlayer(Board board, int destroyers, int frigates, int corvettes, int submarines) {
		super(board, destroyers, frigates, corvettes, submarines);
		this.name = "Schlaue KI";
	}

	public Shot getTarget(FieldState[][] fieldStates) throws Exception {
		enemyBoardRepresentation = buildBoardRepresentation(fieldStates);
		if (hasTargets()) {
			return getNextTarget();
		} else {
			return getNewTarget();
		}
	}

	private Shot getNewTarget() throws Exception {
		// Wenn es mehr als 2 Mitspieler gibt oder die KI mit einem Schuss
		// gleichzeitig zwei Schiffe getroffen hat (nur mit Destroyer
		// möglich), kann es sein, dass es getroffene Schiffe gibt, die sich
		// die KI nicht gemerkt hat.
		// Deshalb wird, wenn die KI keine vorgemerkten Ziele mehr hat, das
		// Feld nach getroffenen Schiffen abgesucht

		ArrayList<Field> hitFields = lookForHitFields();

		// wenn ein getroffenes Schiff gefunden wurde, dann plane die nächsten
		// Schüsse und greife das gefundene Ziel an
		if (hitFields.size() > 0) {
			System.out.println(this + "HAT ZIELE GEFUNDEN");
			planNextShots(hitFields);
			lastShot = getNextTarget();
		} else {
			// wenn keine getroffenen Schiffe gefunden wurden
			// zufällig schießen, Schuss merken
			// setzt currentFieldShotAt und currentShotOrientation
			lastShot = getRandomShot();
		}
		return lastShot;
	}

	private Shot getNextTarget() throws Exception {
		int currentDirection = getCurrentDirection();

		// wenn der letzte Schuss ein Treffer war, dann nach nächstem
		// unbeschossenen Feld in selbe Richtung suchen und als nächstes Ziel
		// speichern

		if (lastShot != null) {
			Field lastFieldShotAt = enemyBoardRepresentation.getField(lastShot.getX(), lastShot.getY());

			if (!isTargetDestroyed()) {
				if (lastFieldShotAt.getState() == FieldState.Hit) {
					// wenn Treffer
					int[] directionArray = getDirectionArray(currentDirection);
					Field newTarget = findNextTarget(lastFieldShotAt, directionArray[0], directionArray[1]);
					// neues Ziel in gleiche Richtung setzen
					nextTargetsArray[currentDirection] = newTarget;

				} else {
					// wenn kein Treffer, dann Target löschen
					nextTargetsArray[currentDirection] = null;
				}
			} else {
				// Schiff zerstört, komplettes targetArray löschen,
				// da die Spur nicht mehr verfolgt werden muss
				nextTargetsArray = null;
				System.out.println(this + "Zerstört, ZIEL LÖSCHEN");
			}

		}

		// hat er immer noch targets?
		if (hasTargets()) {
			Field target;
			currentDirection = getCurrentDirection();
			System.out.println(currentDirection);
			// aktuelles Ziel ermitteln
			target = nextTargetsArray[currentDirection];
			// wenn Richtung Osten oder Westen, dann Ausrichtung horizontal,
			// ansonsten vertikal
			Orientation orientation = (currentDirection == EAST || currentDirection == WEST) ? Orientation.Horizontal : Orientation.Vertical;

			// x und y abhängig von der Schussweite korrigieren, so dass
			// möglichst viele Felder getroffen werden
			int range = this.currentShip.getShootingRange() - 1;
			int adjustedX = adjustX(target, currentDirection, range);
			int adjustedY = adjustY(target, currentDirection, range);

			lastShot = new Shot(adjustedX, adjustedY, orientation);

		} else {
			System.out.println(this + "HAT KEIN ZIEL MEHR");
			lastShot = null;
			getNewTarget();
		}

		return lastShot;

	}

	private boolean isTargetDestroyed() throws FieldOutOfBoardException {
		ArrayList<Field> hitFields = getHitFields();
		for (Field f : hitFields) {
			if (f.getState() == FieldState.Destroyed) {
				return true;
			}
		}
		return false;
	}

	private ArrayList<Field> lookForHitFields() throws FieldOutOfBoardException {
		ArrayList<Field> hitFields = new ArrayList<Field>();
		for (int i = 0; i < enemyBoardRepresentation.getSize(); i++) {
			for (int j = 0; j < enemyBoardRepresentation.getSize(); j++) {
				Field f = enemyBoardRepresentation.getField(j, i);
				if (f.getState() == FieldState.Hit) {
					hitFields.add(f);
				}
			}
		}
		return hitFields;
	}

	private Shot getRandomShot() throws Exception {
		Orientation orientation;
		Field fieldShotAt;

		int boardSize = enemyBoardRepresentation.getSize();
		// zufällig schießen

		// wiederhole die Erzeugung von zufälligen Koordinaten
		// bis ein Feld gefunden wird, an welches kein zerstörtes
		// Schiff angrenzt,
		// (zwischen den Schiffen muss immer ein Feld frei sein)
		do {
			orientation = (createRandomNumber(0, 1) == 0) ? Orientation.Horizontal : Orientation.Vertical;
			fieldShotAt = createRandomField(0, board.getSize() - 1, 0, board.getSize() - 1);
		} while (surroundingFieldContainsShip(fieldShotAt));

		// wenn möglich, den Schuss so ausrichten, dass alle
		// Schussfelder im Board sind
		int adjustedX = fieldShotAt.getXPos();
		int adjustedY = fieldShotAt.getYPos();
		// versuche x-Koordinate anzupassen
		if ((fieldShotAt.getXPos() + this.currentShip.getShootingRange() >= (boardSize)) && (orientation == Orientation.Horizontal)) {
			int overhang = (fieldShotAt.getXPos() + this.currentShip.getShootingRange()) - (boardSize);
			adjustedX = adjustX(fieldShotAt, WEST, overhang);
		}
		// versuche y-Koordinate anzupassen
		if ((fieldShotAt.getYPos() + this.currentShip.getShootingRange() >= (boardSize)) && (orientation == Orientation.Vertical)) {
			int overhang = (fieldShotAt.getYPos() + this.currentShip.getShootingRange()) - (boardSize);
			adjustedY = adjustY(fieldShotAt, NORTH, overhang);
		}
		fieldShotAt = new Field(adjustedX, adjustedY);

		return new Shot(adjustedX, adjustedY, orientation);

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
				System.out.println("HORIZONTALES ZIEL");
				target = findNextTarget(hitFields.get(0), directions[0], directions[1]);
			}
			if (isVerticalHit && (i == NORTH || i == SOUTH)) {
				System.out.println("VERTIKALES ZIEL");
				target = findNextTarget(hitFields.get(0), directions[0], directions[1]);
			}
			// wenn potenzielles Ziel gefunden, dann Feld merken
			nextTargetsArray[i] = target;
			System.out.println("PLANE SCHÜSSE");
			System.out.println(target);
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
				if ((target.getState() == FieldState.Missed) || (target.getState() == FieldState.Destroyed)) {
					// wenn Schiff verfehlt oder zerstört wurde, Ziel nicht
					// merken, Schleife abbrechen
					target = null;
					endLoop = true;
				} else if (!target.isHit()) {
					// wenn Feld noch nicht beschossen wurde, Schleife
					// abbrechen, Feld zurückgeben
					System.out.println("GEFUNDEN");
					System.out.println("X" + target.getXPos() + "Y" + target.getYPos());
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

	private ArrayList<Field> getHitFields() throws FieldOutOfBoardException {
		// prüft ob ein zufälliger Schuss (teilweise) getroffen hat
		// gibt den ersten gefundenen Treffer zurück
		// gibt null zurück, wenn es keinen Treffer gab
		int xDirection = lastShot.getOrientation() == Orientation.Horizontal ? 1 : 0;
		int yDirection = lastShot.getOrientation() == Orientation.Vertical ? 1 : 0;
		int x;
		int y;
		Field hitField = null;
		ArrayList<Field> fields = new ArrayList<Field>();
		for (int i = 0; i < this.currentShip.getShootingRange(); i++) {
			x = lastShot.getX() + i * xDirection;
			y = lastShot.getY() + i * yDirection;
			if (enemyBoardRepresentation.containsFieldAtPosition(x, y)) {
				hitField = enemyBoardRepresentation.getField(x, y);
				if (hitField.hasShip()) {
					fields.add(hitField);
				}
			}
		}
		return fields;
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

	// baut anhand der bekannten Fieldstates eine Nachbildung des Boards
	private Board buildBoardRepresentation(FieldState[][] fieldStates) throws FieldOutOfBoardException {
		enemyBoardRepresentation = new Board(fieldStates.length);
		Field[][] fields = board.getFields();
		for (int i = 0; i < fieldStates.length; i++) {
			for (int j = 0; j < fieldStates[i].length; j++) {
				FieldState state = fieldStates[i][j];
				if (state != null) {
					Field f = enemyBoardRepresentation.getField(j, i);
					switch (state) {
					case Destroyed:
						Submarine submarine = new Submarine();
						submarine.setSize(0);
						f.setShip(submarine);
						f.setHit(true);
						break;
					case Missed:
						f.setHit(true);
						break;
					case Hit:
						f.setHit(true);
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

}

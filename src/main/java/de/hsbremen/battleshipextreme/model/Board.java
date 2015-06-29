package de.hsbremen.battleshipextreme.model;

import java.io.Serializable;
import java.util.ArrayList;

import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;

public class Board implements Serializable {
	private static final long serialVersionUID = 482478412129512090L;
	private Field[][] fields;
	private int size;

	/**
	 * Erzeugt ein Board anhand der übergebenen Größe.
	 * 
	 * @param size
	 *            Boardgröße
	 */
	public Board(int size) {
		this.size = size;
		this.fields = new Field[size][size];
		for (int row = 0; row < fields.length; row++) {
			for (int column = 0; column < fields[row].length; column++) {
				this.fields[row][column] = new Field(column, row);
			}
		}
	}

	/**
	 * Liefert alle Felder des Boards.
	 * 
	 * @return alle Felder
	 */
	public Field[][] getFields() {
		return fields;
	}

	/**
	 * Liefert ein bestimmtes Feld vom Board.
	 * 
	 * @param x
	 * @param y
	 * @return das Feld an der Position x/y
	 * @throws FieldOutOfBoardException
	 *             wenn sich die Koordinaten nicht innerhalb des Feldes
	 *             befinden.
	 */
	public Field getField(int x, int y) throws FieldOutOfBoardException {
		if (!this.containsFieldAtPosition(x, y)) {
			throw new FieldOutOfBoardException(new Field(x, y));
		}
		return this.fields[y][x];
	}

	/**
	 * Dient zum Prüfen, ob das Board ein Feld mit den übergebenen Koordinaten
	 * enthält.
	 * 
	 * @param x
	 * @param y
	 * @return true, wenn das Feld enthalten ist, false wenn nicht
	 */
	public boolean containsFieldAtPosition(int x, int y) {
		return (x < this.size) && (y < this.size) && (x >= 0) && (y >= 0);
	}

	public int getSize() {
		return size;
	}

	/**
	 * Liefert alle Feldzustände eines Boardes abhängig davon, ob es das eigene
	 * Board oder das Board eines Gegners ist. Es werden nur Feldzustände
	 * zurückgegeben, die der Spieler wissen darf.
	 * 
	 * @param isOwnBoard
	 *            gibt an, ob es sich um das eigene Board handelt
	 * @return
	 */
	public FieldState[][] getFieldStates(boolean isOwnBoard) {
		FieldState[][] fieldStates = new FieldState[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				FieldState state = fields[j][i].getState();
				if ((state == FieldState.HAS_SHIP || state == FieldState.IS_EMPTY) && (!isOwnBoard)) {
					fieldStates[i][j] = null;
				} else {
					fieldStates[i][j] = state;
				}
			}
		}
		return fieldStates;
	}

	public ArrayList<Field> getFieldsOfShip(Field sourceField) {

		ArrayList<Field> result = new ArrayList<Field>();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (fields[i][j].hasShip()) {
					if (fields[i][j].getShip() == sourceField.getShip()) {
						result.add(fields[i][j]);
					}
				}
			}
		}
		return result;
	}
}

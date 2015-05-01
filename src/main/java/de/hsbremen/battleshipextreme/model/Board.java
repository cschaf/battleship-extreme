package de.hsbremen.battleshipextreme.model;

import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;

public class Board {
	private Field[][] fields;
	private int size;

	public Board(int size) {
		this.size = size;
		this.fields = new Field[size][size];
		for (int row = 0; row < fields.length; row++) {
			for (int column = 0; column < fields[row].length; column++) {
				this.fields[row][column] = new Field(column, row);
			}
		}
	}

	public Field[][] getFields() {
		return fields;
	}

	public Field getField(int x, int y) throws FieldOutOfBoardException {
		if (x < 0 || y < 0 || x >= this.size || y >= this.size)
			throw new FieldOutOfBoardException(new Field(x, y));
		return this.fields[y][x];
	}

	public int getSize() {
		return size;
	}

	public void printBoard(boolean isOwnBoard) {
		String s = isOwnBoard ? "\nEigenes Board" : "\nGegnerisches Board";
		System.out.println(s);
		Field[][] fields = this.getFields();
		for (int row = 0; row < fields.length; row++) {
			for (int column = 0; column < fields[row].length; column++) {
				Field field = fields[row][column];
				printState(field.getState(), isOwnBoard);
			}
			System.out.println();
		}
	}

	private void printState(FieldState fieldState, boolean isOwnBoard) {
		String s = "";
		switch (fieldState) {
		case Destroyed:
			s = "!";
			break;
		case Hit:
			s = "O";
			break;
		case Missed:
			s = "X";
			break;
		case HasShip:
			s = isOwnBoard ? "+" : "?";
			break;
		case IsEmpty:
			s = isOwnBoard ? "-" : "?";
			break;
		default:
			break;
		}
		System.out.print(s);
	}
}

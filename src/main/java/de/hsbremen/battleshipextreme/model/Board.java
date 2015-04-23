package de.hsbremen.battleshipextreme.model;

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
	
	public Field getField(int x, int y) {
		return this.fields[y][x];
	}
	
	public int getSize() {
		return size;
	}
}

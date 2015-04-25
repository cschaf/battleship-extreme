package de.hsbremen.battleshipextreme.model;

public class FieldOutOfBoardException extends Exception {
	public FieldOutOfBoardException(Field field) {
		super("Field at position x=" + field.getXPos() + ", y=" + field.getYPos() + " is not on the Board!");
	}
}

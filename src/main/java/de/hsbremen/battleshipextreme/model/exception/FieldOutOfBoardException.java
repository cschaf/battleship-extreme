package de.hsbremen.battleshipextreme.model.exception;

import de.hsbremen.battleshipextreme.model.Field;

public class FieldOutOfBoardException extends Exception {
	private Field field;
	
	public FieldOutOfBoardException(Field field) {
		super("Field at position x=" + field.getXPos() + ", y=" + field.getYPos() + " is not on the Board!");
		this.field = field;
	}
	
	public Field getField() {
		return field;
	}
}

package de.hsbremen.battleshipextreme.model.exception;

import de.hsbremen.battleshipextreme.model.Field;

public class FieldOccupiedException extends Exception {
	private Field field;
	
	public FieldOccupiedException(Field field) {
		super("Field at position: x=" + field.getXPos() + ", y=" + field.getYPos() + " already occupied!");
		this.field = field;
	}

	public Field getField() {
		return field;
	}
}

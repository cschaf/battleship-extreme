package de.hsbremen.battleshipextreme.model;

public class FieldOccupiedException extends Exception {
	public FieldOccupiedException(Field field) {
		super("Field at position: x=" + field.getXPos() + ", y=" + field.getYPos() + " already occupied!");
	}
}

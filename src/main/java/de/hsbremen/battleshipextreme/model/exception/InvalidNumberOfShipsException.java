package de.hsbremen.battleshipextreme.model.exception;

public class InvalidNumberOfShipsException extends Exception {
	public InvalidNumberOfShipsException() {
		super("Invalid number of ships! There must be at least one ship.");
	}
}

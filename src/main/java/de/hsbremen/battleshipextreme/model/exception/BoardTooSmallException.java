package de.hsbremen.battleshipextreme.model.exception;

public class BoardTooSmallException extends Exception {
	public BoardTooSmallException() {
		super("Board too small!");
	}
}

package de.hsbremen.battleshipextreme.model.exception;

public class BoardTooSmallException extends Exception {
	private float minPercentageOfFieldsThatShouldBeEmpty;
	private float emptyFieldPercentage;

	public BoardTooSmallException(float minPercentageOfFieldsThatShouldBeEmpty, float emptyFieldPercentage, int availableFields, int requiredFields) {
		super("Board too small!" + minPercentageOfFieldsThatShouldBeEmpty + "should be empty! " + emptyFieldPercentage + "% free fields left " + " available fields: " + availableFields
				+ " required fields: " + requiredFields);
		this.minPercentageOfFieldsThatShouldBeEmpty = minPercentageOfFieldsThatShouldBeEmpty;
		this.emptyFieldPercentage = emptyFieldPercentage;
	}

	public float getMinPercentageOfFieldsThatShouldBeEmpty() {
		return minPercentageOfFieldsThatShouldBeEmpty;
	}

	public float getEmptyFieldPercentage() {
		return emptyFieldPercentage;
	}

}

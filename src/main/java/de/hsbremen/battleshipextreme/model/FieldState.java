package de.hsbremen.battleshipextreme.model;

public enum FieldState {
	IS_EMPTY, HIT, MISSED, DESTROYED, HAS_SHIP;

	public static FieldState[][] array2dOfDefault(int length) {
		FieldState[][] fields = new FieldState[length][length];
		for (int row = 0; row < length; row++) {
			for (int col = 0; col < length; col++) {
				fields[row][col] = FieldState.IS_EMPTY;
			}
		}
		return fields;
	}
}

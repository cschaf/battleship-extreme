package de.hsbremen.battleshipextreme.model.player;

import de.hsbremen.battleshipextreme.model.Orientation;

public class Target {
	private int x;
	private int y;
	private Orientation orientation;

	public Target(int x, int y, Orientation orientation) {
		this.x = x;
		this.y = y;
		this.orientation = orientation;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Orientation getOrientation() {
		return orientation;
	}

}

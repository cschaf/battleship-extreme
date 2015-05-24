package de.hsbremen.battleshipextreme.model.player;

import de.hsbremen.battleshipextreme.model.Orientation;

public class Shot {
	private int x;
	private int y;
	private Orientation orientation;

	public Shot(int x, int y, Orientation orientation) {
		this.x = x;
		this.y = y;
		this.orientation = orientation;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}
}

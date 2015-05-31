package de.hsbremen.battleshipextreme.client;

import javax.swing.JButton;

public class FieldButton extends JButton {
	private int xPos;
	private int yPos;

	public FieldButton(int xPos, int yPos) {
		this.xPos = xPos;
		this.yPos = yPos;
		setOpaque(true);
	}

	public int getxPos() {
		return xPos;
	}

	public int getyPos() {
		return yPos;
	}
}

package de.hsbremen.battleshipextreme.client;

import javax.swing.JButton;

public class FieldButton extends JButton {
	private int xPos;
	private int yPos;

	public FieldButton(int xPos, int yPos) {
		this.xPos = xPos;
		this.yPos = yPos;

		// setBorderPainted(false);
		// setBorder(null);
		// button.setFocusable(false);
		// setMargin(new Insets(0, 0, 0, 0));
		// setContentAreaFilled(false);

	}

	public int getxPos() {
		return xPos;
	}

	public int getyPos() {
		return yPos;
	}
}

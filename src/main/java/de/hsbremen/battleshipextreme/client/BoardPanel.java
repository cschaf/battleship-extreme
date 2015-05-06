package de.hsbremen.battleshipextreme.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public class BoardPanel extends JPanel {
	
	private JButton[][] fieldButtons;
	
	public BoardPanel(String title, int fieldSize) {
		this.setBorder(BorderFactory.createTitledBorder(title));
		this.setLayout(new GridLayout(fieldSize, fieldSize));
		
		fieldButtons = new JButton[fieldSize][fieldSize];
		for (int y = 0; y < fieldButtons.length; y++) {
			for (int x = 0; x < fieldButtons[y].length; x++) {
				fieldButtons[y][x] = new JButton();
				fieldButtons[y][x].setBackground(new Color(135, 206, 250));
				fieldButtons[y][x].setPreferredSize(new Dimension(50, 50));
				this.add(fieldButtons[y][x]);
			}
		}
	}
	
	public JButton[][] getFieldButtons() {
		return fieldButtons;
	}
	
	public JButton getFieldButtonByIndex(int x, int y) {
		return fieldButtons[y][x];
	}
}

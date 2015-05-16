package de.hsbremen.battleshipextreme.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public class BoardPanel extends JPanel {
	
	private JButton[][] buttonsField;
	
	public BoardPanel(String title, int boardSize) {
		this.setBorder(BorderFactory.createTitledBorder(title));
		this.setLayout(new GridLayout(boardSize, boardSize));

		buttonsField = new JButton[boardSize][boardSize];
		for (int y = 0; y < buttonsField.length; y++) {
			for (int x = 0; x < buttonsField[y].length; x++) {
				buttonsField[y][x] = new JButton();
				buttonsField[y][x].setBackground(new Color(135, 206, 250));
				buttonsField[y][x].setPreferredSize(new Dimension(50, 50));
				this.add(buttonsField[y][x]);
			}
		}
	}
	
	public JButton[][] getButtonsField() {
		return buttonsField;
	}
	
	public JButton getButtonFieldByIndex(int x, int y) {
		return buttonsField[y][x];
	}
}

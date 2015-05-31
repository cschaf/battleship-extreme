package de.hsbremen.battleshipextreme.client;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class BoardPanel extends JPanel {

	private JButton[][] buttonsField;
	private String title;

	public BoardPanel(String title) {

		this.title = title;

	}

	public void createBoardPanel(int boardSize) {
		// ohne Nummerierung
		// this.setBorder(BorderFactory.createTitledBorder(this.title));
		// this.setLayout(new GridLayout(boardSize, boardSize));
		//
		// buttonsField = new JButton[boardSize][boardSize];
		// for (int y = 0; y < buttonsField.length; y++) {
		// for (int x = 0; x < buttonsField[y].length; x++) {
		// buttonsField[y][x] = new JButton();
		// buttonsField[y][x].setBackground(new Color(135, 206, 250));
		// this.add(buttonsField[y][x]);
		// }
		// }

		// mit Nummerierung
		this.setBorder(BorderFactory.createTitledBorder(this.title));
		this.setLayout(new GridLayout(boardSize + 1, boardSize + 1));

		// leeres Label (oben Links)
		this.add(new JLabel());
		// Char fuer alphabetische Nummerierung
		char c = '\u0041';
		// alphabetische Nummerierung (erste Zeile)
		for (int i = 1; i < boardSize + 1; i++) {
			this.add(new JLabel(Character.toString(c++), SwingConstants.CENTER));
		}

		buttonsField = new JButton[boardSize][boardSize];

		for (int y = 0; y < buttonsField.length; y++) {
			// Nummerierung jeweils erste Spalte
			this.add(new JLabel(Integer.toString(y + 1), SwingConstants.CENTER));
			// Buttons hinzufuegen
			for (int x = 0; x < buttonsField[y].length; x++) {
				buttonsField[y][x] = new FieldButton(x, y);
				buttonsField[y][x].setBackground(new Color(135, 206, 250));
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		this.setBorder(BorderFactory.createTitledBorder(this.title));
	}

}

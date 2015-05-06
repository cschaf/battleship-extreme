package de.hsbremen.battleshipextreme.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class ShipSelectionPanel extends JPanel {
	
	JLabel[] shipCountLabels;
	JLabel[][] shipLabels;
	JRadioButton[] shipSelectionRadioButtons;
	
	public ShipSelectionPanel() {
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createTitledBorder("Your Ships"));
		
		// GridBagLayout Einschränkungen
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.weightx = 0;
		c.weighty = 0;

		// Labels für die Anzahl der Schiffe
		this.shipCountLabels = new JLabel[4];
		
		for (int i = 0; i < this.shipCountLabels.length; i++) {
			c.insets = new Insets(0, 3, 0, 5);
			c.gridx = 0;
			c.gridy = i;
			this.shipCountLabels[i] = new JLabel("0x");
			this.shipCountLabels[i].setFont(new Font("Tahoma", Font.BOLD, 15));
			this.add(this.shipCountLabels[i], c);
		}
		
		// Labels für die Schiffe
		this.shipLabels = new JLabel[4][];
		this.shipLabels[0] = new JLabel[5];
		this.shipLabels[1] = new JLabel[4];
		this.shipLabels[2] = new JLabel[3];
		this.shipLabels[3] = new JLabel[2];
		
		for (int y = 0; y < this.shipLabels.length; y++) {
			for (int x = 0; x < this.shipLabels[y].length; x++) {
				c.insets = new Insets(0, 0, 1, 1);
				c.gridx = x + 1;
				c.gridy = y;
				c.ipadx = c.ipady = 30;
				this.shipLabels[y][x] = new JLabel();
				this.shipLabels[y][x].setBackground(Color.BLUE);
				this.shipLabels[y][x].setOpaque(true);
				this.add(this.shipLabels[y][x], c);
			}
		}

		// RadioButtons fuer Schiffauswahl
		c.ipadx = 0;
		c.ipady = 0;
		
		this.shipSelectionRadioButtons = new JRadioButton[4];
		this.shipSelectionRadioButtons[0] = new JRadioButton("Destroyer");
		this.shipSelectionRadioButtons[1] = new JRadioButton("Frigate");
		this.shipSelectionRadioButtons[2] = new JRadioButton("Corvette");
		this.shipSelectionRadioButtons[3] = new JRadioButton("Submarine");
		
		ButtonGroup group = new ButtonGroup();

		for (int i = 0; i < this.shipSelectionRadioButtons.length; i++) {
			c.insets = new Insets(0, 5, 0, 0);
			c.weightx = 1;
			c.gridx = 6;
			c.gridy = i;
			this.shipSelectionRadioButtons[i].setOpaque(false);
			this.add(this.shipSelectionRadioButtons[i], c);
			group.add(this.shipSelectionRadioButtons[i]);
		}
		
		this.shipSelectionRadioButtons[0].setSelected(true);
	}

	//////////////////////////////////////////////////////////////////
	// get Ship Count Labels
	
	public JLabel getDestroyerShipCountLabel() {
		return shipCountLabels[0];
	}

	public JLabel getFrigateShipCountLabel() {
		return shipCountLabels[1];
	}

	public JLabel getCorvetteShipCountLabel() {
		return shipCountLabels[2];
	}

	public JLabel getSubmarineShipCountLabel() {
		return shipCountLabels[3];
	}

	//////////////////////////////////////////////////////////////////
	// get Ship Labels
	
	public JLabel[] getDestroyerLabels() {
		return shipLabels[0];
	}
	
	public JLabel[] getFrigateLabels() {
		return shipLabels[1];
	}
	
	public JLabel[] getCorvetteLabels() {
		return shipLabels[2];
	}
	
	public JLabel[] getSubmarineLabels() {
		return shipLabels[3];
	}
	
	//////////////////////////////////////////////////////////////////
	// get Radio Buttons
	
	public JRadioButton getDestroyerRadioButton() {
		return shipSelectionRadioButtons[0];
	}

	public JRadioButton getFrigateRadioButton() {
		return shipSelectionRadioButtons[1];
	}

	public JRadioButton getCorvetteRadioButton() {
		return shipSelectionRadioButtons[2];
	}

	public JRadioButton getSubmarineRadioButton() {
		return shipSelectionRadioButtons[3];
	}
}

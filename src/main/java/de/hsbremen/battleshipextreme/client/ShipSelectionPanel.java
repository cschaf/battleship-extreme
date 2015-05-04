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
	
	JLabel[] lblShipCount;
	JLabel[][] lblShips;
	JRadioButton[] rbShipSelection;
	
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
		this.lblShipCount = new JLabel[4];
		
		for (int i = 0; i < this.lblShipCount.length; i++) {
			c.insets = new Insets(0, 3, 0, 5);
			c.gridx = 0;
			c.gridy = i;
			this.lblShipCount[i] = new JLabel("0x");
			this.lblShipCount[i].setFont(new Font("Tahoma", Font.BOLD, 15));
			this.add(this.lblShipCount[i], c);
		}
		
		// Labels für die Schiffe
		this.lblShips = new JLabel[4][];
		this.lblShips[0] = new JLabel[5];
		this.lblShips[1] = new JLabel[4];
		this.lblShips[2] = new JLabel[3];
		this.lblShips[3] = new JLabel[2];
		
		for (int y = 0; y < this.lblShips.length; y++) {
			for (int x = 0; x < this.lblShips[y].length; x++) {
				c.insets = new Insets(0, 0, 1, 1);
				c.gridx = x + 1;
				c.gridy = y;
				c.ipadx = c.ipady = 30;
				this.lblShips[y][x] = new JLabel();
				this.lblShips[y][x].setBackground(Color.BLUE);
				this.lblShips[y][x].setOpaque(true);
				this.add(this.lblShips[y][x], c);
			}
		}

		// RadioButtons fuer Schiffauswahl
		c.ipadx = 0;
		c.ipady = 0;
		
		this.rbShipSelection = new JRadioButton[4];
		this.rbShipSelection[0] = new JRadioButton("Destroyer");
		this.rbShipSelection[1] = new JRadioButton("Frigate");
		this.rbShipSelection[2] = new JRadioButton("Corvette");
		this.rbShipSelection[3] = new JRadioButton("Submarine");
		
		ButtonGroup group = new ButtonGroup();

		for (int i = 0; i < this.rbShipSelection.length; i++) {
			c.insets = new Insets(0, 5, 0, 0);
			c.weightx = 1;
			c.gridx = 6;
			c.gridy = i;
			this.rbShipSelection[i].setOpaque(false);
			this.add(this.rbShipSelection[i], c);
			group.add(this.rbShipSelection[i]);
		}
		
		this.rbShipSelection[0].setSelected(true);
	}
}

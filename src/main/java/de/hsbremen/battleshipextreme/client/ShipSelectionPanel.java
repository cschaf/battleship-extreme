package de.hsbremen.battleshipextreme.client;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ShipSelectionPanel extends JPanel {
	
	public ShipSelectionPanel() {
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createTitledBorder("Schiffauswahl"));
		this.setBackground(Color.YELLOW);
		
		// GridBagLayout Einschränkungen
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.ipadx = c.ipady = 20;
		c.insets = new Insets(0, 0, 1, 1);
		
		// Schiffe setzen
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 5; x++) {
				c.gridx = x;
				c.gridy = y;
				JLabel l = new JLabel();
				l.setBackground(Color.BLUE);
				l.setOpaque(true);
				
				if (y == 0)
					this.add(l, c);
				else if (y == 1 && x < 4)
					this.add(l, c);
				else if (y == 2 && x < 3)
					this.add(l, c);
				else if (y == 3 && x < 2)
					this.add(l, c);
			}
		}
		
		// unsichtbares Label damit der Content links sitzt
		c.gridx = 5;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.ipadx = c.ipady = 0;
		this.add(Box.createGlue(), c);
	}
}

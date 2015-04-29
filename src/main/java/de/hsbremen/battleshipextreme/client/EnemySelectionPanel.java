package de.hsbremen.battleshipextreme.client;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

public class EnemySelectionPanel extends JPanel {

	JComboBox cbEnemys;
	JButton btnApplyEnemy;
	
	public EnemySelectionPanel() {
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createTitledBorder("Gegnerauswahl"));
		this.setBackground(Color.GREEN);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTHWEST;
		
		// Gegner auswählen ComboBox
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		this.cbEnemys = new JComboBox();
		this.add(cbEnemys, c);
		
		// Gegner auswählen Button
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		this.btnApplyEnemy = new JButton("OK");
		this.add(btnApplyEnemy, c);
	}
}

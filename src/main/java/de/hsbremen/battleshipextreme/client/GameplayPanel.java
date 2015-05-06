package de.hsbremen.battleshipextreme.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class GameplayPanel extends JPanel {
	
	private ShipSelectionPanel shipSelectionPanel;
	private JRadioButton horizontalOrientationRadioButton, verticalOrientationRadioButton;
	private JComboBox enemySelectionComboBox;
	private JButton applyEnemyButton;
	private JTextArea gameLogTextArea;
	private ChatPanel chatPanel;
	
	public GameplayPanel() {
		this.setLayout(new GridBagLayout());
		
		// Einschraenkungen
		GridBagConstraints c = new GridBagConstraints();
		
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 0;
		
		// Schiffauswahl
		shipSelectionPanel = new ShipSelectionPanel();
		this.add(shipSelectionPanel, c);
		
		// Ausrichtungspanel
		c.gridy++;
		this.add(createOrientationPanel(), c);
		
		// Gegnerauswahl
		c.gridy++;
		this.add(createEnemySelectionPanel(), c);
		
		// Game Log
		c.gridy++;
		c.weighty = 1;
		this.add(createGameLogPanel(), c);
		
		// Chat Panel
		c.gridy++;
		c.weighty = 0;
		chatPanel = new ChatPanel();
		this.add(chatPanel, c);
	}

	//////////////////////////////////////////////////////////////////
	// create Panels
	
	private JPanel createOrientationPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 2));
		panel.setBorder(BorderFactory.createTitledBorder("Orientation"));
		
		horizontalOrientationRadioButton = new JRadioButton("Horizontal");
		verticalOrientationRadioButton = new JRadioButton("Vertical");
		
		ButtonGroup group = new ButtonGroup();
		group.add(horizontalOrientationRadioButton);
		group.add(verticalOrientationRadioButton);
		
		horizontalOrientationRadioButton.setSelected(true);
		
		panel.add(horizontalOrientationRadioButton);
		panel.add(verticalOrientationRadioButton);
		
		return panel;
	}
	
	private JPanel createEnemySelectionPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Enemys"));
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 0;
		
		enemySelectionComboBox = new JComboBox();
		panel.add(enemySelectionComboBox, c);
		
		c.weightx = 0;
		c.gridx++;
		applyEnemyButton = new JButton("OK");
		panel.add(applyEnemyButton, c);
		
		return panel;
	}
	
	private JPanel createGameLogPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Game log"));
		
		gameLogTextArea = new JTextArea();
		gameLogTextArea.setEditable(false);
		gameLogTextArea.setRows(10);
		gameLogTextArea.setLineWrap(true);
		gameLogTextArea.setWrapStyleWord(true);
		
		JScrollPane scroll = new JScrollPane(gameLogTextArea);
		panel.add(scroll);
		
		return panel;
	}


	//////////////////////////////////////////////////////////////////
	// get Components
	
	public JRadioButton getHorizontalOrientationRadioButton() {
		return horizontalOrientationRadioButton;
	}
	
	public JRadioButton getVerticalOrientationRadioButton() {
		return verticalOrientationRadioButton;
	}
	
	public JComboBox getEnemySelectionComboBox() {
		return enemySelectionComboBox;
	}
	
	public JButton getApplyEnemyButton() {
		return applyEnemyButton;
	}
	
	public JTextArea getGameLogTextArea() {
		return gameLogTextArea;
	}
	
	
	
}

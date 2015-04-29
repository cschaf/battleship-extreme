package de.hsbremen.battleshipextreme.client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class GUI {
	JFrame frame;
	ShipSelectionPanel panelShipSelection;
	EnemySelectionPanel panelEnemySelection;
	ChatPanel panelChat;
	JPanel panelGame;
	JLabel lblStatus;
	JButton btnQuit;
	
	public GUI() {
		this.frame = new JFrame("Battleship-Extreme");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		InitComponents();

		this.frame.pack();
		this.frame.setLocationRelativeTo(null);
		this.frame.setVisible(true);
	}
	
	private void InitComponents() {
		
		// Panel für das gesamte Spiel
		this.panelGame = new JPanel(new BorderLayout());
		this.frame.add(panelGame);

		// Panel für die Spieleinstelltungen (linke Seite)
		JPanel panelSettings = new JPanel(new GridBagLayout());
		this.panelGame.add(panelSettings, BorderLayout.WEST);
		
		// GridBagLayout Einschränkungen
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		
		// Label für die Statusanzeige
		c.ipady = 30;
		this.lblStatus = new JLabel("Informationsbereich!");
		this.lblStatus.setBackground(Color.RED);
		this.lblStatus.setFont(new Font("Tahoma", Font.BOLD, 20));
		this.lblStatus.setOpaque(true);
		panelSettings.add(lblStatus, c);
		
		// Panel für die Schiffauswahl
		c.gridy++;
		c.ipady = 0;
		this.panelShipSelection = new ShipSelectionPanel();
		panelSettings.add(this.panelShipSelection, c);
		
		// Panel für die Gegnerauswahl
		c.gridy++;
		this.panelEnemySelection = new EnemySelectionPanel();
		panelSettings.add(this.panelEnemySelection, c);

		// Panel für die Gegnerauswahl
		c.gridy++;
		c.weighty = 1;
		this.panelChat = new ChatPanel();
		panelSettings.add(this.panelChat, c);
		
		// Button für die Aufgabe
		c.anchor = GridBagConstraints.SOUTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy++;
		c.weighty = 0;
		this.btnQuit = new JButton("Aufgeben");
		panelSettings.add(btnQuit, c);
		
	}
}

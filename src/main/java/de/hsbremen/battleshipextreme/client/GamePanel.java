package de.hsbremen.battleshipextreme.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class GamePanel extends JPanel {

	private GameplayPanel gameplayPanel;
	private JLabel infoLabel;
	private BoardPanel enemyBoardPanel;
	private BoardPanel playerBoardPanel;
	
	public GamePanel() {
		
		int size = 10;
		
		this.setLayout(new BorderLayout());
		
		// Panel fuer die Spielsteuerung
		gameplayPanel = new GameplayPanel();
		this.add(gameplayPanel, BorderLayout.WEST);
		
		// Panel fuer die eigentliche Spielflaeche
		JPanel gameAreaPanel = new JPanel(new BorderLayout());
		this.add(gameAreaPanel);
		
		// Label fuer die Spielinformationen
		infoLabel = new JLabel("Hier stehen aktuelle Spielinformationen!", SwingConstants.CENTER);
		infoLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
		infoLabel.setBackground(Color.orange);
		infoLabel.setOpaque(true);
		gameAreaPanel.add(infoLabel, BorderLayout.NORTH);
		
		// Spielbrett fuer den Gegner
		enemyBoardPanel = new BoardPanel("Enemy", size);
		gameAreaPanel.add(enemyBoardPanel, BorderLayout.WEST);

		// Spielbrett fuer den Player
		playerBoardPanel = new BoardPanel("Yours", size);
		gameAreaPanel.add(playerBoardPanel, BorderLayout.EAST);
	}
	
	public JLabel getInfoLabel() {
		return infoLabel;
	}
	
	
	
}

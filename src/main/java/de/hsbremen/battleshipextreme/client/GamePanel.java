package de.hsbremen.battleshipextreme.client;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class GamePanel extends JPanel {

	GameplayPanel gameplayPanel;
	
	public GamePanel() {
		this.setLayout(new BorderLayout());
		
		// Panel fuer die Spielsteuerung
		gameplayPanel = new GameplayPanel();
		
		this.add(gameplayPanel, BorderLayout.WEST);
	}
}

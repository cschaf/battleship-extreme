package de.hsbremen.battleshipextreme.client;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;

public class MainMenuPanel extends JPanel {
	
	private JButton buttonLocalGame;
	private JButton buttonMultiplayerGame;
	private JButton buttonLoadGame;
	private JButton buttonQuitGame;
	
	public MainMenuPanel() {
		this.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 0, 15, 0);
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 0;
		
		Font f = new Font("Tahoma", Font.PLAIN, 15);
		
		buttonLocalGame = new JButton("Local Game");
		buttonLocalGame.setFont(f);		
		this.add(buttonLocalGame, c);
		
		c.gridy++;
		buttonMultiplayerGame = new JButton("Multiplayer Game");
		buttonMultiplayerGame.setFont(f);
		this.add(buttonMultiplayerGame, c);
		
		c.gridy++;
		buttonLoadGame = new JButton("Load Game");
		buttonLoadGame.setFont(f);
		this.add(buttonLoadGame, c);
		
		c.gridy++;
		buttonQuitGame = new JButton("Quit");
		buttonQuitGame.setFont(f);
		this.add(buttonQuitGame, c);
	}

	//////////////////////////////////////////////////////////////////
	// get Components
	//////////////////////////////////////////////////////////////////

	public JButton getButtonLocalGame() {
		return buttonLocalGame;
	}

	public JButton getButtonMultiplayerGame() {
		return buttonMultiplayerGame;
	}

	public JButton getButtonLoadGame() {
		return buttonLoadGame;
	}

	public JButton getButtonQuitGame() {
		return buttonQuitGame;
	}
	
}

package de.hsbremen.battleshipextreme.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
	
	public static void main(String[] args) {
		final GUI gui = new GUI();
		
		gui.getPanelMainMenu().getButtonLocalGame().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.showPanel(GUI.SETTINGS_PANEL);
			}
		});
		
		gui.getPanelSettings().getButtonApplySettings().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.showPanel(GUI.GAME_PANEL);
				gui.getPanelGame().setPanelEnemyBoard(new BoardPanel("dsd", 20));
				gui.getPanelGame().setPanelEnemyBoard(new BoardPanel("Yours", 20));
			}
		});
	}
}

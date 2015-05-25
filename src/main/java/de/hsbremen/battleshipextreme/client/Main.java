package de.hsbremen.battleshipextreme.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
	
	public static void main(String[] args) {
		final GUI gui = new GUI();
		
		gui.getPanelMainMenu().getButtonLocalGame().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.showPanel(GUI.SETTINGS_PANEL);
				
				gui.getPanelSettings().getTextFieldPlayers().setText("2");
				gui.getPanelSettings().getTextFieldDestroyers().setText("1");
				gui.getPanelSettings().getTextFieldFrigates().setText("1");
				gui.getPanelSettings().getTextFieldCorvettes().setText("1");
				gui.getPanelSettings().getTextFieldSubmarines().setText("1");
				gui.getPanelSettings().getTextFieldBoardSize().setText("10");
			}
		});
		
		gui.getPanelSettings().getButtonApplySettings().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.showPanel(GUI.GAME_PANEL);
				int size = 15;
				gui.getPanelGame().setPanelEnemyBoard(new BoardPanel("Enemy Board", size));
				gui.getPanelGame().setPanelPlayerBoard(new BoardPanel("Your Board", size));
			}
		});
		
		gui.getPanelGame().getButtonApplyEnemy().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
			}
		});
		
	}
}

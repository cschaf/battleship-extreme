package de.hsbremen.battleshipextreme.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.hsbremen.battleshipextreme.model.Game;

public class Controller {
	
	private Game game;
	private GUI gui;
	
	public Controller(Game game, GUI gui) {
		this.game = game;
		this.gui = gui;
		
		gui.getPanelSettings().getTextFieldPlayers().setText(String.valueOf(game.getPlayers().length));
		gui.getPanelSettings().getTextFieldDestroyers().setText("1");
		gui.getPanelSettings().getTextFieldFrigates().setText("1");
		gui.getPanelSettings().getTextFieldCorvettes().setText("1");
		gui.getPanelSettings().getTextFieldSubmarines().setText("1");
		gui.getPanelSettings().getTextFieldBoardSize().setText(String.valueOf(game.getBoardSize()));
		
		initEvents();
	}
	
	private void initEvents() {
		
		gui.getMenuItemQuitGame().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		gui.getPanelMainMenu().getButtonLocalGame().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.showPanel(GUI.SETTINGS_PANEL);
			}
		});
		
		gui.getPanelSettings().getButtonApplySettings().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (areSettingsOK()) {
					int size = Integer.parseInt(gui.getPanelSettings().getTextFieldBoardSize().getText().toString());
					gui.getPanelGame().setPanelEnemyBoard(new BoardPanel("Enemy Board", size));
					gui.getPanelGame().setPanelPlayerBoard(new BoardPanel("Your Board", size));
					
					gui.showPanel(GUI.GAME_PANEL);
				}
				
			}
		});
	}
	
	private boolean areSettingsOK() {
		
		return true;
	}

}

package de.hsbremen.battleshipextreme.client;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class GUI {
	
	public final static String MAIN_MENU_PANEL = "card with main menu panel";
	public final static String SETTINGS_PANEL = "card with settings panel";
	public final static String GAME_PANEL = "card with game panel";
	
	private JFrame frame;
	private MainMenuPanel panelMainMenu;
	private SettingsPanel panelSettings;
	private GamePanel panelGame;
	private JMenuItem menuItemSaveGame, menuItemLoadGame, menuItemQuitGame;
	private JMenuItem menuItemManual;
	
	private JPanel cards;
	
	public GUI() {
		frame = new JFrame("Battleship-Extreme");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setResizable(false);
		frame.setJMenuBar(createMenuBar());
		
		InitComponents();

		frame.pack();
//		frame.setSize(500, 500);
		frame.setLocationRelativeTo(null);
		
//		showPanel(GUI.GAME_PANEL);
		
		frame.setVisible(true);
	}
	
	private void InitComponents() {
		
		panelMainMenu = new MainMenuPanel();
		panelSettings = new SettingsPanel();
		panelGame = new GamePanel();
		
		cards = new JPanel(new CardLayout());
		cards.add(panelMainMenu, MAIN_MENU_PANEL);
		cards.add(panelSettings, SETTINGS_PANEL);
		cards.add(panelGame, GAME_PANEL);
		
		frame.add(cards);
	}
	
	//////////////////////////////////////////////////////////////////
	// create Menu
	//////////////////////////////////////////////////////////////////
	
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		
		// Game Menue
		JMenu gameMenu = new JMenu("Game");
		menuBar.add(gameMenu);

		menuItemSaveGame = new JMenuItem("Save...");
		menuItemLoadGame = new JMenuItem("Load...");
		menuItemQuitGame = new JMenuItem("Quit");
		
		gameMenu.add(menuItemSaveGame);
		gameMenu.add(menuItemLoadGame);
		gameMenu.add(menuItemQuitGame);
		
		// Help Menue
		JMenu helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);
		
		menuItemManual = new JMenuItem("Manual");
		helpMenu.add(menuItemManual);
		
		return menuBar;
	}
	
	//////////////////////////////////////////////////////////////////
	// get Components
	//////////////////////////////////////////////////////////////////

	public JFrame getFrame() {
		return frame;
	}
	
	public MainMenuPanel getPanelMainMenu() {
		return panelMainMenu;
	}
	
	public SettingsPanel getPanelSettings() {
		return panelSettings;
	}
	
	public GamePanel getPanelGame() {
		return panelGame;
	}
	
	public JMenuItem getMenuItemSaveGame() {
		return menuItemSaveGame;
	}
	
	public JMenuItem getMenuItemLoadGame() {
		return menuItemLoadGame;
	}
	
	public JMenuItem getMenuItemQuitGame() {
		return menuItemQuitGame;
	}
	
	public JMenuItem getMenuItemManual() {
		return menuItemManual;
	}
	
	//////////////////////////////////////////////////////////////////
	// methoden
	//////////////////////////////////////////////////////////////////
	
	public void showPanel(String card) {
		CardLayout c1 = (CardLayout)cards.getLayout();
		c1.show(cards, card);
	}

}

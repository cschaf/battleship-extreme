package de.hsbremen.battleshipextreme.client;

import java.awt.CardLayout;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class GUI extends BasicTabbedPaneUI {

	public final static String MAIN_MENU_PANEL = "card with main menu panel";
	public final static String SETTINGS_PANEL = "card with settings panel";
	public final static String GAME_PANEL = "card with game panel";
	public final static String SERVER_CONNECTION_PANEL = "card with server connection panel";

	public final static Color EMPTY_COLOR = new Color(135, 206, 250);
	public final static Color HAS_SHIP_COLOR = Color.black;
	public final static Color HIT_COLOR = Color.orange;
	public final static Color DESTROYED_COLOR = Color.red;
	public final static Color MISSED_COLOR = Color.blue;
	public final static Color UNKNOWN_COLOR = Color.gray;

	private ImageIcon hitIcon = new ImageIcon(getClass().getResource("/hit.png"));
	private ImageIcon missedIcon = new ImageIcon(getClass().getResource("/missed.png"));
	private ImageIcon destroyedIcon = new ImageIcon(getClass().getResource("/destroyed.png"));
	private ImageIcon shipIcon = new ImageIcon(getClass().getResource("/ship.png"));

	private JFrame frame;
	private MainMenuPanel panelMainMenu;
	private SettingsPanel panelSettings;
	private GamePanel panelGame;
	private ServerConnectionPanel panelServerConnection;
	private JMenuItem menuItemMainMenu, menuItemNewGame, menuItemSaveGame, menuItemLoadGame, menuItemQuitGame;
	private JMenuItem menuItemManual;
	private JMenuItem menuItemNextLookAndFeel;

	private JPanel cards;

	public GUI() {
		setDefaultLookAndFeel();

		frame = new JFrame("Battleship-Extreme");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setResizable(false);
		frame.setJMenuBar(createMenuBar());

		initComponents();

		frame.pack();
		// frame.setMinimumSize(new Dimension(1500, 640));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		scaleIcons();
	}

	private void setDefaultLookAndFeel() {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, you can set the GUI to another look
			// and feel.
		}
	}

	private void scaleIcons() {
		hitIcon = getScaledIcon(hitIcon);
		missedIcon = getScaledIcon(missedIcon);
		destroyedIcon = getScaledIcon(destroyedIcon);
		shipIcon = getScaledIcon(shipIcon);
	}

	private void initComponents() {
		panelMainMenu = new MainMenuPanel();
		panelSettings = new SettingsPanel();
		panelGame = new GamePanel();
		panelServerConnection = new ServerConnectionPanel();

		cards = new JPanel(new CardLayout());

		cards.add(panelMainMenu, MAIN_MENU_PANEL);
		cards.add(panelSettings, SETTINGS_PANEL);
		cards.add(panelGame, GAME_PANEL);
		cards.add(panelServerConnection, SERVER_CONNECTION_PANEL);

		frame.add(cards);

		panelSettings.getTextFieldPlayers().setText("2");
		panelSettings.getTextFieldAiPlayers().setText("2");
		panelSettings.getTextFieldDestroyers().setText("1");
		panelSettings.getTextFieldFrigates().setText("1");
		panelSettings.getTextFieldCorvettes().setText("1");
		panelSettings.getTextFieldSubmarines().setText("1");
		panelSettings.getTextFieldBoardSize().setText("10");
	}

	// ////////////////////////////////////////////////////////////////
	// create Menu
	// ////////////////////////////////////////////////////////////////

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		// Game Menue
		JMenu gameMenu = new JMenu("Game");
		menuBar.add(gameMenu);

		menuItemMainMenu = new JMenuItem("Main Menu");
		menuItemNewGame = new JMenuItem("New Game");
		menuItemSaveGame = new JMenuItem("Save...");
		menuItemSaveGame.setEnabled(false);
		menuItemLoadGame = new JMenuItem("Load...");
		menuItemQuitGame = new JMenuItem("Quit");

		gameMenu.add(menuItemMainMenu);
		gameMenu.add(menuItemNewGame);
		gameMenu.add(menuItemSaveGame);
		gameMenu.add(menuItemLoadGame);
		gameMenu.add(menuItemQuitGame);

		// Help Menue
		JMenu helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);

		menuItemManual = new JMenuItem("Manual");
		helpMenu.add(menuItemManual);

		// Window Menue
		JMenu windowMenu = new JMenu("Window");
		menuBar.add(windowMenu);
		menuItemNextLookAndFeel = new JMenuItem("Next Theme");
		windowMenu.add(menuItemNextLookAndFeel);

		return menuBar;
	}

	// ////////////////////////////////////////////////////////////////
	// get Components
	// ////////////////////////////////////////////////////////////////

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

	public ServerConnectionPanel getPanelServerConnection() {
		return panelServerConnection;
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

	public JMenuItem getMenuItemNewGame() {
		return menuItemNewGame;
	}

	public JMenuItem getMenuItemMainMenu() {
		return menuItemMainMenu;
	}

	public JMenuItem getMenuItemNextLookAndFeel() {
		return menuItemNextLookAndFeel;
	}

	public ImageIcon getHitIcon() {
		return hitIcon;
	}

	public ImageIcon getDestroyedIcon() {
		return destroyedIcon;
	}

	public ImageIcon getMissedIcon() {
		return missedIcon;
	}

	public ImageIcon getShipIcon() {
		return shipIcon;
	}

	/**
	 * Scales the button according to the current fieldButton-size.
	 * 
	 * @param icon
	 * @return
	 */
	private ImageIcon getScaledIcon(ImageIcon icon) {
		return new ImageIcon(icon.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH));
	}

	// ////////////////////////////////////////////////////////////////
	// methoden
	// ////////////////////////////////////////////////////////////////

	public void showPanel(String card) {
		CardLayout c1 = (CardLayout) cards.getLayout();
		c1.show(cards, card);
	}
}

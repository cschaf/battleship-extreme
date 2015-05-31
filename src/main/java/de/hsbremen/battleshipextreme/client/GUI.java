package de.hsbremen.battleshipextreme.client;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.model.Game;
import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.exception.ShipAlreadyPlacedException;
import de.hsbremen.battleshipextreme.model.exception.ShipOutOfBoardException;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.model.ship.ShipType;

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

	private Game game;
	private Controller controller;

	public GUI(Controller controller, Game game) {
		this.controller = controller;
		this.game = game;
	}

	public void createView() {
		frame = new JFrame("Battleship-Extreme");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setResizable(false);
		frame.setJMenuBar(createMenuBar());

		initComponents();

		frame.pack();
		// frame.setMinimumSize(new Dimension(1500, 640));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void initComponents() {
		panelMainMenu = new MainMenuPanel();
		panelSettings = new SettingsPanel();
		panelGame = new GamePanel();

		cards = new JPanel(new CardLayout());
		cards.add(panelMainMenu, MAIN_MENU_PANEL);
		cards.add(panelSettings, SETTINGS_PANEL);
		cards.add(panelGame, GAME_PANEL);

		frame.add(cards);

		panelSettings.getTextFieldPlayers().setText("2");
		panelSettings.getTextFieldDestroyers().setText("1");
		panelSettings.getTextFieldFrigates().setText("1");
		panelSettings.getTextFieldCorvettes().setText("1");
		panelSettings.getTextFieldSubmarines().setText("1");
		panelSettings.getTextFieldBoardSize().setText("10");
	}

	public void createMenuControls() {
		menuItemQuitGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		panelMainMenu.getButtonLocalGame().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showPanel(GUI.SETTINGS_PANEL);
			}
		});

		panelSettings.getButtonApplySettings().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int players = Integer.parseInt(panelSettings.getTextFieldPlayers().getText());
				int aiPlayers = 0;
				int dumbAiPlayers = 0;
				int boardSize = Integer.parseInt(panelSettings.getTextFieldBoardSize().getText());
				int destroyers = Integer.parseInt(panelSettings.getTextFieldDestroyers().getText());
				int frigates = Integer.parseInt(panelSettings.getTextFieldFrigates().getText());
				int corvettes = Integer.parseInt(panelSettings.getTextFieldCorvettes().getText());
				int submarines = Integer.parseInt(panelSettings.getTextFieldSubmarines().getText());
				Settings settings = new Settings(players, aiPlayers, dumbAiPlayers, boardSize, destroyers, frigates, corvettes, submarines);

				controller.initializeGame(settings);
			}
		});
	}

	public void createGameControl(int boardSize) {
		panelGame.getPanelPlayerBoard().createBoardPanel(boardSize);
		panelGame.getPanelEnemyBoard().createBoardPanel(boardSize);
		frame.pack();

		createPlayerBoardControl();
		createEnemyBoardControl();
		createShipSelectionControl();
		createEnemySelectionControl();

		updateEnemyBoard();
		updatePlayerBoard();
		updateGamePanel();
	}

	private void createPlayerBoardControl() {
		JButton[][] playerBoard = panelGame.getPanelPlayerBoard().getButtonsField();
		for (int i = 0; i < playerBoard.length; i++) {
			for (int j = 0; j < playerBoard.length; j++) {
				playerBoard[i][j].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						FieldButton fieldButton = (FieldButton) e.getSource();
						try {
							controller.placeShip(fieldButton.getxPos(), fieldButton.getyPos(), panelGame.getRadioButtonHorizontalOrientation().isSelected());
						} catch (ShipAlreadyPlacedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FieldOutOfBoardException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ShipOutOfBoardException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
			}
		}
	}

	private void createEnemyBoardControl() {
		JButton[][] playerBoard = panelGame.getPanelEnemyBoard().getButtonsField();
		for (int i = 0; i < playerBoard.length; i++) {
			for (int j = 0; j < playerBoard.length; j++) {
				playerBoard[i][j].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						FieldButton fieldButton = (FieldButton) e.getSource();
						try {
							boolean turnDone = controller.makeTurn(panelGame.getComboBoxEnemySelection().getSelectedItem() + "", fieldButton.getxPos(), fieldButton.getyPos(), panelGame
									.getRadioButtonHorizontalOrientation().isSelected());
							if (turnDone) {
							}
						} catch (FieldOutOfBoardException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				});
			}
		}
	}

	private void createShipSelectionControl() {
		panelGame.getRadioButtonDestroyer().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.selectShip(ShipType.DESTROYER);
			}
		});
		panelGame.getRadioButtonFrigate().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.selectShip(ShipType.FRIGATE);
			}
		});
		panelGame.getRadioButtonCorvette().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.selectShip(ShipType.CORVETTE);
			}
		});
		panelGame.getRadioButtonSubmarine().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.selectShip(ShipType.SUBMARINE);
			}
		});

	}

	private void createEnemySelectionControl() {
		final JComboBox<String> enemyComboBox = panelGame.getComboBoxEnemySelection();
		enemyComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateEnemyBoard();
			}
		});
	}

	public void updateGamePanel() {
		panelGame.getLabelDestroyerShipCount().setText("" + game.getCurrentPlayer().getShipCount(ShipType.DESTROYER));
		panelGame.getLabelFrigateShipCount().setText("" + game.getCurrentPlayer().getShipCount(ShipType.FRIGATE));
		panelGame.getLabelCorvetteShipCount().setText("" + game.getCurrentPlayer().getShipCount(ShipType.CORVETTE));
		panelGame.getLabelSubmarineShipCount().setText("" + game.getCurrentPlayer().getShipCount(ShipType.SUBMARINE));

		panelGame.getLabelInfo().setText(game.getCurrentPlayer() + " ist an der Reihe " + (game.getState()));
	}

	public void updateEnemySelection() {
		ArrayList<Player> enemies = game.getEnemiesOfCurrentPlayer();
		JComboBox<String> enemyComboBox = panelGame.getComboBoxEnemySelection();
		enemyComboBox.removeAllItems();
		for (Player enemy : enemies) {
			enemyComboBox.addItem(enemy.getName());
		}
	}

	public void updatePlayerBoard() {
		JButton[][] board;
		FieldState[][] fieldStates = null;
		board = panelGame.getPanelPlayerBoard().getButtonsField();
		try {
			fieldStates = game.getCurrentPlayer().getFieldStates(true);
			updateBoardColors(board, fieldStates);
		} catch (FieldOutOfBoardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateEnemyBoard() {
		JButton[][] board;
		FieldState[][] fieldStates = null;
		board = panelGame.getPanelEnemyBoard().getButtonsField();
		Player enemy = game.getPlayerByName("" + panelGame.getComboBoxEnemySelection().getSelectedItem());
		try {
			if (enemy != null) {
				fieldStates = enemy.getFieldStates(false);
				System.out.println("UPDATE ENEMY");
				updateBoardColors(board, fieldStates);
			}
		} catch (FieldOutOfBoardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void updateBoardColors(JButton[][] board, FieldState[][] fieldStates) {
		int boardSize = fieldStates.length;
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				FieldState f = fieldStates[i][j];
				if (f != null) {
					switch (fieldStates[i][j]) {
					case DESTROYED:
						board[i][j].setBackground(Color.GREEN);
						break;
					case HIT:
						board[i][j].setBackground(Color.YELLOW);
						break;
					case MISSED:
						board[i][j].setBackground(Color.RED);
						break;
					case HAS_SHIP:
						board[i][j].setBackground(Color.BLACK);
						break;
					case IS_EMPTY:
						board[i][j].setBackground(Color.WHITE);
					default:
						break;
					}
				} else {
					board[i][j].setBackground(Color.DARK_GRAY);
				}
			}
		}
	}

	// ////////////////////////////////////////////////////////////////
	// create Menu
	// ////////////////////////////////////////////////////////////////

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

	// ////////////////////////////////////////////////////////////////
	// methoden
	// ////////////////////////////////////////////////////////////////

	public void showPanel(String card) {
		CardLayout c1 = (CardLayout) cards.getLayout();
		c1.show(cards, card);
	}

}

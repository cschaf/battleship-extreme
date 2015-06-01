package de.hsbremen.battleshipextreme.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;

import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.model.Game;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.model.exception.BoardTooSmallException;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.exception.InvalidNumberOfShipsException;
import de.hsbremen.battleshipextreme.model.exception.InvalidPlayerNumberException;
import de.hsbremen.battleshipextreme.model.exception.ShipAlreadyPlacedException;
import de.hsbremen.battleshipextreme.model.exception.ShipOutOfBoardException;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.model.ship.ShipType;

public class Controller {

	private Game game;
	private GUI gui;

	public Controller(Game game, GUI gui) {
		this.game = game;
		this.gui = gui;
		createMenuControls();
	}

	private void initializeGame(Settings settings) {
		game.initialize(settings);
		createBoardPanels(settings.getBoardSize());
		gui.showPanel(GUI.GAME_PANEL);
		setEnemyBoardEnabled(false);
		updateEnemyBoard();
		updatePlayerBoard();
		updateShipSelection();
		setShipSelectionEnabled(false);
		gui.getPanelGame().getLabelInfo().setText(game.getCurrentPlayer() + " is placing ships ");
	}

	private void placeShip(int xPos, int yPos, boolean isHorizontal) throws ShipAlreadyPlacedException, FieldOutOfBoardException, ShipOutOfBoardException {
		Orientation orientation = isHorizontal ? Orientation.HORIZONTAL : Orientation.VERTICAL;
		Player currentPlayer = game.getCurrentPlayer();

		boolean possible = currentPlayer.placeShip(xPos, yPos, orientation);
		if (possible)
			currentPlayer.nextShip();

		if (currentPlayer.hasPlacedAllShips()) {
			setPlayerBoardEnabled(false);
		}

		if (game.isReady()) {
			setPlayerBoardEnabled(false);
			setEnemyBoardEnabled(true);
			updateEnemySelection();
		}

		updatePlayerBoard();
		updateShipSelection();
	}

	private boolean selectShip(ShipType shipType) {
		return game.getCurrentPlayer().setCurrentShipByType(shipType);
	}

	private boolean makeTurn(String enemyName, int xPos, int yPos, boolean isHorizontal) throws FieldOutOfBoardException {
		Orientation orientation = isHorizontal ? Orientation.HORIZONTAL : Orientation.VERTICAL;
		boolean possible = false;

		Player enemy = game.getPlayerByName(enemyName);
		possible = game.makeTurn(enemy, xPos, yPos, orientation);
		updateEnemyBoard();
		if (possible) {
			setEnemyBoardEnabled(false);
		}

		if (game.isGameover()) {
			gui.getPanelGame().getLabelInfo().setText(game.getWinner() + " won ");
		}
		return possible;
	}

	private void done() {
		if (!game.isGameover()) {
			if (!game.isReady()) {
				if (game.getCurrentPlayer().hasPlacedAllShips()) {
					game.nextPlayer();
					gui.getPanelGame().getLabelInfo().setText(game.getCurrentPlayer() + " is placing ships");
					setPlayerBoardEnabled(true);
				}
			} else {
				game.nextPlayer();
				if (game.getCurrentPlayer().areAllShipsReloading()) {
					gui.getPanelGame().getLabelInfo().setText("All ships of " + game.getCurrentPlayer() + " are reloading");
					setEnemyBoardEnabled(false);
					setShipSelectionEnabled(false);
				} else {
					gui.getPanelGame().getLabelInfo().setText(game.getCurrentPlayer() + " is shooting");
					enableAvailableShips();
					selectFirstAvailableShipType();
					setEnemyBoardEnabled(true);
				}
			}
			updatePlayerBoard();
			updateShipSelection();
			updateEnemySelection();
		}
	}

	private void selectFirstAvailableShipType() {
		ShipType availableShipType = game.getCurrentPlayer().getTypeOFirstAvailableShip();
		switch (availableShipType) {
		case DESTROYER:
			gui.getPanelGame().getRadioButtonDestroyer().setSelected(true);
			break;
		case CORVETTE:
			gui.getPanelGame().getRadioButtonCorvette().setSelected(true);
			break;
		case FRIGATE:
			gui.getPanelGame().getRadioButtonFrigate().setSelected(true);
			break;
		case SUBMARINE:
			gui.getPanelGame().getRadioButtonSubmarine().setSelected(true);
		default:
			break;
		}
		selectShip(availableShipType);
	}

	private void createMenuControls() {
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
				SettingsPanel panelSettings = gui.getPanelSettings();
				int players = Integer.parseInt(panelSettings.getTextFieldPlayers().getText());
				int aiPlayers = 0;
				int dumbAiPlayers = 0;
				int boardSize = Integer.parseInt(panelSettings.getTextFieldBoardSize().getText());
				int destroyers = Integer.parseInt(panelSettings.getTextFieldDestroyers().getText());
				int frigates = Integer.parseInt(panelSettings.getTextFieldFrigates().getText());
				int corvettes = Integer.parseInt(panelSettings.getTextFieldCorvettes().getText());
				int submarines = Integer.parseInt(panelSettings.getTextFieldSubmarines().getText());
				Settings settings = new Settings(players, aiPlayers, dumbAiPlayers, boardSize, destroyers, frigates, corvettes, submarines);
				try {
					settings.validate();
					initializeGame(settings);
				} catch (InvalidPlayerNumberException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InvalidNumberOfShipsException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (BoardTooSmallException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
	}

	private void createBoardPanels(int boardSize) {
		GamePanel panelGame = gui.getPanelGame();
		panelGame.getPanelPlayerBoard().createBoardPanel(boardSize);
		panelGame.getPanelEnemyBoard().createBoardPanel(boardSize);
		gui.getFrame().pack();

		addPlayerBoardListener();
		addEnemyBoardListener();
		addShipSelectionListeners();
		addEnemySelectionListener();
		addDoneButtonListener();
	}

	private void addPlayerBoardListener() {
		final GamePanel panelGame = gui.getPanelGame();
		JButton[][] playerBoard = panelGame.getPanelPlayerBoard().getButtonsField();
		for (int i = 0; i < playerBoard.length; i++) {
			for (int j = 0; j < playerBoard.length; j++) {
				playerBoard[i][j].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						FieldButton fieldButton = (FieldButton) e.getSource();
						try {
							placeShip(fieldButton.getxPos(), fieldButton.getyPos(), panelGame.getRadioButtonHorizontalOrientation().isSelected());
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

	private void addEnemyBoardListener() {
		final GamePanel panelGame = gui.getPanelGame();
		JButton[][] playerBoard = panelGame.getPanelEnemyBoard().getButtonsField();
		for (int i = 0; i < playerBoard.length; i++) {
			for (int j = 0; j < playerBoard.length; j++) {
				playerBoard[i][j].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						FieldButton fieldButton = (FieldButton) e.getSource();
						try {
							makeTurn(panelGame.getComboBoxEnemySelection().getSelectedItem() + "", fieldButton.getxPos(), fieldButton.getyPos(), panelGame.getRadioButtonHorizontalOrientation()
									.isSelected());
						} catch (FieldOutOfBoardException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				});
			}
		}
	}

	private void addShipSelectionListeners() {
		gui.getPanelGame().getRadioButtonDestroyer().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectShip(ShipType.DESTROYER);
			}
		});
		gui.getPanelGame().getRadioButtonFrigate().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectShip(ShipType.FRIGATE);
			}
		});
		gui.getPanelGame().getRadioButtonCorvette().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectShip(ShipType.CORVETTE);
			}
		});
		gui.getPanelGame().getRadioButtonSubmarine().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectShip(ShipType.SUBMARINE);
			}
		});

	}

	private void addEnemySelectionListener() {
		GamePanel panelGame = gui.getPanelGame();
		final JComboBox<String> enemyComboBox = panelGame.getComboBoxEnemySelection();
		enemyComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateEnemyBoard();
			}
		});
	}

	private void addDoneButtonListener() {
		GamePanel panelGame = gui.getPanelGame();
		panelGame.getDoneButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				done();
			}
		});

	}

	private void updateShipSelection() {
		GamePanel panelGame = gui.getPanelGame();
		panelGame.getLabelDestroyerShipCount().setText("" + game.getCurrentPlayer().getShipCount(ShipType.DESTROYER));
		panelGame.getLabelFrigateShipCount().setText("" + game.getCurrentPlayer().getShipCount(ShipType.FRIGATE));
		panelGame.getLabelCorvetteShipCount().setText("" + game.getCurrentPlayer().getShipCount(ShipType.CORVETTE));
		panelGame.getLabelSubmarineShipCount().setText("" + game.getCurrentPlayer().getShipCount(ShipType.SUBMARINE));
	}

	private void updateEnemySelection() {
		GamePanel panelGame = gui.getPanelGame();
		ArrayList<Player> enemies = game.getEnemiesOfCurrentPlayer();
		JComboBox<String> enemyComboBox = panelGame.getComboBoxEnemySelection();
		enemyComboBox.removeAllItems();
		for (Player enemy : enemies) {
			enemyComboBox.addItem(enemy.getName());
		}
	}

	private void updatePlayerBoard() {
		GamePanel panelGame = gui.getPanelGame();
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

	private void updateEnemyBoard() {
		GamePanel panelGame = gui.getPanelGame();
		JButton[][] board;
		FieldState[][] fieldStates = null;
		board = panelGame.getPanelEnemyBoard().getButtonsField();
		Player enemy = game.getPlayerByName("" + panelGame.getComboBoxEnemySelection().getSelectedItem());
		try {
			if (enemy != null) {
				fieldStates = enemy.getFieldStates(false);
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
						board[i][j].setBackground(GUI.DESTROYED_COLOR);
						break;
					case HIT:
						board[i][j].setBackground(GUI.HIT_COLOR);
						break;
					case MISSED:
						board[i][j].setBackground(GUI.MISSED_COLOR);
						break;
					case HAS_SHIP:
						board[i][j].setBackground(GUI.HAS_SHIP_COLOR);
						break;
					case IS_EMPTY:
						board[i][j].setBackground(GUI.EMPTY_COLOR);
					default:
						break;
					}
				} else {
					board[i][j].setBackground(GUI.UNKNOWN_COLOR);
				}
			}
		}
	}

	private void setBoardEnabled(JButton[][] board, boolean enabled) {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				board[i][j].setEnabled(enabled);
			}
		}
	}

	private void setPlayerBoardEnabled(boolean enabled) {
		setBoardEnabled(gui.getPanelGame().getPanelPlayerBoard().getButtonsField(), enabled);
	}

	private void setEnemyBoardEnabled(boolean enabled) {
		setBoardEnabled(gui.getPanelGame().getPanelEnemyBoard().getButtonsField(), enabled);
	}

	private void enableAvailableShips() {
		GamePanel panelGame = gui.getPanelGame();
		Player currentPlayer = game.getCurrentPlayer();
		panelGame.getRadioButtonDestroyer().setEnabled(currentPlayer.isShipOfTypeAvailable(ShipType.DESTROYER));
		panelGame.getRadioButtonFrigate().setEnabled(currentPlayer.isShipOfTypeAvailable(ShipType.FRIGATE));
		panelGame.getRadioButtonCorvette().setEnabled(currentPlayer.isShipOfTypeAvailable(ShipType.CORVETTE));
		panelGame.getRadioButtonSubmarine().setEnabled(currentPlayer.isShipOfTypeAvailable(ShipType.SUBMARINE));
	}

	private void setShipSelectionEnabled(boolean enabled) {
		GamePanel panelGame = gui.getPanelGame();
		panelGame.getRadioButtonDestroyer().setEnabled(enabled);
		panelGame.getRadioButtonFrigate().setEnabled(enabled);
		panelGame.getRadioButtonCorvette().setEnabled(enabled);
		panelGame.getRadioButtonSubmarine().setEnabled(enabled);
	}

}

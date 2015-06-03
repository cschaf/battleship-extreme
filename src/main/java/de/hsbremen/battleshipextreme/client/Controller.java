package de.hsbremen.battleshipextreme.client;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

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
import de.hsbremen.battleshipextreme.model.network.IServerObjectReceivedListener;
import de.hsbremen.battleshipextreme.model.network.NetworkClient;
import de.hsbremen.battleshipextreme.model.player.AIPlayer;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.model.player.PlayerType;
import de.hsbremen.battleshipextreme.model.ship.ShipType;
import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.eventhandling.listener.IErrorListener;
import de.hsbremen.battleshipextreme.network.transfarableObject.ClientInfo;
import de.hsbremen.battleshipextreme.network.transfarableObject.GameList;
import de.hsbremen.battleshipextreme.network.transfarableObject.Message;
import de.hsbremen.battleshipextreme.network.transfarableObject.NetGame;
import de.hsbremen.battleshipextreme.network.transfarableObject.ServerInfo;
import de.hsbremen.battleshipextreme.network.transfarableObject.Turn;

public class Controller {

	private Game game;
	private GUI gui;
	private NetworkClient network;

	public Controller(Game game, NetworkClient network, GUI gui) {
		this.game = game;
		this.network = network;
		this.gui = gui;
		addMenuListeners();
		addServerConnectionListener();
		// addNetworkListeners();
		addServerGameBrowserListeners();
	}

	private void initializeGame(Settings settings) throws Exception {
		if (settings != null) {
			game.initialize(settings);
		}

		initializeGameView();

		// wenn erster Spieler AI ist, automatisch anfangen
		if (game.getCurrentPlayer().getType() == PlayerType.SMART_AI) {
			placeAiShips();
		}
	}

	private void loadGame() {
		try {
			game.load(Settings.SAVEGAME_FILENAME);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		initializeGameView();

		boolean hasMadeTurn = game.hasCurrentPlayerMadeTurn();
		setPlayerBoardEnabled(false);
		setEnemySelectionEnabled(true);
		setEnemyBoardEnabled(!hasMadeTurn);
		setDoneButtonEnabled(hasMadeTurn);
		String message = hasMadeTurn ? game.getCurrentPlayer() + " has made his Turn" : game.getCurrentPlayer() + " is shooting";
		setInfoLabelMessage(message);

	}

	private void initializeGameView() {
		createBoardPanels(game.getBoardSize());
		gui.showPanel(GUI.GAME_PANEL);
		updateEnemyBoard();
		updatePlayerBoard();
		updateEnemySelection();
		setEnemySelectionEnabled(false);
		setEnemyBoardEnabled(false);
		setShipSelectionEnabled(false);
		setDoneButtonEnabled(false);
		setInfoLabelMessage(game.getCurrentPlayer() + " is placing ships ");
	}

	private void addMenuListeners() {
		gui.getMenuItemMainMenu().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.showPanel(GUI.MAIN_MENU_PANEL);
				setSaveButtonEnabled(false);
			}
		});

		gui.getMenuItemNewGame().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.showPanel(GUI.SETTINGS_PANEL);
				setSaveButtonEnabled(false);
			}
		});

		gui.getMenuItemSaveGame().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					game.save(Settings.SAVEGAME_FILENAME);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		gui.getMenuItemLoadGame().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					loadGame();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

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

		gui.getPanelMainMenu().getButtonLoadGame().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					loadGame();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		gui.getPanelMainMenu().getButtonMultiplayerGame().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.showPanel(GUI.SERVER_CONNECTION_PANEL);
			}
		});

		addApplySettingsListener();
		addShipSelectionListeners();
		addEnemySelectionListener();
		addDoneButtonListener();
	}

	private void createBoardPanels(int boardSize) {
		GamePanel panelGame = gui.getPanelGame();
		if (panelGame.getPanelPlayerBoard().getComponentCount() > 0) {
			panelGame.getPanelPlayerBoard().removeAll();
			panelGame.getPanelEnemyBoard().removeAll();
		}
		panelGame.getPanelPlayerBoard().initializeBoardPanel("You", boardSize);
		panelGame.getPanelEnemyBoard().initializeBoardPanel("Enemy", boardSize);
		gui.getFrame().pack();
		addPlayerBoardListener();
		addEnemyBoardListener();
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
							setInfoLabelMessage("Ship can not be placed here");
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

	private void addApplySettingsListener() {
		gui.getPanelSettings().getButtonApplySettings().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SettingsPanel panelSettings = gui.getPanelSettings();
				int players = Integer.parseInt(panelSettings.getTextFieldPlayers().getText());
				int aiPlayers = Integer.parseInt(panelSettings.getTextFieldAiPlayers().getText());
				int dumbAiPlayers = 0;
				int boardSize = Integer.parseInt(panelSettings.getTextFieldBoardSize().getText());
				int destroyers = Integer.parseInt(panelSettings.getTextFieldDestroyers().getText());
				int frigates = Integer.parseInt(panelSettings.getTextFieldFrigates().getText());
				int corvettes = Integer.parseInt(panelSettings.getTextFieldCorvettes().getText());
				int submarines = Integer.parseInt(panelSettings.getTextFieldSubmarines().getText());
				Settings settings = new Settings(players, aiPlayers, dumbAiPlayers, boardSize, destroyers, frigates, corvettes, submarines);
				boolean valid = false;
				try {
					settings.validate();
					valid = true;
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
				if (valid) {
					try {
						initializeGame(settings);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
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
		panelGame.getButtonDone().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				next();
			}
		});
	}

	private void addNetworkListeners() {
		network.addErrorListener(new IErrorListener() {
			public void onError(EventArgs<ITransferable> eventArgs) {
				JOptionPane.showMessageDialog(gui.getFrame(), eventArgs.getItem(), "Error", JOptionPane.ERROR_MESSAGE);
				gui.getPanelServerConnection().getPnlServerConnectionBar().setEnabledAfterStartStop(true);
				gui.getPanelServerConnection().getPnlServerGameBrowser().getTblModel().removeAllGames();
			}
		});

		network.addServerObjectReceivedListener(new IServerObjectReceivedListener() {
			public void onObjectReceived(EventArgs<ITransferable> eventArgs) {
				JOptionPane.showMessageDialog(gui.getFrame(), eventArgs.getItem(), "Info", JOptionPane.INFORMATION_MESSAGE);
			}

			public void onMessageObjectReceived(EventArgs<Message> eventArgs) {

			}

			public void onClientInfoObjectReceived(EventArgs<ClientInfo> eventArgs) {
				switch (eventArgs.getItem().getReason()) {
				case Connect:
					break;
				case Disconnect:

					break;
				}
			}

			public void onGameObjectReceived(EventArgs<NetGame> eventArgs) {

			}

			public void onTurnObjectReceived(EventArgs<Turn> eventArgs) {

			}

			public void onGameListObjectReceived(EventArgs<GameList> eventArgs) {
				gui.getPanelServerConnection().getPnlServerGameBrowser().getTblModel().removeAllGames();
				for (NetGame game : eventArgs.getItem().getNetGameList()) {
					gui.getPanelServerConnection().getPnlServerGameBrowser().addGameToTable(game);
				}
			}

			public void onServerInfoObjectReceived(EventArgs<ServerInfo> eventArgs) {
				ServerInfo info = eventArgs.getItem();
				switch (info.getReason()) {
				case Connect:
					gui.getPanelServerConnection().getPnlServerConnectionBar().setEnabledAfterStartStop(false);
					network.getSender().requestGameList();
					resizeServerGameListColumns();
					break;
				}
			}
		});
	}

	private void selectShip(ShipType shipType) {
		game.getCurrentPlayer().setCurrentShipByType(shipType);
	}

	private void placeShip(int xPos, int yPos, boolean isHorizontal) throws ShipAlreadyPlacedException, FieldOutOfBoardException, ShipOutOfBoardException {
		Orientation orientation = isHorizontal ? Orientation.HORIZONTAL : Orientation.VERTICAL;
		Player currentPlayer = game.getCurrentPlayer();

		boolean possible = currentPlayer.placeShip(xPos, yPos, orientation);
		if (possible) {
			currentPlayer.nextShip();
		}

		if (currentPlayer.hasPlacedAllShips()) {
			setPlayerBoardEnabled(false);
			setDoneButtonEnabled(true);
			setInfoLabelMessage(game.getCurrentPlayer() + " placed all ships");
		}

		updatePlayerBoard();
		updateShipSelection();
	}

	private boolean makeTurn(String enemyName, int xPos, int yPos, boolean isHorizontal) throws FieldOutOfBoardException {
		Orientation orientation = isHorizontal ? Orientation.HORIZONTAL : Orientation.VERTICAL;
		boolean possible = false;

		Player enemy = game.getPlayerByName(enemyName);
		possible = game.makeTurn(enemy, xPos, yPos, orientation);
		if (possible) {
			updateEnemyBoard();
			setEnemyBoardEnabled(false);
			setDoneButtonEnabled(true);
			setInfoLabelMessage(game.getCurrentPlayer() + " attacked " + enemy);
		}
		return possible;
	}

	private void next() {
		if (!game.isGameover()) {
			if (!game.isReady()) {
				if (game.getCurrentPlayer().hasPlacedAllShips()) {
					game.nextPlayer();
					setInfoLabelMessage(game.getCurrentPlayer() + " is placing ships");
					if (game.getCurrentPlayer().getType() == PlayerType.SMART_AI) {
						placeAiShips();
					} else {
						setPlayerBoardEnabled(true);
						setDoneButtonEnabled(false);
					}
				}
			} else {
				setEnemySelectionEnabled(true);
				setSaveButtonEnabled(true);
				game.nextPlayer();
				updateEnemySelection();
				if (game.getCurrentPlayer().areAllShipsReloading()) {
					setInfoLabelMessage("All ships of " + game.getCurrentPlayer() + " are reloading");
					setEnemyBoardEnabled(false);
					setShipSelectionEnabled(false);
				} else {
					if (game.getCurrentPlayer().getType() == PlayerType.SMART_AI) {
						makeAiTurn();
					} else {
						setInfoLabelMessage(game.getCurrentPlayer() + " is shooting");
						enableAvailableShips();
						selectFirstAvailableShipType();
						setEnemyBoardEnabled(true);
						gui.getPanelGame().getButtonDone().setEnabled(false);
					}
				}
			}
			updatePlayerBoard();
			updateShipSelection();
		} else {
			setInfoLabelMessage(game.getWinner() + " won ");
		}
	}

	private void placeAiShips() {
		AIPlayer ai = (AIPlayer) game.getCurrentPlayer();
		try {
			ai.placeShips();
		} catch (ShipAlreadyPlacedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FieldOutOfBoardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ShipOutOfBoardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setPlayerBoardEnabled(false);
		setDoneButtonEnabled(true);
		updatePlayerBoard();
	}

	private void makeAiTurn() {
		try {
			game.makeAiTurn();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AIPlayer ai = (AIPlayer) game.getCurrentPlayer();
		Player currentEnemy = game.getPlayers()[ai.getCurrentEnemyIndex()];
		gui.getPanelGame().getComboBoxEnemySelection().setSelectedItem(currentEnemy.getName());
		setInfoLabelMessage(game.getCurrentPlayer() + " attacks " + currentEnemy);
		updateEnemyBoard();
	}

	private void setInfoLabelMessage(String message) {
		gui.getPanelGame().getLabelInfo().setText(message);
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

	private void setDoneButtonEnabled(boolean enabled) {
		gui.getPanelGame().getButtonDone().setEnabled(enabled);
	}

	private void setShipSelectionEnabled(boolean enabled) {
		GamePanel panelGame = gui.getPanelGame();
		panelGame.getRadioButtonDestroyer().setEnabled(enabled);
		panelGame.getRadioButtonFrigate().setEnabled(enabled);
		panelGame.getRadioButtonCorvette().setEnabled(enabled);
		panelGame.getRadioButtonSubmarine().setEnabled(enabled);
	}

	private void setSaveButtonEnabled(boolean enabled) {
		gui.getMenuItemSaveGame().setEnabled(enabled);
	}

	private void setEnemySelectionEnabled(boolean enabled) {
		gui.getPanelGame().getComboBoxEnemySelection().setEnabled(enabled);
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

	private void addServerConnectionListener() {

		gui.getPanelServerConnection().getPnlServerConnectionBar().getBtnConnect().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!network.isConnected()) {
					addNetworkListeners();
					network.connect();
					network.getSender().sendLogin(gui.getPanelServerConnection().getPnlServerConnectionBar().getTbxUsername().getText());
				}
			}
		});

		gui.getPanelServerConnection().getPnlServerConnectionBar().getBtnDisconnect().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (network.isConnected()) {
					// TODO logoff
					network.disconnect();
				}
			}
		});

		gui.getPanelServerConnection().getPnlServerGameBrowser().getBtnRefresh().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				network.getSender().requestGameList();
				resizeServerGameListColumns();
			}
		});

		gui.getPanelServerConnection().getPnlServerGameBrowser().getBtnBack().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.showPanel(GUI.MAIN_MENU_PANEL);
			}
		});

		gui.getPanelServerConnection().getPnlServerGameBrowser().getBtnCreate().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.showPanel(GUI.SETTINGS_PANEL);
			}
		});
	}

	private void addServerGameBrowserListeners() {
		gui.getPanelServerConnection().getPnlServerGameBrowser().getTblGames().getColumnModel().addColumnModelListener(new TableColumnModelListener() {
			public void columnAdded(TableColumnModelEvent e) {
			}

			public void columnRemoved(TableColumnModelEvent e) {
			}

			public void columnMoved(TableColumnModelEvent e) {

			}

			public void columnMarginChanged(ChangeEvent e) {
				resizeServerGameListColumns();
			}

			public void columnSelectionChanged(ListSelectionEvent e) {

			}
		});
	}

	private void resizeServerGameListColumns() {
		JTable tbl = gui.getPanelServerConnection().getPnlServerGameBrowser().getTblGames();
		Dimension tableSize = tbl.getSize();
		tbl.getColumn("Name").setWidth(Math.round((tableSize.width - 195)));
		tbl.getColumn("Player").setWidth(43);
		tbl.getColumn("Created at").setWidth(127);
		tbl.getColumn("PW").setWidth(25);
	}
}

package de.hsbremen.battleshipextreme.client;

import de.hsbremen.battleshipextreme.client.workers.BoardUpdater;
import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.model.Game;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.model.exception.*;
import de.hsbremen.battleshipextreme.model.player.AIPlayer;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.model.player.PlayerType;
import de.hsbremen.battleshipextreme.model.player.Target;
import de.hsbremen.battleshipextreme.model.ship.Ship;
import de.hsbremen.battleshipextreme.model.ship.ShipType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by cschaf on 17.06.2015.
 */
public class LocalClientController implements Serializable {
// ------------------------------ FIELDS ------------------------------

    private Game game;
    private GUI gui;
    private Controller ctrl;
    private ActionListener doneButtonListener;
    private ActionListener enemySelectionListener;
    private ActionListener[][] enemyBoardListeners;
    private ActionListener[][] playerBoardListeners;
    private ActionListener applaySettingsListener;
    private ActionListener showYourShipsListener;
    private ActionListener[] shipSelectionListeners;
    private ActionListener[] menuListeners;

// --------------------------- CONSTRUCTORS ---------------------------

    public LocalClientController(Game game, GUI gui, Controller ctrl) {
        this.game = game;
        this.gui = gui;
        this.ctrl = ctrl;
    }

// -------------------------- OTHER METHODS --------------------------

    public void addAllListeners() {
        addMenuListeners();
        addDoneButtonListener();
        addApplySettingsListener();
        addShipSelectionListeners();
        addEnemySelectionListener();
        addShowYourShipsButtonListener();
        gui.getPanelGame().getButtonShowYourShips().setVisible(true);
    }

    public void addApplySettingsListener() {
        applaySettingsListener = new ActionListener() {
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
        };
        gui.getPanelSettings().getButtonApplySettings().addActionListener(applaySettingsListener);
    }

    public void initializeGame(Settings settings) throws Exception {
        if (settings != null) {
            game.initialize(settings);
        }

        initializeGameView();

        // wenn erster Spieler AI ist, automatisch anfangen
        if (game.getCurrentPlayer().getType() == PlayerType.SMART_AI) {
            placeAiShips();
        }
    }

    private void initializeGameView() {
        ctrl.createBoardPanels(game.getBoardSize());
        addPlayerBoardListener();
        addEnemyBoardListener();
        gui.showPanel(GUI.GAME_PANEL);
        updateEnemyBoard();
        updatePlayerBoard();
        updateEnemySelection();
        ctrl.setEnemySelectionEnabled(false);
        ctrl.setEnemyBoardEnabled(false);
        ctrl.setShipSelectionEnabled(false);
        ctrl.setDoneButtonEnabled(false);
        ctrl.setInfoLabelMessage(game.getCurrentPlayer() + " is placing ships ");
    }

    public void addEnemyBoardListener() {
        final GamePanel panelGame = gui.getPanelGame();
        JButton[][] playerBoard = panelGame.getPanelEnemyBoard().getButtonsField();
        enemyBoardListeners = new ActionListener[playerBoard.length][playerBoard.length];
        for (int i = 0; i < playerBoard.length; i++) {
            for (int j = 0; j < playerBoard.length; j++) {
                ActionListener fieldListener = new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        FieldButton fieldButton = (FieldButton) e.getSource();
                        String attackingPlayerName = game.getCurrentPlayer().getName();
                        String attackedPlayerName = panelGame.getComboBoxEnemySelection().getSelectedItem().toString();
                        int xPos = fieldButton.getxPos();
                        int yPos = fieldButton.getyPos();
                        boolean isHorizontal = panelGame.getRadioButtonHorizontalOrientation().isSelected();
                        Ship currentShip = game.getCurrentPlayer().getCurrentShip();
                        String orientation = isHorizontal ? "horizontal" : "vertical";
                        try {
                            boolean turnMade = makeTurn(attackedPlayerName, xPos, yPos, isHorizontal);
                            if (turnMade) {
                                ctrl.appendGameLogEntry("Player " + attackingPlayerName + " attacked " + attackedPlayerName + " " + orientation + " with ship " + currentShip.getType().toString() + " at start Field X:" + xPos + "  Y: " + yPos);
                            }
                        } catch (FieldOutOfBoardException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                };
                enemyBoardListeners[i][j] = fieldListener;
                playerBoard[i][j].addActionListener(fieldListener);
            }
        }
    }

    public boolean makeTurn(String enemyName, int xPos, int yPos, boolean isHorizontal) throws FieldOutOfBoardException {
        Orientation orientation = isHorizontal ? Orientation.HORIZONTAL : Orientation.VERTICAL;
        boolean possible = false;

        Player enemy = game.getPlayerByName(enemyName);
        possible = game.makeTurn(enemy, xPos, yPos, orientation);
        if (possible) {
            updateEnemyBoard();
            ctrl.setEnemyBoardEnabled(false);
            ctrl.setDoneButtonEnabled(true);
            ctrl.setInfoLabelMessage(game.getCurrentPlayer() + " attacked " + enemy);
        }
        return possible;
    }

    public void addPlayerBoardListener() {
        final GamePanel panelGame = gui.getPanelGame();
        JButton[][] playerBoard = panelGame.getPanelPlayerBoard().getButtonsField();
        playerBoardListeners = new ActionListener[playerBoard.length][playerBoard.length];
        for (int i = 0; i < playerBoard.length; i++) {
            for (int j = 0; j < playerBoard.length; j++) {
                ActionListener fieldListener = new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        FieldButton fieldButton = (FieldButton) e.getSource();
                        try {
                            placeShip(fieldButton.getxPos(), fieldButton.getyPos(), panelGame.getRadioButtonHorizontalOrientation().isSelected());
                        } catch (ShipAlreadyPlacedException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (FieldOutOfBoardException e1) {
                            ctrl.setInfoLabelMessage("Ship can not be placed here");
                            e1.printStackTrace();
                        } catch (ShipOutOfBoardException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                };
                playerBoardListeners[i][j] = fieldListener;
                playerBoard[i][j].addActionListener(fieldListener);
            }
        }
    }

    private void placeShip(int xPos, int yPos, boolean isHorizontal) throws ShipAlreadyPlacedException, FieldOutOfBoardException, ShipOutOfBoardException {
        Orientation orientation = isHorizontal ? Orientation.HORIZONTAL : Orientation.VERTICAL;
        Player currentPlayer = game.getCurrentPlayer();

        boolean possible = currentPlayer.placeShip(xPos, yPos, orientation);
        if (possible) {
            currentPlayer.nextShip();
        }

        if (currentPlayer.hasPlacedAllShips()) {
            ctrl.setPlayerBoardEnabled(false);
            ctrl.setDoneButtonEnabled(true);
            ctrl.setInfoLabelMessage(game.getCurrentPlayer() + " placed all ships");
        }

        updatePlayerBoard();
        ctrl.updateShipSelection(game.getCurrentPlayer());
    }

    public void updateEnemyBoard() {
        GamePanel panelGame = gui.getPanelGame();
        JButton[][] board;
        board = panelGame.getPanelEnemyBoard().getButtonsField();
        Player enemy = game.getPlayerByName("" + panelGame.getComboBoxEnemySelection().getSelectedItem());
        try {
            if (enemy != null) {
                FieldState[][] fieldStates = enemy.getFieldStates(false);
                new BoardUpdater(gui, board, fieldStates).execute();
            }
        } catch (FieldOutOfBoardException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void updateEnemySelection() {
        GamePanel panelGame = gui.getPanelGame();
        ArrayList<Player> enemies = game.getEnemiesOfCurrentPlayer();
        JComboBox<String> enemyComboBox = panelGame.getComboBoxEnemySelection();
        enemyComboBox.removeAllItems();
        for (Player enemy : enemies) {
            enemyComboBox.addItem(enemy.getName());
        }
    }

    public void updatePlayerBoard() {
        GamePanel panelGame = gui.getPanelGame();
        JButton[][] board;
        FieldState[][] fieldStates = null;
        board = panelGame.getPanelPlayerBoard().getButtonsField();
        try {
            fieldStates = game.getCurrentPlayer().getFieldStates(true);
            new BoardUpdater(gui, board, fieldStates).execute();
        } catch (FieldOutOfBoardException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        ctrl.setPlayerBoardEnabled(false);
        ctrl.setDoneButtonEnabled(true);
        showEmptyPlayerBoard(ai.getName());
    }

    public void showEmptyPlayerBoard(String playerName) {
        FieldState[][] fieldStates = game.getPlayerByName(playerName).getFieldWithStateEmpty();
        ctrl.updateBoardColors(gui.getPanelGame().getPanelPlayerBoard().getButtonsField(), fieldStates);
    }

    public void addDoneButtonListener() {
        GamePanel panelGame = gui.getPanelGame();
        doneButtonListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setShipSelectionEnabled(false);
                showEmptyPlayerBoard(game.getCurrentPlayer().getName());
                next();
            }
        };
        panelGame.getButtonDone().addActionListener(doneButtonListener);
    }

    public void next() {
        if (!game.isGameover()) {
            if (!game.isReady()) {
                if (game.getCurrentPlayer().hasPlacedAllShips()) {
                    game.nextPlayer();
                    ctrl.setInfoLabelMessage(game.getCurrentPlayer() + " is placing ships");
                    if (game.getCurrentPlayer().getType() == PlayerType.SMART_AI) {
                        placeAiShips();
                    } else {
                        ctrl.setPlayerBoardEnabled(true);
                        ctrl.setDoneButtonEnabled(false);
                    }
                }
            } else {
                gui.getPanelGame().getButtonShowYourShips().setEnabled(true);
                gui.getPanelGame().getButtonShowYourShips().setSelected(false);
                ctrl.setEnemySelectionEnabled(true);
                ctrl.setSaveButtonEnabled(true);
                game.nextPlayer();
                updateEnemySelection();
                if (game.getCurrentPlayer().areAllShipsReloading()) {
                    ctrl.setInfoLabelMessage("All ships of " + game.getCurrentPlayer() + " are reloading");
                    ctrl.setEnemyBoardEnabled(false);
                    ctrl.setShipSelectionEnabled(false);
                } else {
                    if (game.getCurrentPlayer().getType() == PlayerType.SMART_AI) {
                        gui.getPanelGame().getButtonShowYourShips().setEnabled(false);
                        makeAiTurn();
                    } else {
                        gui.getPanelGame().getButtonShowYourShips().setEnabled(true);
                        ctrl.setInfoLabelMessage(game.getCurrentPlayer() + " is shooting");
                        if (game.getCurrentPlayer().getType() == PlayerType.HUMAN) {
                            enableAvailableShips();
                        }
                        selectFirstAvailableShipType();
                        ctrl.setEnemyBoardEnabled(true);
                        gui.getPanelGame().getButtonDone().setEnabled(false);
                    }
                }
            }
            if (gui.getPanelGame().getButtonShowYourShips().isSelected() && game.getCurrentPlayer().getType() == PlayerType.HUMAN) {
                updatePlayerBoard();
            }
            ctrl.updateShipSelection(game.getCurrentPlayer());
        } else {
            ctrl.setInfoLabelMessage(game.getWinner() + " won ");
        }
    }

    public void enableAvailableShips() {
        GamePanel panelGame = gui.getPanelGame();
        Player currentPlayer = game.getCurrentPlayer();
        panelGame.getRadioButtonDestroyer().setEnabled(currentPlayer.isShipOfTypeAvailable(ShipType.DESTROYER));
        panelGame.getRadioButtonFrigate().setEnabled(currentPlayer.isShipOfTypeAvailable(ShipType.FRIGATE));
        panelGame.getRadioButtonCorvette().setEnabled(currentPlayer.isShipOfTypeAvailable(ShipType.CORVETTE));
        panelGame.getRadioButtonSubmarine().setEnabled(currentPlayer.isShipOfTypeAvailable(ShipType.SUBMARINE));
    }

    private void makeAiTurn() {
        try {
            Target shot = game.makeAiTurn();
            ctrl.appendGameLogEntry("Player " + game.getCurrentPlayer().getName() + " attacked " + gui.getPanelGame().getComboBoxEnemySelection().getSelectedItem().toString() + " " + shot.getOrientation().toString() + " with ship " + game.getCurrentPlayer().getCurrentShip().getType() + " at start Field X:" + shot.getX() + "  Y: " + shot.getY());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AIPlayer ai = (AIPlayer) game.getCurrentPlayer();
        Player currentEnemy = game.getPlayers()[ai.getCurrentEnemyIndex()];
        gui.getPanelGame().getComboBoxEnemySelection().setSelectedItem(currentEnemy.getName());
        ctrl.setInfoLabelMessage(game.getCurrentPlayer() + " attacks " + currentEnemy);
        updateEnemyBoard();
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

    public void selectShip(ShipType shipType) {
        game.getCurrentPlayer().setCurrentShipByType(shipType);
    }

    public void setShipSelectionEnabled(boolean state) {
        GamePanel panelGame = gui.getPanelGame();
        panelGame.getRadioButtonDestroyer().setEnabled(state);
        panelGame.getRadioButtonFrigate().setEnabled(state);
        panelGame.getRadioButtonCorvette().setEnabled(state);
        panelGame.getRadioButtonSubmarine().setEnabled(state);
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

    private void addMenuListeners() {
        this.menuListeners = new ActionListener[3];
        menuListeners[0] = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    game.save(Settings.SAVEGAME_FILENAME);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        };
        gui.getMenuItemSaveGame().addActionListener(menuListeners[0]);

        menuListeners[1] = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    loadGame();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        };
        gui.getMenuItemLoadGame().addActionListener(menuListeners[1]);
        menuListeners[2] = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    loadGame();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        };
        gui.getPanelMainMenu().getButtonLoadGame().addActionListener(menuListeners[2]);
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
        ctrl.setPlayerBoardEnabled(false);
        ctrl.setEnemySelectionEnabled(true);
        ctrl.setEnemyBoardEnabled(!hasMadeTurn);
        ctrl.setDoneButtonEnabled(hasMadeTurn);
        String message = hasMadeTurn ? game.getCurrentPlayer() + " has made his Turn" : game.getCurrentPlayer() + " is shooting";
        ctrl.setInfoLabelMessage(message);
    }

    private void addShipSelectionListeners() {
        this.shipSelectionListeners = new ActionListener[4];
        shipSelectionListeners[0] = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectShip(ShipType.DESTROYER);
            }
        };

        gui.getPanelGame().getRadioButtonDestroyer().addActionListener(shipSelectionListeners[0]);

        shipSelectionListeners[1] = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectShip(ShipType.FRIGATE);
            }
        };

        gui.getPanelGame().getRadioButtonFrigate().addActionListener(shipSelectionListeners[1]);

        shipSelectionListeners[2] = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectShip(ShipType.CORVETTE);
            }
        };
        gui.getPanelGame().getRadioButtonCorvette().addActionListener(shipSelectionListeners[2]);

        shipSelectionListeners[3] = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectShip(ShipType.SUBMARINE);
            }
        };

        gui.getPanelGame().getRadioButtonSubmarine().addActionListener(shipSelectionListeners[3]);
    }

    public void addShowYourShipsButtonListener() {
        GamePanel panelGame = gui.getPanelGame();
        showYourShipsListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JToggleButton btn = (JToggleButton) e.getSource();
                if (btn.isSelected()) {
                    if (game.getCurrentPlayer().getType() == PlayerType.HUMAN) {
                        updatePlayerBoard();
                    }
                } else {
                    showEmptyPlayerBoard(game.getCurrentPlayer().getName());
                }
            }
        };
        panelGame.getButtonShowYourShips().addActionListener(showYourShipsListener);
    }

    public void removeAllListeners() {
        removeApplySettingsListener();
        removeDoneButtonListener();
        removeEnemyBoardListener();
        removeMenuListeners();
        removePlayerBoardListener();
        removeShowYourShipsButtonListener();
        removeShipSelectionListeners();
    }

    public void removeApplySettingsListener() {
        if (applaySettingsListener != null) {
            gui.getPanelSettings().getButtonApplySettings().removeActionListener(applaySettingsListener);
        }
    }

    public void removeDoneButtonListener() {
        if (doneButtonListener != null) {
            GamePanel panelGame = gui.getPanelGame();
            panelGame.getButtonDone().removeActionListener(doneButtonListener);
        }
    }

    public void removeEnemyBoardListener() {
        if (enemyBoardListeners != null) {
            final GamePanel panelGame = gui.getPanelGame();
            JButton[][] playerBoard = panelGame.getPanelEnemyBoard().getButtonsField();
            for (int i = 0; i < playerBoard.length; i++) {
                for (int j = 0; j < playerBoard.length; j++) {
                    playerBoard[i][j].removeActionListener(enemyBoardListeners[i][j]);
                }
            }
        }
    }

    private void removeMenuListeners() {
        if (menuListeners != null) {
            gui.getMenuItemSaveGame().removeActionListener(menuListeners[0]);
            gui.getMenuItemLoadGame().removeActionListener(menuListeners[1]);
            gui.getPanelMainMenu().getButtonLoadGame().removeActionListener(menuListeners[2]);
        }
    }

    public void removePlayerBoardListener() {
        if (playerBoardListeners != null) {
            final GamePanel panelGame = gui.getPanelGame();
            JButton[][] playerBoard = panelGame.getPanelPlayerBoard().getButtonsField();
            playerBoardListeners = new ActionListener[playerBoard.length][playerBoard.length];
            for (int i = 0; i < playerBoard.length; i++) {
                for (int j = 0; j < playerBoard.length; j++) {
                    playerBoard[i][j].removeActionListener(playerBoardListeners[i][j]);
                }
            }
        }
    }

    private void removeShipSelectionListeners() {
        if (shipSelectionListeners != null) {
            gui.getPanelGame().getRadioButtonDestroyer().removeActionListener(shipSelectionListeners[0]);
            gui.getPanelGame().getRadioButtonFrigate().removeActionListener(shipSelectionListeners[1]);
            gui.getPanelGame().getRadioButtonCorvette().removeActionListener(shipSelectionListeners[2]);
            gui.getPanelGame().getRadioButtonSubmarine().removeActionListener(shipSelectionListeners[3]);
        }
    }

    public void removeShowYourShipsButtonListener() {
        if (showYourShipsListener != null) {
            GamePanel panelGame = gui.getPanelGame();
            panelGame.getButtonShowYourShips().removeActionListener(showYourShipsListener);
        }
    }

    public void updatePlayerBoard(String playerName) {
        GamePanel panelGame = gui.getPanelGame();
        JButton[][] board;
        FieldState[][] fieldStates = null;
        board = panelGame.getPanelPlayerBoard().getButtonsField();
        try {
            fieldStates = game.getPlayerByName(playerName).getFieldStates(true);
            ctrl.updateBoardColors(board, fieldStates);
        } catch (FieldOutOfBoardException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void updatePreview(int startX, int startY, JButton[][] board) {
        int boardSize = board.length;
        boolean isHorizontal = gui.getPanelGame().getRadioButtonHorizontalOrientation().isSelected();
        Orientation orientation = isHorizontal ? Orientation.HORIZONTAL : Orientation.VERTICAL;
        int xDirection = isHorizontal ? 1 : 0;
        int yDirection = isHorizontal ? 0 : 1;
        int x;
        int y;
        int range;
        boolean possible = false;

        // Farben zurücksetzen
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j].setBackground(GUI.EMPTY_COLOR);
            }
        }

        if (!game.isReady()) {
            possible = ctrl.isItPossibleToPlaceShip(game.getCurrentPlayer(), startX, startY, orientation);
            range = game.getCurrentPlayer().getCurrentShip().getSize();
        } else {
            possible = ctrl.isItPossibleToShoot(game.getCurrentPlayer().getBoard(), startX, startY);
            range = game.getCurrentPlayer().getCurrentShip().getShootingRange();
        }

        for (int i = 0; i < range; i++) {
            x = startX + i * xDirection;
            y = startY + i * yDirection;
            Color c = possible ? GUI.PREVIEW_COLOR : GUI.NOT_POSSIBLE_COLOR;
            if (x < boardSize && y < boardSize) {
                board[y][x].setBackground(c);
            }
        }
    }
}

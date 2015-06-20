package de.hsbremen.battleshipextreme.client;

import de.hsbremen.battleshipextreme.client.listener.ServerGameBrowserListener;
import de.hsbremen.battleshipextreme.client.listener.ServerObjectReceivedListener;
import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.exception.ShipAlreadyPlacedException;
import de.hsbremen.battleshipextreme.model.exception.ShipOutOfBoardException;
import de.hsbremen.battleshipextreme.model.network.IServerObjectReceivedListener;
import de.hsbremen.battleshipextreme.model.network.NetworkClient;
import de.hsbremen.battleshipextreme.model.player.HumanPlayer;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.model.ship.Ship;
import de.hsbremen.battleshipextreme.model.ship.ShipType;
import de.hsbremen.battleshipextreme.network.TransferableObjectFactory;
import de.hsbremen.battleshipextreme.network.transfarableObject.NetGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;
import java.util.*;

/**
 * Created by cschaf on 18.06.2015.
 */
public class MultiPlayerClientController implements Serializable {
// ------------------------------ FIELDS ------------------------------

    private GUI gui;
    private Controller ctrl;
    private NetworkClient network;
    private ServerGameBrowserListener serverGameBrowserListener;
    private IServerObjectReceivedListener serverObjectReceivedListener;
    private String connectedAsPlayer;
    private Player player;
    private HashMap<String, Board> enemies;
    private boolean playerIsReloading;
    private boolean isReady;

// --------------------------- CONSTRUCTORS ---------------------------

    public MultiPlayerClientController(NetworkClient network, GUI gui, Controller ctrl) {
        this.network = network;
        this.gui = gui;
        this.ctrl = ctrl;
        this.enemies = new HashMap<String, Board>();
        this.serverObjectReceivedListener = new ServerObjectReceivedListener(this.gui, network, this);
        this.serverGameBrowserListener = new ServerGameBrowserListener(network, this);
    }

    // -------------------------- OTHER METHODS --------------------------
    public void addAllListeners() {
        addServerConnectionListener();
        addServerGameBrowserListeners();
        addApplySettingsListener();
        addDoneButtonListener();
    }

    private void addApplySettingsListener() {
/*        gui.getPanelSettings().getButtonApplySettings().addActionListener(new ActionListener() {
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
                String gameName = panelSettings.getTextFieldGameName().getText();
                String gamePassword = panelSettings.getTextFieldGamePassword().getText();
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
                    if (network.isConnected()) {
                        if (gameName.length() > 3 && !gameName.startsWith(" ")) {
                            network.getSender().sendGame(gameName, gamePassword, settings);
                            gui.showPanel(GUI.SERVER_CONNECTION_PANEL);
                        } else {
                            network.getErrorHandler().errorHasOccurred(new EventArgs<ITransferable>(this, TransferableObjectFactory.CreateMessage("Game name have to be more then 3 characters!")));
                        }
                    } else {
                        try {
                            initializeGame(settings);
                        } catch (Exception e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });*/
    }

    public void updateEnemySelection() {
        GamePanel panelGame = gui.getPanelGame();
        Set<String> myEnemies = this.enemies.keySet();
        JComboBox<String> enemyComboBox = panelGame.getComboBoxEnemySelection();
        enemyComboBox.removeAllItems();
        for (String enemy : myEnemies) {
            enemyComboBox.addItem(enemy);
        }
    }

    private void addDoneButtonListener() {
        GamePanel panelGame = gui.getPanelGame();
        panelGame.getButtonDone().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                next();
            }
        });
    }

    private void addEnemyBoardListener() {
        GamePanel panelGame = gui.getPanelGame();
        JButton[][] playerBoard = panelGame.getPanelEnemyBoard().getButtonsField();
        for (int i = 0; i < playerBoard.length; i++) {
            for (int j = 0; j < playerBoard.length; j++) {
                playerBoard[i][j].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        FieldButton fieldButton = (FieldButton) e.getSource();
                        String attackingPlayerName = player.getName();
                        String attackedPlayerName = gui.getPanelGame().getComboBoxEnemySelection().getSelectedItem().toString();
                        int xPos = fieldButton.getxPos();
                        int yPos = fieldButton.getyPos();
                        boolean isHorizontal = gui.getPanelGame().getRadioButtonHorizontalOrientation().isSelected();
                        Ship currentShip = player.getCurrentShip();
                        network.getSender().sendTurn(TransferableObjectFactory.CreateTurn(attackingPlayerName, attackedPlayerName, xPos, yPos, isHorizontal, currentShip));
                        ctrl.setEnemyBoardEnabled(false);
                    }
                });
            }
        }
    }

    public void setPlayerNames(ArrayList<String> names) {
        ArrayList<String> keys = new ArrayList<String>(enemies.keySet());
        LinkedHashMap<String, Board> result = new LinkedHashMap<String, Board>();
        for (int i = 0; i < names.size(); i++) {
            if (keys.size() <= 0) {
                result.put(names.get(i), new Board(player.getBoard().getSize()));
            } else {
                result.put(names.get(i), enemies.get(keys.get(i)));
            }
        }
        for (Map.Entry<String, Board> entry : result.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        enemies = result;
        updateEnemySelection();
    }

    public String getConnectedAsPlayer() {
        return connectedAsPlayer;
    }

    public void setConnectedAsPlayer(String connectedAsPlayer) {
        this.connectedAsPlayer = connectedAsPlayer;
    }

    private void placeShip(int xPos, int yPos, boolean isHorizontal) throws ShipAlreadyPlacedException, FieldOutOfBoardException, ShipOutOfBoardException {
        Orientation orientation = isHorizontal ? Orientation.HORIZONTAL : Orientation.VERTICAL;

        boolean possible = player.placeShip(xPos, yPos, orientation);
        if (possible) {
            network.getSender().sendShipPlacedInformation(xPos, yPos, orientation, player.getCurrentShip().getType());
            player.nextShip();
        }

        if (player.hasPlacedAllShips()) {
            ctrl.setPlayerBoardEnabled(false);
            ctrl.setDoneButtonEnabled(false);
            ctrl.setInfoLabelMessage(player + " placed all ships");
        }

        updatePlayerBoard();
        ctrl.updateShipSelection(player);
    }

    private void addPlayerBoardListener() {
        GamePanel panelGame = gui.getPanelGame();
        JButton[][] playerBoard = panelGame.getPanelPlayerBoard().getButtonsField();
        for (int i = 0; i < playerBoard.length; i++) {
            for (int j = 0; j < playerBoard.length; j++) {
                playerBoard[i][j].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        FieldButton fieldButton = (FieldButton) e.getSource();
                        try {
                            placeShip(fieldButton.getxPos(), fieldButton.getyPos(), gui.getPanelGame().getRadioButtonHorizontalOrientation().isSelected());
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
                });
            }
        }
    }

    private void addServerConnectionListener() {
        gui.getPanelServerConnection().getPnlServerConnectionBar().getBtnConnect().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!network.isConnected()) {
                    addServerObjectReceivedListeners();
                    // hole Verbindungsdaten
                    network.setIp(gui.getPanelServerConnection().getPnlServerConnectionBar().getTbxIp().getText());
                    network.setPort(Integer.parseInt(gui.getPanelServerConnection().getPnlServerConnectionBar().getTbxPort().getText()));
                    // Verbinde zum Server
                    network.connect();
                    // Sende login
                    if (network.isConnected()) {
                        network.getSender().sendLogin(gui.getPanelServerConnection().getPnlServerConnectionBar().getTbxUsername().getText());
                    }
                    //game.setConnectedAsPlayer(gui.getPanelServerConnection().getPnlServerConnectionBar().getTbxUsername().getText());
                }
            }
        });

        gui.getPanelServerConnection().getPnlServerConnectionBar().getBtnDisconnect().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeServerObjectReceivedListeners();
                network.dispose();
                gui.getPanelServerConnection().getPnlServerConnectionBar().setEnabledAfterStartStop(true);
                gui.getPanelServerConnection().getPnlServerGameBrowser().getTblModel().removeAllGames();
                gui.getPanelServerConnection().getPnlServerGameBrowser().getBtnCreate().setEnabled(false);
                gui.getPanelServerConnection().getPnlServerGameBrowser().getBtnRefresh().setEnabled(false);
            }
        });

        gui.getPanelServerConnection().getPnlServerGameBrowser().getBtnJoin().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int rowIndex = gui.getPanelServerConnection().getPnlServerGameBrowser().getTblGames().getSelectedRow();
                if (rowIndex > -1) {
                    GameListModel model = (GameListModel) gui.getPanelServerConnection().getPnlServerGameBrowser().getTblGames().getModel();
                    NetGame game = model.getGame(rowIndex);
                    if (game.isPrivate()) {
                        createPasswordPrompt(game);
                    } else {
                        network.join(game.getId());
                    }
                }
            }
        });
// TODO: das geh�rt hier nicht hin
        gui.getPanelGame().getTextFieldChatMessage().addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {

            }

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }

            public void keyReleased(KeyEvent e) {

            }
        });

        gui.getPanelServerConnection().getPnlServerGameBrowser().getBtnRefresh().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                network.getSender().requestGameList();
            }
        });

        gui.getPanelServerConnection().getPnlServerGameBrowser().getBtnBack().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gui.showPanel(GUI.MAIN_MENU_PANEL);
            }
        });

        gui.getPanelServerConnection().getPnlServerGameBrowser().getBtnCreate().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setupSettingsPanelForMultiplayerGame();
                gui.showPanel(GUI.SETTINGS_PANEL);
            }
        });

        gui.getPanelGame().getButtonSendMessage().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });


        gui.getPanelGame().getTextFieldChatMessage().addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {

            }

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }

            public void keyReleased(KeyEvent e) {

            }
        });
    }

    private void addServerObjectReceivedListeners() {
        network.addServerObjectReceivedListener(serverObjectReceivedListener);
    }

    public boolean handleAllShipsAreReloading() {
        if (player.areAllShipsReloading()) {
            ctrl.setInfoLabelMessage("All ships of " + player + " are reloading");
            ctrl.setEnemyBoardEnabled(false);
            ctrl.setShipSelectionEnabled(false);
            return true;
        } else {
            ctrl.setInfoLabelMessage(player + " is shooting");
            enableAvailableShips();
            //ctrl.selectFirstAvailableShipType();
            return false;
        }
    }

    private void setupSettingsPanelForMultiplayerGame() {
        // enable/disable controls for necessary game options
        SettingsPanel settings = gui.getPanelSettings();
        settings.getTextFieldAiPlayers().setEnabled(false);
        settings.getTextFieldAiPlayers().setVisible(false);
        settings.getLabelAiPlayers().setVisible(false);

        settings.getLabelGameName().setEnabled(true);
        settings.getLabelGameName().setVisible(true);

        settings.getTextFieldGameName().setEnabled(true);
        settings.getTextFieldGameName().setVisible(true);

        settings.getLabelGamePassword().setEnabled(true);
        settings.getLabelGamePassword().setVisible(true);

        settings.getTextFieldGamePassword().setVisible(true);
        settings.getTextFieldGamePassword().setEnabled(true);
    }

    public void createPasswordPrompt(NetGame game) {
        PasswordInputPanel panel = new PasswordInputPanel();
        String[] options = new String[]{"OK", "Cancel"};
        int option = JOptionPane.showOptionDialog(null, panel, "Password for " + game.getName(), JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        // pressing OK button
        if (option == JOptionPane.OK_OPTION) {
            char[] password = panel.getTbxPassword().getPassword();
            String strPassword = new String(password);

            if (strPassword.equals(game.getPassword())) {
                join(game.getId());
            } else {
                JOptionPane.showMessageDialog(null, "Wrong password!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void join(String id) {
        network.join(id);
    }

    private void removeServerObjectReceivedListeners() {
        network.removeServerObjectReceivedListener(serverObjectReceivedListener);
    }

    private void sendMessage() {
        String username = gui.getPanelServerConnection().getPnlServerConnectionBar().getTbxUsername().getText();
        String msg = gui.getPanelGame().getTextFieldChatMessage().getText();
        network.getSender().sendMessage(username, msg);
        gui.getPanelGame().getTextFieldChatMessage().setText("");
    }

    private void addServerGameBrowserListeners() {
        gui.getPanelServerConnection().getPnlServerGameBrowser().getTblGames().getColumnModel().addColumnModelListener(serverGameBrowserListener);
        gui.getPanelServerConnection().getPnlServerGameBrowser().getTblGames().addMouseListener(serverGameBrowserListener);
    }

    private void addShipSelectionListeners() {
/*        gui.getPanelGame().getRadioButtonDestroyer().addActionListener(new ActionListener() {
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
        });*/
    }

    private void addShowYourShipsButtonListener() {
/*        GamePanel panelGame = gui.getPanelGame();
        panelGame.getButtonShowYourShips().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JToggleButton btn = (JToggleButton) e.getSource();
                if (btn.isSelected()) {
                    if (game.getCurrentPlayer().getType() == PlayerType.HUMAN) {
                        showPlayerBoard();
                    }

                } else {
                    if (network.isConnected()) {
                        showEmptyPlayerBoard(game.getConnectedAsPlayer());
                    } else {
                        showEmptyPlayerBoard(game.getCurrentPlayer().getName());

                    }
                }
            }
        });*/
    }

    public void enableAvailableShips() {
        GamePanel panelGame = gui.getPanelGame();
        ;
        panelGame.getRadioButtonDestroyer().setEnabled(player.isShipOfTypeAvailable(ShipType.DESTROYER));
        panelGame.getRadioButtonFrigate().setEnabled(player.isShipOfTypeAvailable(ShipType.FRIGATE));
        panelGame.getRadioButtonCorvette().setEnabled(player.isShipOfTypeAvailable(ShipType.CORVETTE));
        panelGame.getRadioButtonSubmarine().setEnabled(player.isShipOfTypeAvailable(ShipType.SUBMARINE));
    }

    public void initializeClientAfterJoined(NetGame game) {
        ctrl.createBoardPanels(game.getBoardSize());
        player = new HumanPlayer(game.getBoardSize(), game.getSettings().getDestroyers(), game.getSettings().getFrigates(), game.getSettings().getCorvettes(), game.getSettings().getSubmarines());
        addPlayerBoardListener();
        addEnemyBoardListener();
        // disable all controls till game ready to start
        ctrl.setBoardsEnabled(false);
        ctrl.setEnemySelectionEnabled(false);
        ctrl.setEnemyBoardEnabled(false);
        ctrl.setShipSelectionEnabled(false);
        ctrl.setDoneButtonEnabled(false);
        gui.showPanel(GUI.GAME_PANEL);
    }

    public void updateEnemyBoard() {
/*        GamePanel panelGame = gui.getPanelGame();
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
        }*/
    }

    public boolean makeOnlineTurn(String attackingPlayerName, String enemyName, int xPos, int yPos, boolean isHorizontal) throws FieldOutOfBoardException {
/*        Orientation orientation = isHorizontal ? Orientation.HORIZONTAL : Orientation.VERTICAL;
        boolean possible = false;
        Player enemy = game.getPlayerByName(enemyName);
        possible = game.makeTurn(enemy, xPos, yPos, orientation);
        // Hier wird noch nicht alles richtig angezeigt!
        if (possible) {
            if (enemy.getName().equals(game.getConnectedAsPlayer())) {
                updatePlayerBoard(game.getConnectedAsPlayer());
            }

            if (game.getConnectedAsPlayer().equals(attackingPlayerName)) {
                updateEnemyBoard();
            }

            setInfoLabelMessage(game.getCurrentPlayer() + " attacked " + enemy);
        }
        return possible;*/
        return false;
    }

    public void setPlayerIsReloading(boolean playerIsReloading) {
        this.playerIsReloading = playerIsReloading;
    }

    public void next() {
        if (!isReady()) {
            if (player.hasPlacedAllShips()) {
                ctrl.setBoardsEnabled(false);
                gui.getPanelGame().getButtonDone().setEnabled(false);
            }
        } else {
            // alle Spieler habe ihre Schiffe gesetzt
            ctrl.setBoardsEnabled(false);
            gui.getPanelGame().getButtonDone().setEnabled(false);

            if (playerIsReloading) {
                network.getSender().sendPlayerIsReloading();
                playerIsReloading = false;
            }
        }
    }

    private void removeServerGameBrowserListeners() {
        gui.getPanelServerConnection().getPnlServerGameBrowser().getTblGames().getColumnModel().removeColumnModelListener(serverGameBrowserListener);
    }

    public void updatePlayerBoard() {
        GamePanel panelGame = gui.getPanelGame();
        JButton[][] board;
        FieldState[][] fieldStates = null;
        board = panelGame.getPanelPlayerBoard().getButtonsField();
        try {
            fieldStates = player.getFieldStates(true);
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

        // Farben zur?cksetzen
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j].setBackground(GUI.EMPTY_COLOR);
            }
        }

        if (!isReady()) {
            possible = ctrl.isItPossibleToPlaceShip(player, startX, startY, orientation);
            range = player.getCurrentShip().getSize();
        } else {
            possible = ctrl.isItPossibleToShoot(enemies.get(gui.getPanelGame().getComboBoxEnemySelection().getSelectedItem()), startX, startY);
            range = player.getCurrentShip().getShootingRange();
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

    public void resizeServerGameListColumns() {
        ctrl.resizeServerGameListColumns();
    }

    public void setPlayerBoardEnabled(boolean b) {
        ctrl.setPlayerBoardEnabled(b);
    }

    public void setEnemySelectionEnabled(boolean b) {
        ctrl.setEnemySelectionEnabled(b);
    }

    public boolean isReady() {
        return isReady;
    }

    public void setIsReady(boolean isReady) {
        this.isReady = isReady;
    }

    public void setEnemyBoardEnabled(boolean b) {
        ctrl.setEnemyBoardEnabled(b);
    }

    public void setDoneButtonEnabled(boolean b) {
        ctrl.setDoneButtonEnabled(b);
    }
}

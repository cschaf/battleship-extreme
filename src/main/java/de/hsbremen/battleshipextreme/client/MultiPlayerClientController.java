package de.hsbremen.battleshipextreme.client;

import de.hsbremen.battleshipextreme.client.listener.ServerGameBrowserListener;
import de.hsbremen.battleshipextreme.client.listener.ServerObjectReceivedListener;
import de.hsbremen.battleshipextreme.client.workers.BoardUpdater;
import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.model.exception.*;
import de.hsbremen.battleshipextreme.model.network.IServerObjectReceivedListener;
import de.hsbremen.battleshipextreme.model.network.NetworkClient;
import de.hsbremen.battleshipextreme.model.player.HumanPlayer;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.model.ship.Ship;
import de.hsbremen.battleshipextreme.model.ship.ShipType;
import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.TransferableObjectFactory;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.transfarableObject.ClientTurn;
import de.hsbremen.battleshipextreme.network.transfarableObject.NetGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class MultiPlayerClientController implements Serializable {
    // ------------------------------ FIELDS ------------------------------

    private GUI gui;
    private Controller ctrl;
    private NetworkClient network;
    private ServerGameBrowserListener serverGameBrowserListener;
    private IServerObjectReceivedListener serverObjectReceivedListener;
    private Player player;
    private HashMap<String, FieldState[][]> enemies;
    private boolean playerIsReloading;
    private boolean isReady;
    private String connectedAs;
    private ActionListener doneButtonListener;
    private ActionListener enemySelectionListener;
    private ActionListener[][] enemyBoardListeners;
    private ActionListener[][] playerBoardListeners;
    private ActionListener applySettingsListener;
    private KeyListener textFieldChatMessageListener;
    private ActionListener buttonSendMessageListener;

    // --------------------------- CONSTRUCTORS ---------------------------

    public MultiPlayerClientController(NetworkClient network, GUI gui, Controller ctrl) {
        this.network = network;
        this.gui = gui;
        this.ctrl = ctrl;
        this.enemies = new HashMap<String, FieldState[][]>();
        this.serverObjectReceivedListener = new ServerObjectReceivedListener(this.gui, network, this);
        this.serverGameBrowserListener = new ServerGameBrowserListener(network, this);
    }

    // -------------------------- OTHER METHODS --------------------------

    /**
     * Fügt alle Listeners hinzu.
     */
    public void addAllListeners() {
        addServerConnectionListener();
        addServerGameBrowserListeners();
        addApplySettingsListener();
        addDoneButtonListener();
        addShipSelectionListeners();
        addEnemySelectionListener();
        addChatListeners();
        gui.getPanelGame().getButtonShowYourShips().setVisible(false);
    }

    public void removeAllListeners() {
        removeDoneButtonListener();
        removeEnemySelectionListener();
        removeEnemyBoardListener();
        removePlayerBoardListener();
        removeApplySettingsListener();
        removeChatListeners();
        if (network.isConnected()) {
            removeServerGameBrowserListeners();
            removeServerObjectReceivedListeners();
        }
    }

    /**
     * Fügt den Listener für den OK Buttons des SettingsPanels hinzu.
     */
    private void addApplySettingsListener() {
        this.applySettingsListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SettingsPanel panelSettings = gui.getPanelSettings();
                int players = Integer.parseInt(panelSettings.getTextFieldPlayers().getText());
                int aiPlayers = 0;// Integer.parseInt(panelSettings.getTextFieldAiPlayers().getText());
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
                    JOptionPane.showMessageDialog(gui.getFrame(), "Invalid player number!");
                } catch (InvalidNumberOfShipsException e1) {
                    JOptionPane.showMessageDialog(gui.getFrame(), "Invalid ship number");
                } catch (BoardTooSmallException e1) {
                    JOptionPane.showMessageDialog(gui.getFrame(), "Invalid board size!");
                }
                if (valid) {
                    if (gameName.length() > 3 && !gameName.startsWith(" ")) {
                        network.getSender().sendGame(gameName, gamePassword, settings);
                        gui.showPanel(GUI.SERVER_CONNECTION_PANEL);
                    } else {
                        network.getErrorHandler().errorHasOccurred(new EventArgs<ITransferable>(this, TransferableObjectFactory.CreateMessage("Game name have to be more then 3 characters!")));
                    }
                }
            }
        };
        gui.getPanelSettings().getButtonApplySettings().addActionListener(applySettingsListener);
    }

    /**
     * Enfernt den Listener für ApplySettings.
     */
    private void removeApplySettingsListener() {
        if (applySettingsListener != null) {
            gui.getPanelSettings().getButtonApplySettings().removeActionListener(this.applySettingsListener);
        }
    }

    /**
     * Updated die Liste der möglichen Gegner.
     */
    public void updateEnemySelection(String except) {
        GamePanel panelGame = gui.getPanelGame();
        Set<String> myEnemies = this.enemies.keySet();
        JComboBox<String> enemyComboBox = panelGame.getComboBoxEnemySelection();
        enemyComboBox.removeAllItems();
        for (String enemy : myEnemies) {
            if (!enemy.equals(except)) {
                enemyComboBox.addItem(enemy);
            }
        }
    }

    /**
     * Fügt den DoneButtom Listener hinzu.
     */
    private void addDoneButtonListener() {
        GamePanel panelGame = gui.getPanelGame();
        this.doneButtonListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                decreaseCurrentReloadTimeOfShips();
                next();
            }
        };
        panelGame.getButtonDone().addActionListener(doneButtonListener);
    }

    /**
     * Enfernt den Listener für den DoneButton.
     */
    private void removeDoneButtonListener() {
        if (doneButtonListener != null) {
            gui.getPanelGame().getButtonDone().removeActionListener(doneButtonListener);
        }
    }

    /**
     * Fügt den Listener der Gegnersauswahl hinzu.
     */
    private void addEnemySelectionListener() {
        GamePanel panelGame = gui.getPanelGame();
        final JComboBox<String> enemyComboBox = panelGame.getComboBoxEnemySelection();
        this.enemySelectionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateEnemyBoard();
            }
        };
        enemyComboBox.addActionListener(enemySelectionListener);
    }

    /**
     * Entfernt den Listener für die Gegnerauswahl.
     */
    private void removeEnemySelectionListener() {
        if (enemySelectionListener != null) {
            GamePanel panelGame = gui.getPanelGame();
            panelGame.getComboBoxEnemySelection().removeActionListener(enemySelectionListener);
        }
    }

    /**
     * Fügt den Listener für das gegnerische Board bzw die einzelnen Felder
     * davon hinzu
     */
    private void addEnemyBoardListener() {
        GamePanel panelGame = gui.getPanelGame();
        final JButton[][] playerBoard = panelGame.getPanelEnemyBoard().getButtonsField();
        enemyBoardListeners = new ActionListener[playerBoard.length][playerBoard.length];
        for (int i = 0; i < playerBoard.length; i++) {
            for (int j = 0; j < playerBoard.length; j++) {
                ActionListener fieldListener = new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        FieldButton fieldButton = (FieldButton) e.getSource();
                        String attackingPlayerName = player.getName();
                        String attackedPlayerName = gui.getPanelGame().getComboBoxEnemySelection().getSelectedItem().toString();
                        int xPos = fieldButton.getxPos();
                        int yPos = fieldButton.getyPos();
                        boolean isHorizontal = gui.getPanelGame().getRadioButtonHorizontalOrientation().isSelected();
                        ShipType shipType = player.getCurrentShip().getType();
                        if (ctrl.isItPossibleToShoot(enemies.get(attackedPlayerName), xPos, yPos)) {
                            network.getSender().sendTurn(TransferableObjectFactory.CreateTurn(attackingPlayerName, attackedPlayerName, xPos, yPos, isHorizontal, shipType));
                            ctrl.setEnemyBoardEnabled(false);
                            decreaseCurrentReloadTimeOfShips();
                        } else {
                            ctrl.setInfoLabelMessage("It´s not allowed to shoot at this field");
                        }
                    }
                };
                enemyBoardListeners[i][j] = fieldListener;
                playerBoard[i][j].addActionListener(fieldListener);
            }
        }
    }

    /**
     * Enfernt den Listeners für das gegnerische Board
     */
    private void removeEnemyBoardListener() {
        if (enemyBoardListeners != null) {
            GamePanel panelGame = gui.getPanelGame();
            JButton[][] playerBoard = panelGame.getPanelEnemyBoard().getButtonsField();
            for (int i = 0; i < playerBoard.length; i++) {
                for (int j = 0; j < playerBoard.length; j++) {
                    playerBoard[i][j].removeActionListener(enemyBoardListeners[i][j]);
                }
            }
        }
    }

    /**
     * Dient zum aktualisieren der Gegnerliste.
     */

    public void setPlayerNames(ArrayList<String> names) {
        ArrayList<String> keys = new ArrayList<String>(enemies.keySet());
        LinkedHashMap<String, FieldState[][]> result = new LinkedHashMap<String, FieldState[][]>();
        for (int i = 0; i < names.size(); i++) {
            if (!player.getName().equals(names.get(i))) {
                if (keys.size() <= 0) {
                    FieldState[][] fields = FieldState.array2dOfDefault(player.getBoard().getSize());
                    result.put(names.get(i), fields);
                } else {
                    result.put(names.get(i), enemies.get(keys.get(i)));
                }
                }
            }
        enemies = result;
        updateEnemySelection(null);
    }

    /**
     * Platziert das aktuell selektierte Schiff des aktuellen Spielers auf
     * seinem Board.
     */
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

    /**
     * Fügt den Listener für die Felder des eigenen Boards hinzu.
     */
    private void addPlayerBoardListener() {
        GamePanel panelGame = gui.getPanelGame();
        JButton[][] playerBoard = panelGame.getPanelPlayerBoard().getButtonsField();
        playerBoardListeners = new ActionListener[playerBoard.length][playerBoard.length];
        for (int i = 0; i < playerBoard.length; i++) {
            for (int j = 0; j < playerBoard.length; j++) {
                ActionListener fieldListener = new ActionListener() {
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
                            ctrl.setInfoLabelMessage("Ship can not be placed here");
                        }
                    }
                };
                playerBoardListeners[i][j] = fieldListener;
                playerBoard[i][j].addActionListener(fieldListener);
            }
        }
    }

    /**
     * Enfernt den Listener für PlayerBoard.
     */
    private void removePlayerBoardListener() {
        if (playerBoardListeners != null) {
            GamePanel panelGame = gui.getPanelGame();
            JButton[][] playerBoard = panelGame.getPanelPlayerBoard().getButtonsField();
            playerBoardListeners = new ActionListener[playerBoard.length][playerBoard.length];
            for (int i = 0; i < playerBoard.length; i++) {
                for (int j = 0; j < playerBoard.length; j++) {
                    playerBoard[i][j].removeActionListener(playerBoardListeners[i][j]);
                }
            }
        }
    }

    /**
     * Fügt Listener für die Schaltflächen im Multiplayer-Menü hinzu.
     */
    private void addServerConnectionListener() {
        // Listener für den Connect-Button
        // dient zur Herstellung der Verbindung zwischen Client und Server
        gui.getPanelServerConnection().getPnlServerConnectionBar().getBtnConnect().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (network.isConnected() && (connectedAs == null || connectedAs.isEmpty())) {
                    network.getSender().sendLogin(gui.getPanelServerConnection().getPnlServerConnectionBar().getTbxUsername().getText());
                } else if (!network.isConnected()) {
                    addServerObjectReceivedListeners();
                    // Verbindungsinformationen
                    network.setIp(gui.getPanelServerConnection().getPnlServerConnectionBar().getTbxIp().getText());
                    network.setPort(Integer.parseInt(gui.getPanelServerConnection().getPnlServerConnectionBar().getTbxPort().getText()));
                    // zum Server verbinden
                    network.connect();
                    // Login senden
                    if (network.isConnected()) {
                        network.getSender().sendLogin(gui.getPanelServerConnection().getPnlServerConnectionBar().getTbxUsername().getText());
                    }
                }
            }
        });

        // Listener für den Disconnect-Button
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

        // Listener für den Join-Button
        // dient zum Verbinden zu einem bestimmten Spiel
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

        // Listener für den Refresh-Button
        // dient zum aktualisieren der Spielliste
        gui.getPanelServerConnection().getPnlServerGameBrowser().getBtnRefresh().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                network.getSender().requestGameList();
            }
        });

        // Listener für den Back-Button
        // dient zum Zurückkehren ins Hauptmenü
        gui.getPanelServerConnection().getPnlServerGameBrowser().getBtnBack().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gui.showPanel(GUI.MAIN_MENU_PANEL);
            }
        });

        // Listener für den Create-Button
        // dient zum Aufrufen des Settings-Panel für ein Multiplayer-Spiel
        gui.getPanelServerConnection().getPnlServerGameBrowser().getBtnCreate().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setupSettingsPanelForMultiplayerGame();
                gui.showPanel(GUI.SETTINGS_PANEL);
            }
        });
    }

    /**
     * Fügt Listener für den Chat hinzu.
     */
    private void addChatListeners() {
        this.textFieldChatMessageListener = new KeyListener() {
            public void keyTyped(KeyEvent e) {

            }

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }

            public void keyReleased(KeyEvent e) {

            }
        };
        gui.getPanelGame().getTextFieldChatMessage().addKeyListener(textFieldChatMessageListener);
        this.buttonSendMessageListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        };
        gui.getPanelGame().getButtonSendMessage().addActionListener(buttonSendMessageListener);
    }

    /**
     * Entfernt Listener für den Chat.
     */
    private void removeChatListeners() {
        gui.getPanelGame().getTextFieldChatMessage().removeKeyListener(textFieldChatMessageListener);
        gui.getPanelGame().getButtonSendMessage().removeActionListener(buttonSendMessageListener);
    }

    /**
     * Fügt dem NetworkClient einen ServerObjectReceivedListener hinzu.
     */
    private void addServerObjectReceivedListeners() {
        network.addServerObjectReceivedListener(serverObjectReceivedListener);
    }

    /**
     * Prüft ob alle Schiffe eines Spielers nachladen.
     * @return true, wenn alle Schiffe nachladen, false wenn nicht
     */
    public boolean handleAllShipsAreReloading() {
        if (player.areAllShipsReloading()) {
            ctrl.setInfoLabelMessage("All ships of " + player + " are reloading");
            ctrl.setEnemyBoardEnabled(false);
            ctrl.setShipSelectionEnabled(false);
            return true;
        } else {
            ctrl.setInfoLabelMessage(player + " is shooting");
            enableAvailableShips();
            selectFirstAvailableShipType();
            return false;
        }
    }

    /**
     * Sorgt dafür, dass der RadioButton eines verfügbaren Schiffes auf selected
     * gesetzt wird.
     */
    private void selectFirstAvailableShipType() {
        ShipType availableShipType = player.getTypeOFirstAvailableShip();
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

    /**
     * Fügt Schalteflächen im Settings-Menu zu, die für ein Multiplayer-Spiel
     * benötigt werden.
     */
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

    /**
     * Dient zur Passwort-Abfrage in einem passwortgeschützten
     * Multiplayer-Spiel.
     */
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

    /**
     * Entfernt einen serverObjectReceivedListener vom NetworkClient.
     */
    private void removeServerObjectReceivedListeners() {
        network.removeServerObjectReceivedListener(serverObjectReceivedListener);
    }

    /**
     * Dient zum Senden einer Chat-Nachricht.
     */
    private void sendMessage() {
        String username = gui.getPanelServerConnection().getPnlServerConnectionBar().getTbxUsername().getText();
        String msg = gui.getPanelGame().getTextFieldChatMessage().getText();
        network.getSender().sendMessage(username, msg);
        gui.getPanelGame().getTextFieldChatMessage().setText("");
    }

    /**
     * Dient zum Hinzufügen der Listener im ServerGameBrowserPanel.
     */
    private void addServerGameBrowserListeners() {
        gui.getPanelServerConnection().getPnlServerGameBrowser().getTblGames().getColumnModel().addColumnModelListener(serverGameBrowserListener);
        gui.getPanelServerConnection().getPnlServerGameBrowser().getTblGames().addMouseListener(serverGameBrowserListener);
    }

    /**
     * Dient zum Setzen der currentShip-Eigenschaft vom Player.
     */
    public void selectShip(ShipType shipType) {
        player.setCurrentShipByType(shipType);
    }

    /**
     * Fügt Listener für die Schiffauswahl hinzu.
     */
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

    /**
     * Aktiviert die Radiobuttons für verfügbare Schiffe, deaktiviert sie für
     * nicht verfügbare.
     */
    public void enableAvailableShips() {
        GamePanel panelGame = gui.getPanelGame();
        panelGame.getRadioButtonDestroyer().setEnabled(player.isShipOfTypeAvailable(ShipType.DESTROYER));
        panelGame.getRadioButtonFrigate().setEnabled(player.isShipOfTypeAvailable(ShipType.FRIGATE));
        panelGame.getRadioButtonCorvette().setEnabled(player.isShipOfTypeAvailable(ShipType.CORVETTE));
        panelGame.getRadioButtonSubmarine().setEnabled(player.isShipOfTypeAvailable(ShipType.SUBMARINE));
    }

    /**
     * Initialisiert den Spieler nachdem er beigetreten ist.
     */
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

    /**
     * Aktualisiert die Felder des gegnerischen Boards.
     */
    public void updateEnemyBoard() {
        GamePanel panelGame = gui.getPanelGame();
        JButton[][] board;
        board = panelGame.getPanelEnemyBoard().getButtonsField();
        String enemyName = panelGame.getComboBoxEnemySelection().getSelectedItem().toString();
        FieldState[][] enemyBoard = enemies.get(enemyName);
        if (enemyBoard != null) {
            new BoardUpdater(gui, board, enemyBoard).execute();
        }
    }

    public void setPlayerIsReloading(boolean playerIsReloading) {
        this.playerIsReloading = playerIsReloading;
    }

    /**
     * Verringert die Nachladezeit der Schiffe des aktuellen Spielers.
     */
    public void decreaseCurrentReloadTimeOfShips() {
        Ship[] ships = player.getShips();
        for (Ship ship : ships) {
            ship.decreaseCurrentReloadTime();
        }
    }

    /**
     * Dient zum Setzen des nächsten Spielers.
     */
    public void next() {
        if (!isReady()) {
            if (player.hasPlacedAllShips()) {
                ctrl.setBoardsEnabled(false);
                gui.getPanelGame().getButtonDone().setEnabled(false);
            }
        } else {
            // all players has set their ships
            ctrl.setBoardsEnabled(false);
            gui.getPanelGame().getButtonDone().setEnabled(false);

            if (playerIsReloading) {
                network.getSender().sendTurn(TransferableObjectFactory.CreateTurn(player.getName()));
                playerIsReloading = false;
            }
        }
    }

    /**
     * Entfernt die Listener vom ServerGameBrowser.
     */
    private void removeServerGameBrowserListeners() {
        if (serverGameBrowserListener != null) {
            gui.getPanelServerConnection().getPnlServerGameBrowser().getTblGames().getColumnModel().removeColumnModelListener(serverGameBrowserListener);
        }
    }

    /**
     * Aktualisiert die Felder des eigenen Boards.
     */
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

    /**
     * Updated die Live Schuss- und Setz-Vorschau
     */
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

        // reset colors
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j].setBackground(GUI.EMPTY_COLOR);
            }
        }

        if (!isReady()) {
            possible = ctrl.isItPossibleToPlaceShip(player, startX, startY, orientation);
            range = player.getCurrentShip().getSize();
        } else {
            FieldState[][] enemyBoard = enemies.get(gui.getPanelGame().getComboBoxEnemySelection().getSelectedItem());
            possible = ctrl.isItPossibleToShoot(enemyBoard, startX, startY);
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

    public void updateCurrentShip() {
        player.setCurrentShipByType(player.getTypeOFirstAvailableShip());
    }

    /**
     * Dient zum Ausführen eines Spielerzugs.
     */
    public void handleClientTurn(ClientTurn clientTurn) {
        if (clientTurn.isWinner()) {
            markClientTurnFields(clientTurn);
            ctrl.setInfoLabelMessage(clientTurn.getWinnerName() + " has won");
            ctrl.setDoneButtonEnabled(false);
            ctrl.setShipSelectionEnabled(false);
            ctrl.setBoardsEnabled(false);
        } else if (clientTurn.isReloading()) {
            ctrl.setInfoLabelMessage(clientTurn.getAttackingPlayerName() + " is reloading");
        } else {
            if (clientTurn.getAttackingPlayerName().equals(player.getName())) {
                player.getCurrentShip().shoot();
            }
            ctrl.setInfoLabelMessage(clientTurn.getAttackingPlayerName() + " is shooting");
            markClientTurnFields(clientTurn);
        }
        updateEnemyBoard();
        updatePlayerBoard();
    }

    /**
     * Dient zum markieren der Felder die sich in einem Zug verändert haben.
     */
    private void markClientTurnFields(ClientTurn clientTurn) {
        if (clientTurn.getAttackedPlayerName().equals(player.getName())) {
            for (Field field : clientTurn.getFields()) {
                try {
                    if (field != null) {
                        player.markBoard(field.getXPos(), field.getYPos());
                    }
                } catch (FieldOutOfBoardException e) {
                    e.printStackTrace();
                }
            }
        } else {
            FieldState[][] board = enemies.get(clientTurn.getAttackedPlayerName());
            for (Field field : clientTurn.getFields()) {
                if (field != null) {
                    FieldState state = field.getState();
                    int x = field.getXPos();
                    int y = field.getYPos();
                    board[y][x] = state;
                }
            }
            String name = clientTurn.getAttackedPlayerName();
            enemies.put(name, board);
        }
    }

    public void setPlayerName(String playerName) {
        this.player.setName(playerName);
    }

    public String getConnectedAs() {
        return connectedAs;
    }

    public void setConnectedAs(String connectedAs) {
        this.connectedAs = connectedAs;
    }

    public void updateShipSelection() {
        ctrl.updateShipSelection(player);
    }

    public Player getPlayer() {
        return player;
    }
}

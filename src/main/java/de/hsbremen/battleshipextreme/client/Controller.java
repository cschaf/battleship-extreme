package de.hsbremen.battleshipextreme.client;

import de.hsbremen.battleshipextreme.client.listener.ServerErrorListener;
import de.hsbremen.battleshipextreme.client.workers.ShipStatusUpdater;
import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.model.Game;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.exception.ShipAlreadyPlacedException;
import de.hsbremen.battleshipextreme.model.exception.ShipOutOfBoardException;
import de.hsbremen.battleshipextreme.model.network.NetworkClient;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.network.eventhandling.listener.IErrorListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Verbindet die Models mit den Views. Fügt den Views Listeners hinzu, bzw verwaltet Usereingaben.
 */
public class Controller {
// ------------------------------ FIELDS ------------------------------

    private GUI gui;
    private NetworkClient network;
    private LocalClientController localClientController;
    private MultiPlayerClientController multiPlayerClientController;

    private IErrorListener serverErrorListener;

// --------------------------- CONSTRUCTORS ---------------------------

    public Controller(Game game, GUI gui) {
        this.gui = gui;
        this.network = new NetworkClient();

        this.serverErrorListener = new ServerErrorListener(this.gui, this.network);
        localClientController = new LocalClientController(game, gui, this);

        addServerErrorListeners();
        this.multiPlayerClientController = new MultiPlayerClientController(network, gui, this);
        addMenuListeners();
    }


// -------------------------- OTHER METHODS --------------------------

    /**
     * Fügt der Gui die Menü-Listerns hinzu
     */
    private void addMenuListeners() {
        gui.getMenuItemMainMenu().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gui.showPanel(GUI.MAIN_MENU_PANEL);
                setSaveButtonEnabled(false);
            }
        });

        gui.getMenuItemNewGame().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                multiPlayerClientController.removeAllListeners();
                network.dispose();
                localClientController.removeAllListeners();
                localClientController.addAllListeners();
                gui.showPanel(GUI.SETTINGS_PANEL);
                setSaveButtonEnabled(false);
            }
        });

        gui.getMenuItemQuitGame().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        gui.getPanelMainMenu().getButtonLocalGame().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                multiPlayerClientController.removeAllListeners();
                network.dispose();
                localClientController.addAllListeners();
                setupSettingsPanelForLocalGame();
                gui.showPanel(GUI.SETTINGS_PANEL);
            }
        });

        gui.getPanelMainMenu().getButtonMultiplayerGame().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (localClientController != null) {
                    localClientController.removeAllListeners();
                }
                multiPlayerClientController.addAllListeners();
                gui.showPanel(GUI.SERVER_CONNECTION_PANEL);
            }
        });

        gui.getPanelMainMenu().getButtonQuitGame().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        addNextLookAndFeelListener();
    }

    /**
     * Fügt der Gui die Style-Change-Listerns hinzu
     */
    private void addNextLookAndFeelListener() {
        gui.getMenuItemNextLookAndFeel().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int i;
                UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
                for (i = 0; i < lafInfo.length; i++) {
                    if (lafInfo[i].getClassName() == UIManager.getLookAndFeel().getClass().getName()) {
                        break;
                    }
                }
                i = (i >= lafInfo.length - 1) ? i = 0 : i + 1;
                try {
                    UIManager.setLookAndFeel(lafInfo[i].getClassName());
                } catch (ClassNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (InstantiationException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (UnsupportedLookAndFeelException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                SwingUtilities.updateComponentTreeUI(gui.getFrame());
                // gui.getFrame().pack();
            }
        });
    }

    /**
     * Aktiviert/deaktiviert den Save-Button
     */
    public void setSaveButtonEnabled(boolean enabled) {
        gui.getMenuItemSaveGame().setEnabled(enabled);
    }

    /**
     * Erstellt den Startzustand für das Settingspanel des Lokalen Spiels
     */
    private void setupSettingsPanelForLocalGame() {
        // enable/disable controls for necessary game options
        SettingsPanel settings = gui.getPanelSettings();
        settings.getTextFieldAiPlayers().setEnabled(true);
        settings.getTextFieldAiPlayers().setVisible(true);
        settings.getLabelAiPlayers().setVisible(true);

        settings.getLabelGameName().setEnabled(false);
        settings.getLabelGameName().setVisible(false);

        settings.getTextFieldGameName().setEnabled(false);
        settings.getTextFieldGameName().setVisible(false);

        settings.getLabelGamePassword().setEnabled(false);
        settings.getLabelGamePassword().setVisible(false);

        settings.getTextFieldGamePassword().setVisible(false);
        settings.getTextFieldGamePassword().setEnabled(false);
    }

    /**
     * Fürgt den Listener für die Fehlerbehandlung hinzu
     */
    private void addServerErrorListeners() {
        network.addErrorListener(serverErrorListener);
    }

    /**
     * Updated die farbigen Schiffstatuskacheln aus dem Navigationsbereich
     */
    public void UpdateShipLabelColors(JLabel[] shipFields, Color color) {
        for (int i = 0; i < shipFields.length; i++) {
            shipFields[i].setBackground(color);
        }
    }

    /**
     * Fügt dem Gamelog einen neuen Eintrag hinzu
     */
    public void appendGameLogEntry(String message) {
        gui.getPanelGame().getTextAreaGameLog().append(message + "\r\n\r\n");
    }

    /**
     * Erzeugt das Spielfeld für einen Spieler (Gegener- und Eigenes -Board)
     */
    public void createBoardPanels(int boardSize) {
        GamePanel panelGame = gui.getPanelGame();
        if (panelGame.getPanelPlayerBoard().getComponentCount() > 0) {
            panelGame.getPanelPlayerBoard().removeAll();
            panelGame.getPanelEnemyBoard().removeAll();
        }
        panelGame.getPanelPlayerBoard().initializeBoardPanel("You", boardSize);
        panelGame.getPanelEnemyBoard().initializeBoardPanel("Enemy", boardSize);
        gui.getFrame().pack();
        addBoardMouseListener(panelGame.getPanelPlayerBoard().getButtonsField());
        addBoardMouseListener(panelGame.getPanelEnemyBoard().getButtonsField());
    }

    /**
     * Fügt einem Board einen MouseListener hinzu, für das wechseln der Orientation per Mausklick
     */
    private void addBoardMouseListener(final JButton[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                board[i][j].addMouseListener(new MouseListener() {
                    public void mouseClicked(MouseEvent e) {
                        // TODO Auto-generated method stub
                    }

                    public void mousePressed(MouseEvent e) {
                        // TODO Auto-generated method stub
                    }

                    public void mouseReleased(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            FieldButton fieldButton = (FieldButton) e.getSource();
                            GamePanel pg = gui.getPanelGame();
                            if (pg.getRadioButtonHorizontalOrientation().isSelected()) {
                                pg.getRadioButtonVerticalOrientation().setSelected(true);
                            } else {
                                pg.getRadioButtonHorizontalOrientation().setSelected(true);
                            }
                            updatePreview(fieldButton.getxPos(), fieldButton.getyPos(), board);
                        }
                    }

                    public void mouseEntered(MouseEvent e) {
                        FieldButton fieldButton = (FieldButton) e.getSource();
                        updatePreview(fieldButton.getxPos(), fieldButton.getyPos(), board);
                    }

                    public void mouseExited(MouseEvent e) {
                        // TODO Auto-generated method stub
                    }
                });
            }
        }
    }

    /**
     * Updated die Live Schuss- und Setz-Vorschau
     */
    private void updatePreview(int startX, int startY, JButton[][] board) {
        if (network.isConnected()) {
            multiPlayerClientController.updatePreview(startX, startY, board);
        } else {
            localClientController.updatePreview(startX, startY, board);
        }
    }

    /**
     * Prüft ob ein Schiff an der gewünschten Position platziert werden kann
     */
    public boolean isItPossibleToPlaceShip(Player player, int startX, int startY, Orientation orientation) {
        try {
            if (player.isItPossibleToPlaceShip(startX, startY, orientation)) {
                return true;
            }
        } catch (ShipOutOfBoardException e) {
        } catch (ShipAlreadyPlacedException e) {
        } catch (FieldOutOfBoardException e) {
        }
        return false;
    }

    /**
     * Prüft ob auf die gewünschte Position geschossen werden kann
     */
    public boolean isItPossibleToShoot(Board board, int startX, int startY) {
        FieldState fs = board.getFieldStates(false)[startY][startX];
        return fs == null;
    }

    /**
     * Prüft ob auf die gewünschte Position geschossen werden kann
     */
    public boolean isItPossibleToShoot(FieldState[][] board, int startX, int startY) {
        boolean result = false;
        FieldState state = board[startY][startX];
        if (state == FieldState.HAS_SHIP || state == FieldState.IS_EMPTY) {
            result = true;
        }
        return result;
    }

    private void removeServerErrorListeners() {
        network.removeErrorListener(serverErrorListener);
    }

    /**
     * Updated die Spaltenlänge des GameBrowserTable mithilfe von Prozentverteilung
     */

    public void resizeServerGameListColumns() {
        JTable tbl = gui.getPanelServerConnection().getPnlServerGameBrowser().getTblGames();
        Dimension tableSize = tbl.getSize();
        tbl.getColumn("Name").setWidth(Math.round((tableSize.width - 202)));
        tbl.getColumn("Player").setWidth(45);
        tbl.getColumn("Created at").setWidth(127);
        tbl.getColumn("PW").setWidth(30);
    }

    /**
     * Aktiviert/Deaktiviert die beiden Boards eines Spielers
     */
    public void setBoardsEnabled(boolean state) {
        setEnemyBoardEnabled(state);
        setPlayerBoardEnabled(state);
    }

    /**
     * Aktiviert/Deaktiviert das Gegner-Board
     */
    public void setEnemyBoardEnabled(boolean enabled) {
        setBoardEnabled(gui.getPanelGame().getPanelEnemyBoard().getButtonsField(), enabled);
    }

    /**
     * Aktiviert/Deaktiviert ein Board
     */
    private void setBoardEnabled(JButton[][] board, boolean enabled) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                board[i][j].setEnabled(enabled);
            }
        }
    }

    /**
     * Aktiviert/Deaktiviert das eigene Board
     */
    public void setPlayerBoardEnabled(boolean enabled) {
        setBoardEnabled(gui.getPanelGame().getPanelPlayerBoard().getButtonsField(), enabled);
    }

    /**
     * Aktiviert/Deaktiviert den Done Button
     */
    public void setDoneButtonEnabled(boolean enabled) {
        gui.getPanelGame().getButtonDone().setEnabled(enabled);
    }

    /**
     * Aktiviert/Deaktiviert die Gegenerauswahl
     */
    public void setEnemySelectionEnabled(boolean enabled) {
        gui.getPanelGame().getComboBoxEnemySelection().setEnabled(enabled);
    }

    /**
     * Setzt eine Nachricht für das Message Label
     */
    public void setInfoLabelMessage(String message) {
        gui.getPanelGame().getLabelInfo().setText(message);
    }

    /**
     * Aktiviert/Deaktiviert die Schiffsauswahl
     */
    public void setShipSelectionEnabled(boolean enabled) {
        GamePanel panelGame = gui.getPanelGame();
        panelGame.getRadioButtonDestroyer().setEnabled(enabled);
        panelGame.getRadioButtonFrigate().setEnabled(enabled);
        panelGame.getRadioButtonCorvette().setEnabled(enabled);
        panelGame.getRadioButtonSubmarine().setEnabled(enabled);
    }

    /**
     * Updated die Felder eines Boardes, ändert die Icons/Images
     */
    public void updateBoardColors(JButton[][] board, FieldState[][] fieldStates) {
        int boardSize = fieldStates.length;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                FieldState f = fieldStates[i][j];
                if (f != null) {
                    switch (fieldStates[i][j]) {
                        case DESTROYED:
                            board[i][j].setIcon(gui.getDestroyedIcon());
                            break;
                        case HIT:
                            board[i][j].setIcon(gui.getHitIcon());
                            break;
                        case MISSED:
                            board[i][j].setIcon(gui.getMissedIcon());
                            break;
                        case HAS_SHIP:
                            board[i][j].setIcon(gui.getShipIcon());
                            break;
                        case IS_EMPTY:
                            board[i][j].setIcon(null);
                        default:
                            break;
                    }
                } else {
                    board[i][j].setIcon(null);
                }
            }
        }
    }

    /**
     * Updated die Schiffsauswahl, bei Änderung eines Schiffstatusses
     */
    public void updateShipSelection(Player player) {
        GamePanel panelGame = gui.getPanelGame();
        new ShipStatusUpdater(this, panelGame, player).execute();
    }
}

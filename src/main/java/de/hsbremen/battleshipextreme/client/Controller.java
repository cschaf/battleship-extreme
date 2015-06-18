package de.hsbremen.battleshipextreme.client;

import de.hsbremen.battleshipextreme.client.listener.ServerErrorListener;
import de.hsbremen.battleshipextreme.client.workers.ShipStatusUpdater;
import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.model.Game;
import de.hsbremen.battleshipextreme.model.network.NetworkClient;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.network.eventhandling.listener.IErrorListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Controller {
// ------------------------------ FIELDS ------------------------------

    private Game game;
    private GUI gui;
    private NetworkClient network;
    private LocalClientController localClientController;

    private IErrorListener serverErrorListener;

    private boolean playerIsReloading;

// --------------------------- CONSTRUCTORS ---------------------------

    public Controller(Game game, GUI gui) {
        this.game = game;
        this.gui = gui;
        this.network = new NetworkClient();

        this.serverErrorListener = new ServerErrorListener(this.gui, this.network);

        this.localClientController = new LocalClientController(game, gui, this);
        addServerErrorListeners();

        addMenuListeners();
        //addServerConnectionListener();
        //addServerGameBrowserListeners();
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

        gui.getMenuItemQuitGame().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        gui.getPanelMainMenu().getButtonLocalGame().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setupSettingsPanelForLocalGame();
                gui.showPanel(GUI.SETTINGS_PANEL);
            }
        });


        gui.getPanelMainMenu().getButtonMultiplayerGame().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gui.showPanel(GUI.SERVER_CONNECTION_PANEL);
            }
        });

        gui.getPanelMainMenu().getButtonQuitGame().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        addNextLookAndFeelListener();
        //addApplySettingsListener();
        //addDoneButtonListener();
        localClientController.addShowYourShipsButtonListener();
    }

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

    public void setSaveButtonEnabled(boolean enabled) {
        gui.getMenuItemSaveGame().setEnabled(enabled);
    }

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

    private void addServerErrorListeners() {
        network.addErrorListener(serverErrorListener);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setPlayerIsReloading(boolean playerIsReloading) {
        this.playerIsReloading = playerIsReloading;
    }

// -------------------------- OTHER METHODS --------------------------

    public void UpdateShipLabelColors(JLabel[] shipFields, Color color) {
        for (int i = 0; i < shipFields.length; i++) {
            shipFields[i].setBackground(color);
        }
    }

    private void addServerConnectionListener() {
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

    private void sendMessage() {
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

    public void appendGameLogEntry(String message) {
        gui.getPanelGame().getTextAreaGameLog().append(message + "\r\n\r\n");
    }

    public void createBoardPanels(int boardSize) {
        GamePanel panelGame = gui.getPanelGame();
        if (panelGame.getPanelPlayerBoard().getComponentCount() > 0) {
            panelGame.getPanelPlayerBoard().removeAll();
            panelGame.getPanelEnemyBoard().removeAll();
        }
        panelGame.getPanelPlayerBoard().initializeBoardPanel("You", boardSize);
        panelGame.getPanelEnemyBoard().initializeBoardPanel("Enemy", boardSize);
        gui.getFrame().pack();
        localClientController.addPlayerBoardListener();
        localClientController.addEnemyBoardListener();
        addBoardMouseListener(panelGame.getPanelPlayerBoard().getButtonsField());
        addBoardMouseListener(panelGame.getPanelEnemyBoard().getButtonsField());
    }

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
                            GamePanel pg = gui.getPanelGame();
                            if (pg.getRadioButtonHorizontalOrientation().isSelected()) {
                                pg.getRadioButtonVerticalOrientation().setSelected(true);
                            } else {
                                pg.getRadioButtonHorizontalOrientation().setSelected(true);
                            }
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

    private void updatePreview(int startX, int startY, JButton[][] board) {
        if (network.isConnected()){
        }
        else {
            localClientController.updatePreview(startX,startY,board);
        }
    }

    public boolean handleAllShipsAreReloading() {
        return false;
    }

    private void initializeGameView() {
    }

    private void removeServerErrorListeners() {
        network.removeErrorListener(serverErrorListener);
    }

    public void resizeServerGameListColumns() {
        JTable tbl = gui.getPanelServerConnection().getPnlServerGameBrowser().getTblGames();
        Dimension tableSize = tbl.getSize();
        tbl.getColumn("Name").setWidth(Math.round((tableSize.width - 202)));
        tbl.getColumn("Player").setWidth(45);
        tbl.getColumn("Created at").setWidth(127);
        tbl.getColumn("PW").setWidth(30);
    }

    public void setBoardsEnabled(boolean state) {
        setEnemyBoardEnabled(state);
        setPlayerBoardEnabled(state);
    }

    public void setEnemyBoardEnabled(boolean enabled) {
        setBoardEnabled(gui.getPanelGame().getPanelEnemyBoard().getButtonsField(), enabled);
    }

    private void setBoardEnabled(JButton[][] board, boolean enabled) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                board[i][j].setEnabled(enabled);
            }
        }
    }

    public void setPlayerBoardEnabled(boolean enabled) {
        setBoardEnabled(gui.getPanelGame().getPanelPlayerBoard().getButtonsField(), enabled);
    }

    public void setDoneButtonEnabled(boolean enabled) {
        gui.getPanelGame().getButtonDone().setEnabled(enabled);
    }

    public void setEnemySelectionEnabled(boolean enabled) {
        gui.getPanelGame().getComboBoxEnemySelection().setEnabled(enabled);
        gui.getPanelGame().getButtonApplyEnemy().setEnabled(enabled);
    }

    public void setInfoLabelMessage(String message) {
        gui.getPanelGame().getLabelInfo().setText(message);
    }

    public void setPlayerNames(ArrayList<String> names) {
        for (int i = 0; i < game.getPlayers().length; i++) {
            game.getPlayers()[i].setName(names.get(i));
        }
        updateEnemyOnlineSelection(game.getConnectedAsPlayer());
    }

    public void updateEnemyOnlineSelection(String playerName) {
        GamePanel panelGame = gui.getPanelGame();
        ArrayList<Player> enemies = game.getEnemiesOfPlayer(playerName);
        JComboBox<String> enemyComboBox = panelGame.getComboBoxEnemySelection();
        enemyComboBox.removeAllItems();
        for (Player enemy : enemies) {
            enemyComboBox.addItem(enemy.getName());
        }
    }

    public void setShipSelectionEnabled(boolean enabled) {
        GamePanel panelGame = gui.getPanelGame();
        panelGame.getRadioButtonDestroyer().setEnabled(enabled);
        panelGame.getRadioButtonFrigate().setEnabled(enabled);
        panelGame.getRadioButtonCorvette().setEnabled(enabled);
        panelGame.getRadioButtonSubmarine().setEnabled(enabled);
    }

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
                    // board[i][j].setBackground(GUI.UNKNOWN_COLOR);
                    board[i][j].setIcon(null);
                }
            }
        }
    }

    public void updateShipSelection(Player player) {
        GamePanel panelGame = gui.getPanelGame();
        new ShipStatusUpdater(this, panelGame, player).execute();
    }
}

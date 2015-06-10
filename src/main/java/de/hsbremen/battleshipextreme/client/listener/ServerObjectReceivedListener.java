package de.hsbremen.battleshipextreme.client.listener;

import de.hsbremen.battleshipextreme.client.Controller;
import de.hsbremen.battleshipextreme.client.GUI;
import de.hsbremen.battleshipextreme.model.Game;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.network.IServerObjectReceivedListener;
import de.hsbremen.battleshipextreme.model.network.NetworkClient;
import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.transfarableObject.*;

/**
 * Created by cschaf on 03.06.2015.
 */
public class ServerObjectReceivedListener implements IServerObjectReceivedListener {
    private GUI gui;
    private Game game;
    private NetworkClient network;
    private Controller ctrl;

    public ServerObjectReceivedListener(GUI gui, Game game, NetworkClient network, Controller ctrl) {
        this.gui = gui;
        this.game = game;
        this.network = network;
        this.ctrl = ctrl;
    }

    public void onObjectReceived(EventArgs<ITransferable> eventArgs) {
        //JOptionPane.showMessageDialog(gui.getFrame(), eventArgs.getItem().getType().toString() + ": " + eventArgs.getItem(), "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void onMessageObjectReceived(EventArgs<Message> eventArgs) {
        gui.getPanelGame().getTextAreaChatLog().append(eventArgs.getItem() + "\r\n");
    }

    public void onClientInfoObjectReceived(EventArgs<ClientInfo> eventArgs) {
        switch (eventArgs.getItem().getReason()) {
            case Connect:
                gui.getPanelGame().getTextAreaGameLog().append(eventArgs.getItem().getUsername() + " has connected\r\n");
                break;
            case Disconnect:
                gui.getPanelGame().getTextAreaGameLog().append(eventArgs.getItem().getUsername() + " has disconnected\r\n");
                break;
        }
    }

    public void onGameObjectReceived(EventArgs<NetGame> eventArgs) {
        NetGame game = eventArgs.getItem();
        ctrl.initializeClientAfterJoined(game);
        // disable all controls till game ready to start
        ctrl.setBoardsEnabled(false);
        gui.getPanelGame().getLabelInfo().setText("Waiting for other players...");
    }

    public void onTurnObjectReceived(EventArgs<Turn> eventArgs) {
        Turn turn = eventArgs.getItem();
        ctrl.selectShip(turn.getCurrentShip().getType());
        try {
            ctrl.makeOnlineTurn(turn.getAttackingPlayerName(), turn.getAttackedPlayerName(), turn.getFieldX(), turn.getFieldY(), turn.isHorizontal());
        } catch (FieldOutOfBoardException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        game.nextPlayer();
    }

    public void onGameListObjectReceived(EventArgs<GameList> eventArgs) {
        gui.getPanelServerConnection().getPnlServerGameBrowser().getTblModel().removeAllGames();
        GameList list = eventArgs.getItem();
        for (NetGame game : list.getNetGameList()) {
            gui.getPanelServerConnection().getPnlServerGameBrowser().addGameToTable(game);
        }
        ctrl.resizeServerGameListColumns();
    }

    public void onServerInfoObjectReceived(EventArgs<ServerInfo> eventArgs) {
        ServerInfo info = eventArgs.getItem();
        switch (info.getReason()) {
            case Connect:
                gui.getPanelServerConnection().getPnlServerConnectionBar().setEnabledAfterStartStop(false);
                network.getSender().requestGameList();
                break;
            case ReadyForPlacement:
                gui.getPanelGame().getLabelInfo().setText("Ships will be placed...");
                break;
            case PlaceYourShips:
                ctrl.setPlayerBoardEnabled(true);
                break;
            case GameReady:
                game.setCurrentPlayer(game.getPlayers()[0]);
                ctrl.setEnemySelectionEnabled(true);
                break;
            case MakeTurn:
                boolean reloading = ctrl.handleAllShipsAreReloading();
                if (!reloading) {
                    ctrl.setEnemyBoardEnabled(true);
                }
                else {
                    ctrl.setPlayerIsReloading(true);
                    ctrl.setDoneButtonEnabled(true);
                }

                break;
            case PlayerIsReloading:
                ctrl.setInfoLabelMessage(game.getCurrentPlayer().getName() + " is reloading...");
                game.nextPlayer();
                break;
            case PlayerWon:
                String winnerName = game.getWinner() != null ? game.getWinner().getName() : "You";
                ctrl.setInfoLabelMessage(winnerName + " won ");
                ctrl.setDoneButtonEnabled(false);
                ctrl.setEnemyBoardEnabled(false);
                break;
        }
    }

    public void onPlayerBoardsObjectReceived(EventArgs<PlayerBoards> eventArgs) {
        PlayerBoards boards = eventArgs.getItem();
        game.setPlayerBoards(boards.getBoards());
        ctrl.nextOnline();
    }

    public void onPlayerNamesObjectReceived(EventArgs<PlayerNames> eventArgs) {
        ctrl.setPlayerNames(eventArgs.getItem().getNames());
    }
}

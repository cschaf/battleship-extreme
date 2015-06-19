package de.hsbremen.battleshipextreme.client.listener;

import de.hsbremen.battleshipextreme.client.Controller;
import de.hsbremen.battleshipextreme.client.GUI;
import de.hsbremen.battleshipextreme.client.workers.LogUpdater;
import de.hsbremen.battleshipextreme.model.network.IServerObjectReceivedListener;
import de.hsbremen.battleshipextreme.model.network.NetworkClient;
import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.TransferableType;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.transfarableObject.*;

/**
 * Created by cschaf on 03.06.2015.
 */
public class ServerObjectReceivedListener implements IServerObjectReceivedListener {
    private GUI gui;
    private NetworkClient network;
    private Controller ctrl;

    public ServerObjectReceivedListener(GUI gui, NetworkClient network, Controller ctrl) {
        this.gui = gui;
        this.network = network;
        this.ctrl = ctrl;
    }

    public void onObjectReceived(EventArgs<ITransferable> eventArgs) {
        //JOptionPane.showMessageDialog(gui.getFrame(), eventArgs.getItem().getType().toString() + ": " + eventArgs.getItem(), "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void onMessageObjectReceived(EventArgs<Message> eventArgs) {
        if (eventArgs.getItem().getType() == TransferableType.Error) {
            network.getErrorHandler().errorHasOccurred(new EventArgs<ITransferable>(this, eventArgs.getItem()));
        } else {
            new LogUpdater(gui.getPanelGame().getTextAreaChatLog(), eventArgs.getItem().toString()).execute();
        }
    }

    public void onClientInfoObjectReceived(EventArgs<ClientInfo> eventArgs) {
        switch (eventArgs.getItem().getReason()) {
            case Connect:
                new LogUpdater(gui.getPanelGame().getTextAreaGameLog(), eventArgs.getItem().getUsername() + " has connected").execute();
                break;
            case Disconnect:
                new LogUpdater(gui.getPanelGame().getTextAreaGameLog(), eventArgs.getItem().getUsername() + " has disconnected").execute();
                break;
        }
    }

    public void onGameObjectReceived(EventArgs<NetGame> eventArgs) {
/*        NetGame game = eventArgs.getItem();
        //ctrl.initializeClientAfterJoined(game);
        // disable all controls till game ready to start
        ctrl.setBoardsEnabled(false);
        gui.getPanelGame().getLabelInfo().setText("Waiting for other players...");*/
    }

    public void onTurnObjectReceived(EventArgs<Turn> eventArgs) {
/*        Turn turn = eventArgs.getItem();

        //ctrl.selectShip(turn.getCurrentShip().getType());
        try {
            //ctrl.makeOnlineTurn(turn.getAttackingPlayerName(), turn.getAttackedPlayerName(), turn.getFieldX(), turn.getFieldY(), turn.isHorizontal());
        } catch (FieldOutOfBoardException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        game.nextPlayer();
        ctrl.updateShipSelection(game.getPlayerByName(game.getConnectedAsPlayer()));*/
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
                gui.getPanelServerConnection().getPnlServerGameBrowser().getBtnCreate().setEnabled(true);
                gui.getPanelServerConnection().getPnlServerGameBrowser().getBtnRefresh().setEnabled(true);
                break;
            case ReadyForPlacement:
                gui.getPanelGame().getLabelInfo().setText("Ships will be placed...");
                break;
            case PlaceYourShips:
                ctrl.setPlayerBoardEnabled(true);
                break;
            case GameReady:
                gui.getPanelGame().getButtonShowYourShips().setEnabled(true);
                ctrl.setEnemySelectionEnabled(true);
                break;
            case MakeTurn:
                boolean reloading = ctrl.handleAllShipsAreReloading();
                gui.getPanelGame().getButtonShowYourShips().setEnabled(true);
                if (!reloading) {
                    ctrl.setEnemyBoardEnabled(true);
                } else {
                    ctrl.setPlayerIsReloading(true);
                    ctrl.setDoneButtonEnabled(true);
                }

                break;
            case PlayerIsReloading:
                //ctrl.setInfoLabelMessage(game.getCurrentPlayer().getName() + " is reloading...");
                break;
            case PlayerWon:
/*                String winnerName = game.getWinner() != null ? game.getWinner().getName() : "You";
                ctrl.setInfoLabelMessage(winnerName + " won ");
                ctrl.setDoneButtonEnabled(false);
                ctrl.setEnemyBoardEnabled(false);
                ctrl.setEnemySelectionEnabled(false);*/
                break;
        }
    }

    public void onPlayerBoardsObjectReceived(EventArgs<PlayerBoards> eventArgs) {
        PlayerBoards boards = eventArgs.getItem();
        //ctrl.nextOnline();
    }

    public void onPlayerNamesObjectReceived(EventArgs<PlayerNames> eventArgs) {
        //ctrl.setPlayerNames(eventArgs.getItem().getNames());
    }
}

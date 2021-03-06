package de.hsbremen.battleshipextreme.client.listener;

import de.hsbremen.battleshipextreme.client.GUI;
import de.hsbremen.battleshipextreme.client.MultiPlayerClientController;
import de.hsbremen.battleshipextreme.client.workers.LogUpdater;
import de.hsbremen.battleshipextreme.model.network.IServerObjectReceivedListener;
import de.hsbremen.battleshipextreme.model.network.NetworkClient;
import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.TransferableObjectFactory;
import de.hsbremen.battleshipextreme.network.TransferableType;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.transfarableObject.*;

import javax.swing.*;

/**
 * Created on 03.06.2015.
 * Listener, der auf die durchs Netzwerk empfangenen Objekte reagiert.
 */
public class ServerObjectReceivedListener implements IServerObjectReceivedListener {
// ------------------------------ FIELDS ------------------------------

    private GUI gui;
    private NetworkClient network;
    private MultiPlayerClientController ctrl;

// --------------------------- CONSTRUCTORS ---------------------------

    public ServerObjectReceivedListener(GUI gui, NetworkClient network, MultiPlayerClientController ctrl) {
        this.gui = gui;
        this.network = network;
        this.ctrl = ctrl;
    }


// --------------------- Interface IServerObjectReceivedListener ---------------------

    public void onObjectReceived(EventArgs<ITransferable> eventArgs) {
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
                ctrl.setConnectedAs(eventArgs.getItem().getUsername());
                new LogUpdater(gui.getPanelGame().getTextAreaGameLog(), eventArgs.getItem().getUsername() + " has connected").execute();
                break;
            case Disconnect:
                new LogUpdater(gui.getPanelGame().getTextAreaGameLog(), eventArgs.getItem().getUsername() + " has disconnected").execute();
                break;
            case GameOver:
                if (!ctrl.getConnectedAs().equals(eventArgs.getItem().getUsername()) && !ctrl.getPlayer().hasLost()) {
                    ComboBoxModel model = gui.getPanelGame().getComboBoxEnemySelection().getModel();
                    for (int i = 0; i < model.getSize(); i++) {
                        if (model.getElementAt(i).equals(eventArgs.getItem().getUsername())) {
                            gui.getPanelGame().getComboBoxEnemySelection().removeItemAt(i);
                            return;
                        }
                    }
                }
                break;
        }
    }

    public void onGameObjectReceived(EventArgs<NetGame> eventArgs) {
        NetGame game = eventArgs.getItem();
        ctrl.initializeClientAfterJoined(game);
        ctrl.setPlayerName(ctrl.getConnectedAs());
        gui.getPanelGame().getLabelInfo().setText("Waiting for other players...");
    }

    public void onTurnObjectReceived(EventArgs<Turn> eventArgs) {

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
                ctrl.setIsReady(true);
                ctrl.setEnemySelectionEnabled(true);
                break;
            case MakeTurn:
                if (ctrl.getPlayer().hasLost()) {
                    network.getSender().sendTurn(TransferableObjectFactory.CreateTurn(ctrl.getPlayer().getName()));
                } else {
                    ctrl.updateShipSelection();
                    boolean reloading = ctrl.handleAllShipsAreReloading();

                    if (!reloading) {
                        ctrl.updateCurrentShip();
                        ctrl.setEnemyBoardEnabled(true);
                    } else {
                        ctrl.setPlayerIsReloading(true);
                        ctrl.setDoneButtonEnabled(true);
                    }
                }

                break;
        }
    }

    public void onPlayerNamesObjectReceived(EventArgs<PlayerNames> eventArgs) {
        ctrl.setPlayerNames(eventArgs.getItem().getNames());
    }

    public void onClientTurnObjectReceived(EventArgs<ClientTurn> eventArgs) {
        ctrl.handleClientTurn(eventArgs.getItem());
    }
}

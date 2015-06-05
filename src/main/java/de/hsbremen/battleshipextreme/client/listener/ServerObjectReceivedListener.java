package de.hsbremen.battleshipextreme.client.listener;

import de.hsbremen.battleshipextreme.client.Controller;
import de.hsbremen.battleshipextreme.client.GUI;
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
        ctrl.setBoardsEnabled(false);
        gui.getPanelGame().getLabelInfo().setText("Waiting for other players...");
    }

    public void onTurnObjectReceived(EventArgs<Turn> eventArgs) {
        Turn turn = eventArgs.getItem();
        gui.getPanelGame().getTextAreaGameLog().append(turn.getFrom().getName() + " attacked " + turn.getTo().getName() + " " + turn.getOrientation().toString() + " at field " + turn.getFieldX() + " / " + turn.getFieldY() + "\r\n");
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
        }
    }

    public void onPlayerBoardsObjectReceived(EventArgs<PlayerBoards> eventArgs) {
        PlayerBoards boards = eventArgs.getItem();
        //TODO
    }
}

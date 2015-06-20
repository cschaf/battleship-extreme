package de.hsbremen.battleshipextreme.model.network;
import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.transfarableObject.*;

import java.util.EventListener;

/**
 * Created by cschaf on 15.05.2015.
 */
public interface IServerObjectReceivedListener extends EventListener{
    void onObjectReceived(EventArgs<ITransferable> eventArgs);
    void onMessageObjectReceived(EventArgs<Message> eventArgs);
    void onClientInfoObjectReceived(EventArgs<ClientInfo> eventArgs);
    void onGameObjectReceived(EventArgs<NetGame> eventArgs);
    void onTurnObjectReceived(EventArgs<Turn> eventArgs);
    void onGameListObjectReceived(EventArgs<GameList> eventArgs);
    void onServerInfoObjectReceived(EventArgs<ServerInfo> eventArgs);
    void onPlayerNamesObjectReceived(EventArgs<PlayerNames> eventArgs);

    void onClientTurnObjectReceived(EventArgs<ClientTurn> eventArgs);
}

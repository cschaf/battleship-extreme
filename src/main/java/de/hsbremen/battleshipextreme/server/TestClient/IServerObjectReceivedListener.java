package de.hsbremen.battleshipextreme.server.TestClient;
import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.transfarableObject.ClientInfo;
import de.hsbremen.battleshipextreme.network.transfarableObject.Message;

import java.util.EventListener;

/**
 * Created by cschaf on 15.05.2015.
 */
public interface IServerObjectReceivedListener extends EventListener{
    void onObjectReceived(EventArgs<ITransferable> eventArgs);
    void onMessageObjectReceived(EventArgs<Message> eventArgs);
    void onClientInfoObjectReceived(EventArgs<ClientInfo> eventArgs);
}

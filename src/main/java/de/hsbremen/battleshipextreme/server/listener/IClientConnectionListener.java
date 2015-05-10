package de.hsbremen.battleshipextreme.server.listener;
import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;

import java.util.EventListener;

/**
 * Created by cschaf on 25.04.2015.
 */
public interface IClientConnectionListener extends EventListener {
    void onClientHasConnected(EventArgs<ITransferable> eventArgs);
    void onClientHasDisconnected(EventArgs<ITransferable> eventArgs);
}

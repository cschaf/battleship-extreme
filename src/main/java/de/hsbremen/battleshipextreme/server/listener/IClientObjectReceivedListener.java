package de.hsbremen.battleshipextreme.server.listener;


import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;

import java.util.EventListener;

/**
 * Created on 25.04.2015.
 * Interface für das reagieren auf das Empfangen von Objekten über das Netzwerk
 */
public interface IClientObjectReceivedListener extends EventListener{
    void onObjectReceived(EventArgs<ITransferable> eventArgs);
}

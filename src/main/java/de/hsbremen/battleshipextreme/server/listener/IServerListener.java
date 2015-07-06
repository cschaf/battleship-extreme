package de.hsbremen.battleshipextreme.server.listener;

import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;

import java.util.EventListener;

/**
 * Created on 25.04.2015.
 * Interface für das reagieren auf Fehler
 */
public interface IServerListener extends EventListener {
    void onInfo(EventArgs<ITransferable> eventArgs);
}

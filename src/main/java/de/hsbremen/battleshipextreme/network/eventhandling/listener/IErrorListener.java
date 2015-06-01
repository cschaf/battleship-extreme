package de.hsbremen.battleshipextreme.network.eventhandling.listener;

import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;

import java.util.EventListener;

/**
 * Created by cschaf on 25.04.2015.
 */
public interface IErrorListener extends EventListener {
    void onError(EventArgs<ITransferable> eventArgs);
}

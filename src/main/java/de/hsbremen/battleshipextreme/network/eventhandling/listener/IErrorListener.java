package de.hsbremen.battleshipextreme.network.eventhandling.listener;

import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;

import java.util.EventListener;

/**
 * Created by cschaf on 25.04.2015.
 * Dient zur Weitergabe von Fehlern über ein Events
 */
public interface IErrorListener extends EventListener {
    /**
     * Methode die aufgerufen wird, wenn ein Fehler als Event geworfen werden soll
     * @param eventArgs
     */
    void onError(EventArgs<ITransferable> eventArgs);
}

package de.hsbremen.battleshipextreme.network.eventhandling;

import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.eventhandling.listener.IErrorListener;

import javax.swing.event.EventListenerList;

/**
 * Created on 25.04.2015.
 * Verwaltet und benachrichtigt IErrorListener
 */
public class ErrorHandler {
    protected EventListenerList listeners = new EventListenerList();

    /**
     * Fügt einen IErrorListener der Listenerlist hinzu
     * @param listener
     */
    public void addErrorListener(IErrorListener listener) {
        this.listeners.add(IErrorListener.class, listener);
    }

    /**
     * Entfernt einen IErrorListener von der Listenerlist
     * @param listener
     */
    public void removeErrorListener(IErrorListener listener) {
        this.listeners.remove(IErrorListener.class, listener);
    }

    /**
     * Sendet ein Eventobject an alle registrierten Listeners
     * @param eventArgs
     */
    public void errorHasOccurred(EventArgs<ITransferable> eventArgs) {
        Object[] listeners = this.listeners.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] ==IErrorListener.class){
                ((IErrorListener) listeners[i + 1]).onError(eventArgs);
            }
        }
    }
}

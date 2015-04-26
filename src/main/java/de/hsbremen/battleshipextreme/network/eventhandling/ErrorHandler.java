package de.hsbremen.battleshipextreme.network.eventhandling;

import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.eventhandling.listener.IErrorListener;

import javax.swing.event.EventListenerList;

/**
 * Created by cschaf on 25.04.2015.
 */
public class ErrorHandler {
    protected EventListenerList listeners = new EventListenerList();

    public void addErrorListender(IErrorListener listener) {
        this.listeners.add(IErrorListener.class, listener);
    }

    public void removeErrorListener(IErrorListener listener) {
        this.listeners.remove(IErrorListener.class, listener);
    }

    public void errorHasOccurred(EventArgs<ITransferable> eventArgs) {
        Object[] listeners = this.listeners.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] ==IErrorListener.class){
                ((IErrorListener) listeners[i + 1]).onError(eventArgs);
            }
        }
    }
}

package de.hsbremen.battleshipextreme.server.TestClient;

import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.eventhandling.listener.IErrorListener;


/**
 * Created by cschaf on 15.05.2015.
 */
public class ErrorListener implements IErrorListener {

    @Override
    public void onError(EventArgs<ITransferable> eventArgs) {
        System.out.println(eventArgs.getItem());
    }
}

package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.InfoSendingReason;

/**
 * Created by cschaf on 31.05.2015.
 */
public class ServerInfo extends TransferInfo {

    public ServerInfo(InfoSendingReason reason) {
        this.reason = reason;
    }
}

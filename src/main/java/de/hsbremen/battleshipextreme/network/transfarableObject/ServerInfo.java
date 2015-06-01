package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.network.InfoSendingReason;
import de.hsbremen.battleshipextreme.network.TransferableType;

/**
 * Created by cschaf on 31.05.2015.
 */
public class ServerInfo extends TransferInfo {

    public TransferableType getType() {
        return TransferableType.ServerInfo;
    }

    public ServerInfo(InfoSendingReason reason) {
        this.reason = reason;
    }
}

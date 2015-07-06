package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.network.InfoSendingReason;
import de.hsbremen.battleshipextreme.network.TransferableType;

/**
 * Created by cschaf on 31.05.2015.
 * Info vom Server für die Clients, verwendet für
 * einfache Ereignisse, die keine weiteren daten ben�tigen
 */
public class ServerInfo extends TransferInfo {

    public ServerInfo(InfoSendingReason reason) {
        this.reason = reason;
    }

    public TransferableType getType() {
        return TransferableType.ServerInfo;
    }
}

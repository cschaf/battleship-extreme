package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.network.InfoSendingReason;
import de.hsbremen.battleshipextreme.network.TransferableType;

/**
 * Created on 25.04.2015.
 * Information für die Übertragung
 */
public abstract class TransferInfo extends TransferableObject {

    protected InfoSendingReason reason;

    public TransferInfo() {
        this.reason = InfoSendingReason.Default;
    }

    public TransferInfo(InfoSendingReason reason) {
        this.reason = reason;
    }

    public InfoSendingReason getReason() {
        return reason;
    }

    public TransferableType getType() {
        return TransferableType.TransferInfo;
    }
}

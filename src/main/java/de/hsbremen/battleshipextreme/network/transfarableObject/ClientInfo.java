package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.network.InfoSendingReason;
import de.hsbremen.battleshipextreme.network.TransferableType;

/**
 * Created by cschaf on 25.04.2015.
 */
public class ClientInfo extends TransferInfo {

    private String username = null;

    public ClientInfo(String username, String ip, int port) {
        super(ip, port, InfoSendingReason.Default);
        this.username = username;
    }

    public ClientInfo(String username, String ip, int port, InfoSendingReason reason) {
        super(ip, port, reason);
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    @Override
    public TransferableType getType() {
        return TransferableType.ClientInfo;
    }
}

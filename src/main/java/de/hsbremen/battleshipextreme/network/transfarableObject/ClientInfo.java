package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.network.InfoSendingReason;
import de.hsbremen.battleshipextreme.network.TransferableType;

/**
 * Created by cschaf on 25.04.2015.
 */
public class ClientInfo extends TransferInfo {

    private String username = null;

    public ClientInfo(String username, String ip, int port) {
        super(ip, port);
        this.username = username;
        this.reason = InfoSendingReason.Info;
    }

    public ClientInfo(String username, String ip, int port, InfoSendingReason reason) {
        this(username, ip, port);
        this.reason = reason;
    }

    public String getUsername() {
        return this.username;
    }

    @Override
    public TransferableType getType() {
        return TransferableType.ClientInfo;
    }
}

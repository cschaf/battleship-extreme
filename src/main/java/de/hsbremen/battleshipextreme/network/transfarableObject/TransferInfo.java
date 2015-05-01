package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.network.InfoSendingReason;
import de.hsbremen.battleshipextreme.network.TransferableType;

/**
 * Created by cschaf on 25.04.2015.
 */
public abstract class TransferInfo extends TransferableObject {
    protected String ip;
    protected int port;
    protected InfoSendingReason reason;

    public TransferInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.reason = InfoSendingReason.Default;
    }

    public TransferInfo(String ip, int port, InfoSendingReason reason) {
        this.ip = ip;
        this.port = port;
        this.reason = reason;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public InfoSendingReason getReason() {
        return reason;
    }

    public TransferableType getType() {
        return TransferableType.TransferInfo;
    }
}

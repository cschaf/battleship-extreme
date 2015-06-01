package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.network.InfoSendingReason;
import de.hsbremen.battleshipextreme.network.TransferableType;

/**
 * Created by cschaf on 25.04.2015.
 */
public class ClientInfo extends TransferInfo {
    protected String ip;
    protected int port;
    private String username = null;

    public ClientInfo(String username, String ip, int port) {
        super();
        this.ip = ip;
        this.port = port;
        this.username = username;
    }

    public ClientInfo(String username, String ip, int port, InfoSendingReason reason) {
        super(reason);
        this.ip = ip;
        this.port = port;
        this.username = username;
    }

    public String getUsername() {
        return this.username;
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

    @Override
    public TransferableType getType() {
        return TransferableType.ClientInfo;
    }
}

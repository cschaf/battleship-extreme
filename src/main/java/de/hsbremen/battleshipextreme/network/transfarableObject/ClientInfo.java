package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.network.InfoSendingReason;
import de.hsbremen.battleshipextreme.network.TransferableType;

/**
 * Created by cschaf on 25.04.2015.
 *
 * Beinhaltet Informationen über einen Client
 */
public class ClientInfo extends TransferInfo {
    protected String ip; // IP-Adresse des Client
    protected int port; // Port des Clients
    private String username = null; // Name des Clients

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

    /**
     * Gibt den Benutzernamen des Clients zurück
     * @return
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Gibt die IP-Adresse des Clients zurück
     * @return
     */
    public String getIp() {
        return ip;
    }

    /**
     * Setzt die IP-Adresse des Clients
     * @param ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Gibt den Port des Clients zurück
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * Setzt den Port des Clients
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public TransferableType getType() {
        return TransferableType.ClientInfo;
    }
}

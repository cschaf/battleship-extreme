package de.hsbremen.battleshipextreme.network;

import de.hsbremen.battleshipextreme.network.transfarableObject.ClientInfo;
import de.hsbremen.battleshipextreme.network.transfarableObject.ClientMessage;
import de.hsbremen.battleshipextreme.network.transfarableObject.Message;

/**
 * Created by cschaf on 26.04.2015.
 */
public class TransferableObjectFactory {
    public static ITransferable CreateClientMessage(String message, ITransferable sender) {
        return new ClientMessage(message, sender);
    }

    public static ITransferable CreateClientInfo(String username, String ip, int port) {
        return new ClientInfo(username, ip, port);
    }

    public static ITransferable CreateClientInfo(String username, String ip, int port, InfoSendingReason reason) {
        return new ClientInfo(username, ip, port, reason);
    }

    public static ITransferable CreateMessage(String message) {
        return new Message(message);
    }
}

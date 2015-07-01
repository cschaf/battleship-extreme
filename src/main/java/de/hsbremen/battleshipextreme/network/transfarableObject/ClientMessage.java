package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.TransferableType;

/**
 * Created by cschaf on 25.04.2015.
 *
 * Beinhaltet Informationen einen Client und seiner Nachricht
 */
public class ClientMessage extends Message {
    private ClientInfo info; // Informationen über den CLient, welcher die Nachricht sendet

    public ClientMessage(String message, ITransferable sender) {
        super(message);
        this.info = (ClientInfo) sender;
    }

    @Override
    public TransferableType getType() {
        return TransferableType.ClientMessage;
    }

    @Override
    public String toString() {
        return this.info.getUsername() + "(" + this.info.getPort() + "): " + this.getMessage() + " - " + String.format("%1$TT", this.getCreatedAt());
    }
}

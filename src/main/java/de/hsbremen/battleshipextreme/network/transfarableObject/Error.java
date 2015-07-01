package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.network.TransferableType;

/**
 * Created by cschaf on 13.06.2015.
 * Beinhaltet eine Fehlernachricht
 */
public class Error extends Message {

    /**
     * Konstuktor
     * @param message Nachricht
     */
    public Error(String message) {
        super(message);
    }

    public TransferableType getType() {
        return TransferableType.Error;
    }
}

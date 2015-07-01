package de.hsbremen.battleshipextreme.network;

/**
 * Created by cschaf on 25.04.2015.
 */
public interface ITransferable {

    /**
     * Type eines Obkektes, was über das netzwerk übertragen wird
     */
    TransferableType getType();
}

package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.network.ITransferable;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created on 25.04.2015.
 * Vaterklasse für jedes Objekt was über das Netzwerk übertragen wird
 */
public abstract class TransferableObject implements Serializable, ITransferable {
    private Timestamp createdAt;

    /**
     * Konstuktor
     */
    protected TransferableObject() {
        this.setCreatedAt(new Timestamp(new Date().getTime()));
    }

    /**
     * Gibt den Zeitstempel der Erzeugung des Objectes zurück
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Setzt den Zeitstempel des Objektes
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return this.getType().toString() + " created at: " + this.getCreatedAt();
    }
}

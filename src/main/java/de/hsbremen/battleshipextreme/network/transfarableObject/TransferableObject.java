package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.network.ITransferable;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by cschaf on 25.04.2015.
 */
public abstract class TransferableObject implements Serializable, ITransferable {
    private Timestamp createdAt;

    /**
     * Constructor
     */
    protected TransferableObject() {
        this.setCreatedAt(new Timestamp(new Date().getTime()));
    }

    /**
     * Returns the timestamp for the creation date of the object
     * @return
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Set the timestamp for the creation date of the object
     * @param createdAt
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "created at: " + this.getCreatedAt();
    }
}

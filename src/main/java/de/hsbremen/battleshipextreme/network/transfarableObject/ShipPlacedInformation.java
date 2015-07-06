package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.ship.ShipType;
import de.hsbremen.battleshipextreme.network.TransferableType;

/**
 * Created on 19.06.2015.
 * Beinhaltet alle Informationen, die für das Setzten eines Client Schiffes n�tig sind
 */
public class ShipPlacedInformation extends TransferableObject {
    private int xPos;
    private int yPos;
    private Orientation orientation;
    private ShipType shipType;

    public ShipPlacedInformation(int xPos, int yPos, Orientation orientation, ShipType type) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.orientation = orientation;
        this.shipType = type;
    }

    public TransferableType getType() {
        return TransferableType.ShipPlacedInformation;
    }

    public int getX() {
        return xPos;
    }

    public int getY() {
        return yPos;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public ShipType getShipType() {
        return shipType;
    }
}

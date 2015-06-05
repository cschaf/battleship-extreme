package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.network.TransferableType;

import java.util.ArrayList;

/**
 * Created by cschaf on 05.06.2015.
 */
public class PlayerBoards extends TransferableObject {
    private ArrayList<FieldState[][]> boards;

    public PlayerBoards(ArrayList<FieldState[][]> boards) {
        this.boards = boards;
    }

    public TransferableType getType() {
        return TransferableType.PlayerBoards;
    }
}

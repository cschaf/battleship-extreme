package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.network.TransferableType;

/**
 * Created by cschaf on 05.06.2015.
 */
public class ClientBoard extends TransferableObject {
    private Board board;

    public  ClientBoard(Board board){
        this.board = board;
    }

    public TransferableType getType() {
        return TransferableType.ClientBoard;
    }

    public Board getBoard() {
        return board;
    }
}

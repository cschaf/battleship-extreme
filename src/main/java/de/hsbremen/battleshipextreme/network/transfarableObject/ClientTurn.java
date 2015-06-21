package de.hsbremen.battleshipextreme.network.transfarableObject;
import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.network.TransferableType;
/**
 * Created by cschaf on 20.06.2015.
 */
public class ClientTurn extends TransferableObject {
    private Field[] fields;
    private boolean isReloading;
    private boolean isWinner;
    private String winnerName;
    private String attackingPlayerName;
    private String attackedPlayerName;

    public ClientTurn(Field[] fields, boolean isReloading, String attackingPlayerName, String attackedPlayerName) {
        this.fields = fields;
        this.isReloading = isReloading;
        this.attackingPlayerName = attackingPlayerName;
        this.attackedPlayerName = attackedPlayerName;
    }

    public ClientTurn(String winnerName) {
        this.winnerName = winnerName;
        this.isWinner = true;
    }

    public TransferableType getType() {
        return TransferableType.ClientTurn;
    }

    public boolean isReloading() {
        return isReloading;
    }

    public Field[] getFields() {
        return fields;
    }

    public String getAttackingPlayerName() {
        return attackingPlayerName;
    }

    public String getAttackedPlayerName() {
        return attackedPlayerName;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public void setIsWinner(boolean isWinner) {
        this.isWinner = isWinner;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }
}

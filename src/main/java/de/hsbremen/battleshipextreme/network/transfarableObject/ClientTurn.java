package de.hsbremen.battleshipextreme.network.transfarableObject;
import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.network.TransferableType;
/**
 * Created by cschaf on 20.06.2015.
 * Beinhaltet die Spielzuginformationen, welche vom Server an den Client gesendet wird
 */
public class ClientTurn extends TransferableObject {
    private Field[] fields; // Alle Felder die beschossen werden
    private boolean isReloading; // gibt an ob der Client in diesem Zug nachlädt
    private boolean isWinner; // gibt an ob der client gewonnen hat
    private String winnerName; // Name des Gewinners
    private String attackingPlayerName; // Name des angreifenden Spielers
    private String attackedPlayerName; // Name des angegriffenden Spielers

    /**
     * Konstruktor
     * @param fields beschossene Felder
     */
    public ClientTurn(Field[] fields, boolean isReloading, String attackingPlayerName, String attackedPlayerName) {
        this.fields = fields;
        this.isReloading = isReloading;
        this.attackingPlayerName = attackingPlayerName;
        this.attackedPlayerName = attackedPlayerName;
    }

    /**
     * Konstruktor
     * @param winnerName Name des Gewinners
     */
    public ClientTurn(String winnerName) {
        this.winnerName = winnerName;
        this.isWinner = true;
    }

    /**
     * Gibt den  Type des Netzwerk-Objektes zurück
     */
    public TransferableType getType() {
        return TransferableType.ClientTurn;
    }

    /**
     * Gibt zurück ob der Spieler am Nachladen ist
     */
    public boolean isReloading() {
        return isReloading;
    }

    /**
     * Gibt die beschossenden Felder zurück
     */
    public Field[] getFields() {
        return fields;
    }

    /**
     * Gibt den angreifenden Spielernamen zurück
     */
    public String getAttackingPlayerName() {
        return attackingPlayerName;
    }

    /**
     * Gibt den angegriffenden Spielernamen zurück
     */
    public String getAttackedPlayerName() {
        return attackedPlayerName;
    }

    /**
     * Gibt zurück ob es einen Gewinner gibt
     */
    public boolean isWinner() {
        return isWinner;
    }

    /**
     * Setzt den Wert ob es einen Gewinner gibt
     */
    public void setIsWinner(boolean isWinner) {
        this.isWinner = isWinner;
    }

    /**
     * Gibt den Names des Gewinner zurück
     */
    public String getWinnerName() {
        return winnerName;
    }

    /**
     * Setzt den Namen des Gewinners
     */
    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }
}

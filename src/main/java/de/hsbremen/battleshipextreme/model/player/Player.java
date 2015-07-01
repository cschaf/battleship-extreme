package de.hsbremen.battleshipextreme.model.player;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.exception.ShipAlreadyPlacedException;
import de.hsbremen.battleshipextreme.model.exception.ShipOutOfBoardException;
import de.hsbremen.battleshipextreme.model.ship.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Die abstrakte Klasse Player dient als Grundlage für die HumanPlayer- und
 * AIPlayer-Klasse.
 */

public abstract class Player implements Serializable {
    private static final long serialVersionUID = -8473281041556043384L;
    protected String name;
    protected Ship[] ships;
    protected Ship currentShip;
    protected PlayerType type;
    protected Board board;

    /**
     * Dient zum Initialisieren der Schiffe und des Boards anhand der
     * übergebenen Parameter.
     * @param boardSize Größe des Boards
     * @param destroyers Anzahl der Zerstörer
     * @param frigates Anzahl der Frigatten
     * @param corvettes Anzahl der Korvetten
     * @param submarines Anzahl der U-Boote
     */
    public Player(int boardSize, int destroyers, int frigates, int corvettes, int submarines) {
        initShips(destroyers, frigates, corvettes, submarines);
        this.board = new Board(boardSize);
        this.currentShip = this.ships[0];
    }

    public Player(int boardSize, HashMap<Ship, ArrayList<Field>> shipMap) {
        this.board = new Board(boardSize);
        ships = new Ship[shipMap.size()];
        int counter = 0;
        for (Map.Entry<Ship, ArrayList<Field>> entry : shipMap.entrySet()) {
            Ship key = entry.getKey();
            ArrayList<Field> value = entry.getValue();
            switch (key.getType()) {
                case DESTROYER:
                    Ship destroyer = new Destroyer();
                    ships[counter] = destroyer;
                    for (int fieldIndex = 0; fieldIndex < value.size(); fieldIndex++) {
                        Field field = value.get(fieldIndex);
                        board.getFields()[field.getYPos()][field.getXPos()].setShip(destroyer);
                    }
                    break;

                case FRIGATE:
                    Frigate frigate = new Frigate();
                    ships[counter] = frigate;
                    for (int fieldIndex = 0; fieldIndex < value.size(); fieldIndex++) {
                        Field field = value.get(fieldIndex);
                        board.getFields()[field.getYPos()][field.getXPos()].setShip(frigate);
                    }
                    break;

                case CORVETTE:
                    Corvette corvette = new Corvette();
                    ships[counter] = corvette;
                    for (int fieldIndex = 0; fieldIndex < value.size(); fieldIndex++) {
                        Field field = value.get(fieldIndex);
                        board.getFields()[field.getYPos()][field.getXPos()].setShip(corvette);
                    }
                    break;

                case SUBMARINE:
                    Submarine submarine = new Submarine();
                    ships[counter] = submarine;
                    for (int fieldIndex = 0; fieldIndex < value.size(); fieldIndex++) {
                        Field field = value.get(fieldIndex);
                        board.getFields()[field.getYPos()][field.getXPos()].setShip(submarine);
                    }
                    break;
            }
            counter++;
        }
        this.currentShip = this.ships[0];
    }

    /**
     * Die Methode dient zum Initialisieren der Schiffe.
     */
    private void initShips(int destroyers, int frigates, int corvettes, int submarines) {
        ships = new Ship[destroyers + frigates + corvettes + submarines];
        for (int i = 0; i < ships.length; i++) {
            if (i < destroyers) {
                ships[i] = new Destroyer();
            } else if (i < destroyers + frigates) {
                ships[i] = new Frigate();
            } else if (i < destroyers + frigates + corvettes) {
                ships[i] = new Corvette();
            } else {
                ships[i] = new Submarine();
            }
        }
    }

    private void initShips(HashMap<Ship, ArrayList<Field>> shipMap) {
        ships = new Ship[shipMap.size()];
    }

    /**
     * Dient zum Zurücksetzen eines Boards.
     */
    public void resetBoard() {
        int size = board.getSize();
        board = new Board(size);
        for (Ship ship : ships) {
            ship.setPlaced(false);
        }
        currentShip = ships[0];
    }

    /**
     * Liefert alle Feldzustände des Playerboards abhängig davon, ob es das
     * eigene Board oder das Board eines Gegners ist. Es werden nur Feldzustände
     * zurückgegeben, die der Spieler wissen darf.
     * @param isOwnBoard gibt an, ob es sich um das eigene Board handelt
     */
    public FieldState[][] getFieldStates(boolean isOwnBoard) throws FieldOutOfBoardException {
        int size = board.getSize();
        FieldState[][] fieldStates = new FieldState[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                FieldState state = board.getField(j, i).getState();
                if ((state == FieldState.HAS_SHIP || state == FieldState.IS_EMPTY) && (!isOwnBoard)) {
                    fieldStates[i][j] = null;
                } else {
                    fieldStates[i][j] = state;
                }
            }
        }
        return fieldStates;
    }

    public FieldState[][] getFieldWithStateEmpty() {
        int size = board.getSize();
        FieldState[][] fieldStates = new FieldState[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                fieldStates[i][j] = FieldState.IS_EMPTY;
            }
        }
        return fieldStates;
    }

    /**
     * Prüft ob es möglich ist, ein Schiff an eine bestimmten Position zu
     * platzieren. Wenn ja, dann wird das Schiff platziert und true
     * zurückgegeben, ansonsten wird false zurückgegeben.
     * @param xPos X-Koordinate
     * @param yPos Y-Koordinate
     * @param orientation Ausrichtung
     * @return true, wenn das Schiff an dieser Stelle platziert werden kann,
     * false wenn nicht
     * @throws ShipAlreadyPlacedException wenn das Schiff bereits platziert wurde
     * @throws FieldOutOfBoardException wenn sich das Feld nicht im Board befindet
     * @throws ShipOutOfBoardException wenn sich das Schiff (teilweise) nicht im Board befindet
     */
    public boolean placeShip(int xPos, int yPos, Orientation orientation) throws ShipAlreadyPlacedException, FieldOutOfBoardException, ShipOutOfBoardException {
        if (isItPossibleToPlaceShip(xPos, yPos, orientation)) {
            placeShipOnBoard(this.currentShip, xPos, yPos, orientation);
            return true;
        }
        return false;
    }

    /**
     * Prüft ob es möglich ist, das Schiff an der übergebenen Position zu
     * platzieren.
     * @return true, wenn es möglich ist, false wenn nicht
     */
    public boolean isItPossibleToPlaceShip(int xPos, int yPos, Orientation orientation) throws ShipOutOfBoardException, ShipAlreadyPlacedException, FieldOutOfBoardException {
        if (this.currentShip.isPlaced()) {
            throw new ShipAlreadyPlacedException(this.currentShip);
        }

        if (!this.board.containsFieldAtPosition(xPos, yPos)) {
            throw new FieldOutOfBoardException(new Field(xPos, yPos));
        }

        if (isShipPartiallyOutOfBoard(this.currentShip, xPos, yPos, orientation)) {
            throw new ShipOutOfBoardException(this.currentShip);
        }

        Field occupiedField = findOccupiedField(this.currentShip, xPos, yPos, orientation);
        if (occupiedField != null) {
            return false;
        }
        return true;
    }

    /**
     * Prüft ob Felder an der gewünschten Position bereits belegt sind. Dabei
     * wird berücksichtigt, dass um das Schiff herum ein Feld frei bleiben muss.
     * @return das erste belegte Feld, das gefunden wurde, null wenn es kein
     * belegtes Feld gibt
     */
    private Field findOccupiedField(Ship ship, int xPos, int yPos, Orientation orientation) {
        Field[][] fields = this.board.getFields();
        // Orientation Horizontal
        if (orientation == Orientation.HORIZONTAL) {

            // Felder prüfen ob bereits belegt
            for (int y = yPos - 1; y <= yPos + 1; y++)
                for (int x = xPos - 1; x <= xPos + ship.getSize(); x++)
                    // x und y innerhalb des Spielfeldes
                    if (x >= 0 && y >= 0 && x < fields.length && y < fields.length) {
                        if (fields[y][x].getShip() != null) {
                            return (fields[y][x]);
                        }
                    }
        }

        // Orientation Vertical
        if (orientation == Orientation.VERTICAL) {
            // Felder prüfen ob bereits belegt
            for (int y = yPos - 1; y <= yPos + ship.getSize(); y++)
                for (int x = xPos - 1; x <= xPos + 1; x++)
                    // x und y innerhalb des Spielfeldes
                    if (x >= 0 && y >= 0 && x < fields.length && y < fields.length) {
                        if (fields[y][x].getShip() != null) // Feld hat Schiff
                        {
                            return (fields[y][x]);
                        }
                    }
        }
        return null;
    }

    /**
     * Prüft ob sich das Schiff (teilweise) außerhalb des Boards befindet.
     * @return true, wenn sich das Schiff außerhalb des Boards befindet, false
     * wenn nicht
     */
    private boolean isShipPartiallyOutOfBoard(Ship ship, int xPos, int yPos, Orientation orientation) {
        int xDirection = orientation == Orientation.HORIZONTAL ? 1 : 0;
        int yDirection = orientation == Orientation.VERTICAL ? 1 : 0;
        int x = xPos + ship.getSize() * xDirection - 1;
        int y = yPos + ship.getSize() * yDirection - 1;
        return (x >= board.getSize()) || (y >= board.getSize());
    }

    /**
     * Setzt das Schiff auf die Felder vom Board.
     */
    private void placeShipOnBoard(Ship ship, int xPos, int yPos, Orientation orientation) {
        int xDirection = orientation == Orientation.HORIZONTAL ? 1 : 0;
        int yDirection = orientation == Orientation.VERTICAL ? 1 : 0;
        for (int i = 0; i < ship.getSize(); i++) {
            board.getFields()[yPos + i * yDirection][xPos + i * xDirection].setShip(ship);
        }
        ship.place();
    }

    /**
     * Prüft ob der Spieler alle seine Schiffe gesetzt hat.
     * @return true, wenn alle Schiffe gesetzt wurden, false wenn nicht.
     */
    public boolean hasPlacedAllShips() {
        boolean arePlaced = true;

        for (Ship ship : this.ships) {
            if (!ship.isPlaced()) {
                arePlaced = false;
                break;
            }
        }

        return arePlaced;
    }

    /**
     * prüft ob der Player das übergebene Schiff tatsächlich besitzt
     * @return gibt true zurück, wenn ja, false wenn nicht.
     */
    private boolean possessesShip(Ship ship) {
        return Arrays.asList(this.getShips()).contains(ship);
    }

    /**
     * Setzt das currentShip auf den das nächste Schiff. Die Methode wird beim
     * Setzen der Schiffe verwendet.
     */
    public void nextShip() {
        int currentShipIndex = Arrays.asList(this.ships).indexOf(this.currentShip);
        if (currentShipIndex < ships.length - 1) {
            currentShipIndex++;
            currentShip = ships[currentShipIndex];
        }
    }

    /**
     * Liefert alle Schiffe die nicht zerstört wurden. Zusätzlich können Schiffe
     * gefiltert werden, die nachladen.
     * @return eine Liste von Schiffen die benutzbar sind
     */
    public ArrayList<Ship> getAvailableShips(boolean excludeReloadingShips) {
        ArrayList<Ship> availableShips = new ArrayList<Ship>();
        for (Ship ship : ships) {
            if (!ship.isDestroyed()) {
                if (excludeReloadingShips) {
                    if (!ship.isReloading()) {
                        availableShips.add(ship);
                    }
                } else {
                    availableShips.add(ship);
                }
            }
        }
        return availableShips;
    }

    /**
     * Dient zum Setzen des aktuellen Schiffs.
     */
    public void selectShip(Ship ship) throws Exception {
        if (!possessesShip(ship)) {
            throw new Exception("Player does not possess ship!");
        }
        this.currentShip = ship;
    }

    /**
     * Markiert das Spieler-Board an der übergebenen Position. Gibt false
     * zurück, wenn ein Schuss nicht ausgeführt werden kann.
     * @return true, wenn das Board an der übergebenen Position markiert werden
     * konnte, false wenn nicht
     */
    public boolean markBoard(int x, int y) throws FieldOutOfBoardException {
        // Schüsse ignorieren, die außerhalb des Feldes liegen
        if (board.containsFieldAtPosition(x, y)) {
            Field fieldShotAt = board.getField(x, y);
            // wenn Board schon beschossen wurde, dann Schuss ignorieren
            if (!fieldShotAt.isHit()) {
                board.getField(x, y).mark();
                // wenn das Feld auf das geschossen wurde ein Schiff hat,
                // dann ein Leben vom Schiff abziehen
                if (fieldShotAt.hasShip()) {
                    Ship ship = fieldShotAt.getShip();
                    ship.decreaseSize();
                }
            } else {
                // Feld bereits beschossen
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Prüft ob alle Schiffe nachladen.
     * @return true wenn alle Schiffe nachladen, false wenn nicht
     */
    public boolean areAllShipsReloading() {
        ArrayList<Ship> availableShips = this.getAvailableShips(true);
        return availableShips.size() <= 0;
    }

    public Ship getCurrentShip() {
        return this.currentShip;
    }

    public void setCurrentShip(Ship currentShip) {
        this.currentShip = currentShip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Ship[] getShips() {
        return ships;
    }

    public void setShips(Ship[] ships) {
        this.ships = ships;
    }

    public String toString() {
        return this.name;
    }

    public boolean hasLost() {
        for (Ship ship : ships) {
            if (!ship.isDestroyed()) {
                return false;
            }
        }
        return true;
    }

    public PlayerType getType() {
        return type;
    }

    public void setType(PlayerType type) {
        this.type = type;
    }

    /**
     * Liefert die Anzahl der Schiffe von einem bestimmten Typ.
     * @return die Anzahl der Schiffe des übergebenen Typs
     */
    public int getShipCount(ShipType shipType) {
        int numberOfOccurences = 0;
        for (Ship ship : ships) {
            if (ship.getType() == shipType) {
                if (!ship.isDestroyed() && ship.isPlaced()) {
                    numberOfOccurences++;
                }
            }
        }
        return numberOfOccurences;
    }

    /**
     * Setzt das currentShip auf das erste verfügbare Schiff, dessen Schifftyp
     * dem übergebenem Schifftyp gleicht.
     * @return das erste gefundene Schiff, das dem übergebenem Schifftyp gleicht
     */
    public boolean setCurrentShipByType(ShipType shipType) {
        ArrayList<Ship> availableShips = getAvailableShips(true);
        for (Ship ship : availableShips) {
            if (ship.getType() == shipType) {
                currentShip = ship;
                return true;
            }
        }
        return false;
    }

    /**
     * Gibt an, ob Schiffe vorhanden sind, die dem übergebenen ShipType
     * gleichen.
     * @return true, wenn Schiffe vom Schifftyp vorhanden sind, false wenn nicht
     */
    public boolean isShipOfTypeAvailable(ShipType shipType) {
        ArrayList<Ship> availableshShips = getAvailableShips(true);
        for (Ship ship : availableshShips) {
            if (ship.getType() == shipType) {
                return true;
            }
        }
        return false;
    }

    public ShipType getTypeOFirstAvailableShip() {
        ArrayList<Ship> availableshShips = getAvailableShips(true);
        for (Ship ship : availableshShips) {
            return ship.getType();
        }
        return null;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        for (Ship ship : ships) {
            ship.setPlaced(true);
        }
        this.board = board;
    }

    public boolean areAllShipsOfTypeReloading(ShipType type) {
        ArrayList<Ship> availableshShips = getAvailableShips(true);
        for (Ship ship : availableshShips) {
            if (ship.getType() == type && !ship.isReloading() && !ship.isDestroyed()) {
                return false;
            }
        }
        return true;
    }

    public void resetShips() {
        for (Ship ship : ships) {
            ship.reset();
        }
    }
}

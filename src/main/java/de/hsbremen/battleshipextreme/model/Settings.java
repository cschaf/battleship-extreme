package de.hsbremen.battleshipextreme.model;

import java.io.Serializable;

import de.hsbremen.battleshipextreme.model.exception.BoardTooSmallException;
import de.hsbremen.battleshipextreme.model.exception.InvalidNumberOfShipsException;
import de.hsbremen.battleshipextreme.model.exception.InvalidPlayerNumberException;

/**
 * Die Klasse beinhaltet die Einstellungen für ein Spiel. Zusätzlich bietet sie
 * Methoden zur Validierung der Spieleinstellungen.
 */

public class Settings implements Serializable {
	private static final long serialVersionUID = 7869883437538019851L;
	private int players;
	private int smartAiPlayers;
	private int dumbAiPlayers;
	private int boardSize;
	private int destroyers;
	private int frigates;
	private int corvettes;
	private int submarines;

	public static final int DESTROYER_SIZE = 5;
	public static final int FRIGATE_SIZE = 4;
	public static final int CORVETTE_SIZE = 3;
	public static final int SUBMARINE_SIZE = 2;

	private static final float MIN_PERCENTAGE_OF_FIELDS_THAT_SHOULD_BE_EMPTY = 0.0f;

	public static final int MIN_BOARD_SIZE = 5;
	public static final int MAX_BOARD_SIZE = 40;

	public static final int MIN_PLAYERS = 2;
	public static final int MAX_PLAYERS = 6;

	public static final String SAVEGAME_FILENAME = "savegame.sav";

	/**
	 * Konstruktor mit Default-Einstellungen
	 */
	public Settings() {
		this.players = 2;
		this.smartAiPlayers = 0;
		this.dumbAiPlayers = 0;
		this.boardSize = 10;
		this.destroyers = 1;
		this.frigates = 1;
		this.corvettes = 1;
		this.submarines = 1;
	}

	/**
	 * Konstruktor zum Erzeugen beliebiger Einstellungen
	 * 
	 * @param players
	 *            Anzahl der Spieler
	 * @param smartAiPlayers
	 *            Anzahl der schlauen AI-Spieler
	 * @param dumbAiPlayers
	 *            Anzahl der dummen AI-Spieler
	 * @param boardSize
	 *            Boardgröße
	 * @param destroyers
	 *            Anzahl der Zerstörer
	 * @param frigates
	 *            Anzahl der Frigatten
	 * @param corvettes
	 *            Anzahl der Korvetten
	 * @param submarines
	 *            Anzahl der U-Boote
	 */
	public Settings(int players, int smartAiPlayers, int dumbAiPlayers, int boardSize, int destroyers, int frigates, int corvettes, int submarines) {
		this.players = players;
		this.smartAiPlayers = smartAiPlayers;
		this.dumbAiPlayers = dumbAiPlayers;
		this.boardSize = boardSize;
		this.destroyers = destroyers;
		this.frigates = frigates;
		this.corvettes = corvettes;
		this.submarines = submarines;
	}

	/**
	 * Die Methode dient zum Validieren der Spieleinstellungen.
	 * 
	 * @throws InvalidPlayerNumberException
	 *             bei ungültiger Spieleranzahl
	 * @throws InvalidNumberOfShipsException
	 *             bei ungültiger Schiffanzahl
	 * @throws BoardTooSmallException
	 *             bei ungültiger Boardgröße
	 */
	public void validate() throws InvalidPlayerNumberException, InvalidNumberOfShipsException, BoardTooSmallException {
		validateNumberOfPlayers(players, smartAiPlayers, dumbAiPlayers);
		validateNumberOfShips(destroyers, corvettes, frigates, submarines);
		validateFieldSize(boardSize, destroyers, corvettes, frigates, submarines);
	}

	public int getSmartAiPlayers() {
		return smartAiPlayers;
	}

	public int getDumbAiPlayers() {
		return dumbAiPlayers;
	}

	public int getPlayers() {
		return players;
	}

	public void setPlayers(int players) {
		this.players = players;
	}

	public int getBoardSize() {
		return boardSize;
	}

	public void setBoardSize(int boardSize) {
		this.boardSize = boardSize;
	}

	public int getDestroyers() {
		return destroyers;
	}

	public void setDestroyers(int destroyers) {
		this.destroyers = destroyers;
	}

	public int getFrigates() {
		return frigates;
	}

	public void setFrigates(int frigates) {
		this.frigates = frigates;
	}

	public int getCorvettes() {
		return corvettes;
	}

	public void setCorvettes(int corvettes) {
		this.corvettes = corvettes;
	}

	public int getSubmarines() {
		return submarines;
	}

	public void setSubmarines(int submarines) {
		this.submarines = submarines;
	}

	/**
	 * Liefert die Anzahl der benötigten Felder. Diese wird anhand der
	 * Schiffzahlen und einem prozentualen Anteil an Feldern die leer sein
	 * sollen berechnet.
	 * 
	 * @param destroyers
	 * @param corvettes
	 * @param frigates
	 * @param submarines
	 * @return Anzahl der benötigten Felder
	 */
	public static int getRequiredFields(int destroyers, int corvettes, int frigates, int submarines) {
		// benötigte Felder unter Einbeziehung der Schiffradien
		int requiredFields = destroyers * ((Settings.DESTROYER_SIZE + 1) * 2) + corvettes * ((Settings.DESTROYER_SIZE + 1) * 2) + frigates * ((Settings.FRIGATE_SIZE + 1) * 2) + submarines
				* ((Settings.SUBMARINE_SIZE + 1) * 2);
		// Felder die leer sein sollen addieren
		if (MIN_PERCENTAGE_OF_FIELDS_THAT_SHOULD_BE_EMPTY > 0)
			requiredFields += (int) Math.floor((requiredFields / MIN_PERCENTAGE_OF_FIELDS_THAT_SHOULD_BE_EMPTY));
		return requiredFields;
	}

	public static int getRequiredBoardSize(int requiredFields) {
		int size = (int) Math.ceil(Math.sqrt(requiredFields));
		if (size < MIN_BOARD_SIZE)
			return MIN_BOARD_SIZE;
		else
			return size;
	}

	/**
	 * Prüft ob die Feldgröße gültig ist.
	 * 
	 * @param boardSize
	 * @param destroyers
	 * @param corvettes
	 * @param frigates
	 * @param submarines
	 * @throws BoardTooSmallException
	 *             wenn das Board zu groß oder klein ist
	 */
	private void validateFieldSize(int boardSize, int destroyers, int corvettes, int frigates, int submarines) throws BoardTooSmallException {
		int requiredFields = getRequiredFields(destroyers, corvettes, frigates, submarines);
		int requiredBoardSize = getRequiredBoardSize(requiredFields);
		if ((boardSize < requiredBoardSize) || (boardSize < MIN_BOARD_SIZE) || (boardSize > MAX_BOARD_SIZE))
			throw new BoardTooSmallException();
	}

	/**
	 * Prüft ob die Anzahl der Spieler gültig ist.
	 * 
	 * @param players
	 * @param smartAiPlayers
	 * @param dumbAiPlayers
	 * @throws InvalidPlayerNumberException
	 *             bein ungültiger Spieleranzahl
	 */
	private void validateNumberOfPlayers(int players, int smartAiPlayers, int dumbAiPlayers) throws InvalidPlayerNumberException {
		int numberOfPlayers = players + smartAiPlayers + dumbAiPlayers;
		if ((numberOfPlayers < MIN_PLAYERS) || (numberOfPlayers > MAX_PLAYERS))
			throw new InvalidPlayerNumberException(MIN_PLAYERS, MAX_PLAYERS);
	}

	/**
	 * Prüft ob mindestens ein Schiff existiert.
	 * 
	 * @param destroyers
	 * @param corvettes
	 * @param frigates
	 * @param submarines
	 * @throws InvalidNumberOfShipsException
	 *             wenn es keine Schiffe gibt.
	 */
	private void validateNumberOfShips(int destroyers, int corvettes, int frigates, int submarines) throws InvalidNumberOfShipsException {
		if (destroyers + corvettes + frigates + submarines <= 0)
			throw new InvalidNumberOfShipsException();
	}

}

package de.hsbremen.battleshipextreme.model;

import de.hsbremen.battleshipextreme.model.exception.BoardTooSmallException;
import de.hsbremen.battleshipextreme.model.exception.InvalidNumberOfShipsException;
import de.hsbremen.battleshipextreme.model.exception.InvalidPlayerNumberException;

public class Settings {
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

	private static final float MIN_PERCENTAGE_OF_FIELDS_THAT_SHOULD_BE_EMPTY = 10.0f;

	public static final int MIN_PLAYERS = 2;
	public static final int MAX_PLAYERS = 6;

	public Settings(int players, int aiPlayers, int dumbAiPlayers, int boardSize, int destroyers, int frigates, int corvettes, int submarines) throws BoardTooSmallException,
			InvalidPlayerNumberException, InvalidNumberOfShipsException {
		validateNumberOfPlayers(players, aiPlayers, dumbAiPlayers);
		validateNumberOfShips(destroyers, corvettes, frigates, submarines);
		validateFieldSize(boardSize, destroyers, corvettes, frigates, submarines);
		this.players = players;
		this.smartAiPlayers = aiPlayers;
		this.dumbAiPlayers = dumbAiPlayers;
		this.boardSize = boardSize;
		this.boardSize = boardSize;
		this.destroyers = destroyers;
		this.frigates = frigates;
		this.corvettes = corvettes;
		this.submarines = submarines;
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

	public static int getRequiredFields(int destroyers, int corvettes, int frigates, int submarines) {
		int requiredFields = destroyers * (Settings.DESTROYER_SIZE * 2 + 1) + corvettes * (Settings.CORVETTE_SIZE * 2 + 1) + frigates * (Settings.FRIGATE_SIZE * 2 + 1) + submarines
				* (Settings.SUBMARINE_SIZE * 2 + 1);
		// Felder die leer sein sollen addieren
		requiredFields += (int) Math.floor((requiredFields / MIN_PERCENTAGE_OF_FIELDS_THAT_SHOULD_BE_EMPTY));
		return requiredFields;
	}

	public static int getRequiredBoardSize(int requiredFields) {
		return (int) Math.floor(Math.sqrt(requiredFields)) + 1;
	}

	private void validateFieldSize(int boardSize, int destroyers, int corvettes, int frigates, int submarines) throws BoardTooSmallException {
		int requiredFields = getRequiredFields(destroyers, corvettes, frigates, submarines);
		int requiredBoardSize = getRequiredBoardSize(requiredFields);
		if (boardSize < requiredBoardSize)
			throw new BoardTooSmallException();
	}

	private void validateNumberOfPlayers(int players, int smartAiPlayers, int dumbAiPlayers) throws InvalidPlayerNumberException {
		int numberOfPlayers = players + smartAiPlayers + dumbAiPlayers;
		if ((numberOfPlayers < MIN_PLAYERS) || (numberOfPlayers > MAX_PLAYERS))
			throw new InvalidPlayerNumberException(MIN_PLAYERS, MAX_PLAYERS);
	}

	private void validateNumberOfShips(int destroyers, int corvettes, int frigates, int submarines) throws InvalidNumberOfShipsException {
		if (destroyers + corvettes + frigates + submarines <= 0)
			throw new InvalidNumberOfShipsException();
	}

}

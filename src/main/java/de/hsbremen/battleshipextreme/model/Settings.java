package de.hsbremen.battleshipextreme.model;
import java.io.Serializable;
import de.hsbremen.battleshipextreme.model.exception.BoardTooSmallException;
import de.hsbremen.battleshipextreme.model.exception.InvalidNumberOfShipsException;
import de.hsbremen.battleshipextreme.model.exception.InvalidPlayerNumberException;

public class Settings implements Serializable{
	private int players;
	private int smartAiPlayers;
	private int dumbAiPlayers;
	private int boardSize;
	private int destroyers;
	private int frigates;
	private int corvettes;
	private int submarines;

	private static final int DESTROYER_SIZE = 5;
	private static final int FRIGATE_SIZE = 4;
	private static final int CORVETTE_SIZE = 3;
	private static final int SUBMARINE_SIZE = 2;

	private static final float MIN_PERCENTAGE_OF_FIELDS_THAT_SHOULD_BE_EMPTY = 10.0f;

	private static final int MIN_PLAYERS = 2;
	private static final int MAX_PLAYERS = 6;

	public Settings(int players, int aiPlayers, int dumbAiPlayers, int boardSize, int destroyers, int frigates, int corvettes, int submarines) throws BoardTooSmallException,
			InvalidPlayerNumberException, InvalidNumberOfShipsException {
		this.players = players;
		this.smartAiPlayers = aiPlayers;
		this.dumbAiPlayers = dumbAiPlayers;
		this.boardSize = boardSize;
		this.boardSize = boardSize;
		this.destroyers = destroyers;
		this.frigates = frigates;
		this.corvettes = corvettes;
		this.submarines = submarines;
		validateNumberOfPlayers();
		validateNumberOfShips();
		validateFieldSize();
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

	private void validateFieldSize() throws BoardTooSmallException {
		int availableFields = this.boardSize * this.boardSize;
		int requiredFields = this.destroyers * DESTROYER_SIZE + this.corvettes * CORVETTE_SIZE + this.frigates * FRIGATE_SIZE + this.submarines * SUBMARINE_SIZE;
		float emptyFieldPercentage = 100f - (((float) requiredFields / (float) availableFields) * 100f);
		if (emptyFieldPercentage < MIN_PERCENTAGE_OF_FIELDS_THAT_SHOULD_BE_EMPTY)
			throw new BoardTooSmallException(MIN_PERCENTAGE_OF_FIELDS_THAT_SHOULD_BE_EMPTY, emptyFieldPercentage, availableFields, requiredFields);
	}

	private void validateNumberOfPlayers() throws InvalidPlayerNumberException {
		int numberOfPlayers = this.players + this.smartAiPlayers + this.dumbAiPlayers;
		if ((numberOfPlayers < MIN_PLAYERS) || (numberOfPlayers > MAX_PLAYERS))
			throw new InvalidPlayerNumberException(MIN_PLAYERS, MAX_PLAYERS);
	}

	private void validateNumberOfShips() throws InvalidNumberOfShipsException {
		if (this.destroyers + this.corvettes + this.frigates + this.submarines <= 0)
			throw new InvalidNumberOfShipsException();
	}

}

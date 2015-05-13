package de.hsbremen.battleshipextreme.model.exception;

public class InvalidPlayerNumberException extends Exception {

	private int minPlayers;
	private int maxPlayers;

	public InvalidPlayerNumberException(int minPlayers, int maxPlayers) {
		super("The number of players must be greater than" + minPlayers + " and lower than " + maxPlayers + 1);
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
	}

	public int getMinPlayers() {
		return minPlayers;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

}

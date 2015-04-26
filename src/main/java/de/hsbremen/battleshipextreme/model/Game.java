package de.hsbremen.battleshipextreme.model;

import java.util.Random;

import de.hsbremen.battleshipextreme.model.exception.NotAllShipsPlacedException;
import de.hsbremen.battleshipextreme.model.player.Player;

public class Game {
	Player[] players;
	Player currentPlayer;

	public Game(Settings settings) {
		this.players = new Player[settings.getPlayers()];
		for (int i = 0; i < this.players.length; i++)
			this.players[i] = new Player(settings.getBoardSize(),
					settings.getDestroyers(), settings.getFrigates(),
					settings.getCorvettes(), settings.getSubmarines());
		this.currentPlayer = null;
	}

	public boolean isReady() throws Exception {
		if (this.currentPlayer == null)
			throw new Exception("Current Player is not set!");

		// prüft ob alle Schiffe gesetzt sind
		for (Player player : this.players)
			if (!player.hasPlacedAllShips())
				throw new NotAllShipsPlacedException(player);

		return true;
	}

	public boolean isGameover() {
		int numberOfPlayersLeft = 0;
		for (Player player : this.players) {
			if (!player.hasLost())
				numberOfPlayersLeft++;
		}
		return numberOfPlayersLeft <= 1;
	}

	public Player determineWinner() throws Exception {
		if (!isGameover()) {
			throw new Exception("The game is not over yet!");
		}
		Player winner = null;
		for (Player player : players) {
			if (!player.hasLost()) {
				winner = player;
			}
		}
		return winner;
	}

	/**
	 * Set beginning player by valid id or randomly.
	 * 
	 * @param playerId
	 */
	public void setBeginningPlayer(int playerId) {
		if (this.currentPlayer == null) {
			boolean isIdOk = false;
			for (Player player : this.players) {
				if (player.getId() == playerId) {
					this.currentPlayer = player;
					isIdOk = true;
					break;
				}
			}
			if (!isIdOk)
				this.setBeginningPlayerRandomly();
		}
	}

	public void setBeginningPlayerRandomly() {
		Random rand = new Random();
		if (this.currentPlayer == null)
			this.currentPlayer = this.players[rand.nextInt(this.players.length)];
	}

	public Player[] getPlayers() {
		return players;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}
}

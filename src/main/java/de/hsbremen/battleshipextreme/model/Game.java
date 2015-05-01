package de.hsbremen.battleshipextreme.model;

import java.util.ArrayList;
import java.util.Random;

import de.hsbremen.battleshipextreme.model.player.Player;

public class Game {
	private Player[] players;
	private Player currentPlayer;
	private Player winner;

	public Game(Settings settings) {
		this.players = new Player[settings.getPlayers()];
		for (int i = 0; i < this.players.length; i++)
			this.players[i] = new Player(settings.getBoardSize(), settings.getDestroyers(), settings.getFrigates(), settings.getCorvettes(), settings.getSubmarines());
		this.currentPlayer = null;
	}

	/**
	 * Returns true if the ships of all players have been placed. The method is
	 * used to determine if a game may start.
	 * 
	 * @return true if all ships by all players are placed, else false
	 */
	public boolean isReady() {
		// prüft ob alle Schiffe gesetzt sind
		for (Player player : this.players)
			if (!player.hasPlacedAllShips())
				return false;
		return true;
	}

	/**
	 * Check if the game is over. Set the game winner if the game is over.
	 * 
	 * @return true if the game is over, false if not
	 */
	public boolean isGameover() {
		int numberOfPlayersLeft = 0;
		Player potentialWinner = null;
		for (Player player : this.players) {
			if (!player.hasLost()) {
				numberOfPlayersLeft++;
				potentialWinner = player;
			}
		}
		if (numberOfPlayersLeft <= 1) {
			this.winner = potentialWinner;
		}
		return numberOfPlayersLeft <= 1;
	}

	public Player getWinner() {
		return this.winner;
	}

	/**
	 * Set the currentPlayer to the next available player. If a player is not
	 * able to make a turn, he will be skipped. Decrease the reload time of the
	 * current players' ships.
	 * 
	 */
	public void nextPlayer() {
		this.currentPlayer.decreaseCurrentReloadTimeOfShips();
		int currentPlayerIndex = 0;
		// currentPlayerIndex ermitteln
		for (int i = 0; i < this.players.length; i++) {
			if (this.currentPlayer.equals(this.players[i])) {
				currentPlayerIndex = i;
			}
		}
		// wenn letzter Spieler im Array, dann Index wieder auf 0 setzen,
		// ansonsten hochzählen
		currentPlayerIndex = (currentPlayerIndex >= this.players.length - 1) ? currentPlayerIndex = 0 : currentPlayerIndex + 1;
		this.currentPlayer = this.players[currentPlayerIndex];
		// Spieler überspringen, wenn alle Schiffe nachladen oder er tot ist
		if (this.currentPlayer.hasToSkip()) {
			this.nextPlayer();
		}
	}

	/**
	 * Provides a list of enemies the current player may attack. Players that
	 * are lost or equal to the current player are filtered.
	 * 
	 * @return an ArrayList of Players
	 */
	public ArrayList<Player> getEnemiesOfCurrentPlayer() {
		// angreifbare Gegner des currentPlayers zurückgeben
		ArrayList<Player> enemies = new ArrayList<Player>();
		for (int i = 0; i < this.players.length; i++) {
			if (!this.players[i].hasLost()) {
				if (!this.currentPlayer.equals(players[i])) {
					enemies.add(this.players[i]);
				}
			}
		}
		return enemies;
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

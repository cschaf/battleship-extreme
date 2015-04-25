package de.hsbremen.battleshipextreme.model;

import java.util.Random;

import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.model.ship.Ship;

public class Game {
	Player[] players;
	Player currentPlayer;
	
	public Game(Settings settings) {
		this.players = new Player[settings.getPlayers()];
		for (int i = 0; i < this.players.length; i++)
			this.players[i] = new Player(
					settings.getBoardSize(), 
					settings.getDestroyers(), 
					settings.getFrigates(), 
					settings.getCorvettes(), 
					settings.getSubmarines()
					);
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
	
	public void setCurrentPlayer(int playerId) {
		if (this.currentPlayer == null)
			this.currentPlayer = this.players[playerId];
	}
	
	public void setCurrentPlayerRandom() {
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

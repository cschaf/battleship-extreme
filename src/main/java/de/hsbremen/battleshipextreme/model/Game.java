package de.hsbremen.battleshipextreme.model;

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

	public boolean isReady() {
		boolean isReady = false;
		// prüft ob ein aktueller Spieler gesetzt ist
		if (this.currentPlayer != null) {
			// prüft ob alle Schiffe gesetzt sind
			for (Player player : this.players) {
				for (Ship ship : player.getShips()) {
					
				}
			}
		}
		return isReady;
	}
	
	public Player[] getPlayers() {
		return players;
	}
	
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
}

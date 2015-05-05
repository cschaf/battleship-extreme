package de.hsbremen.battleshipextreme.model.exception;

import de.hsbremen.battleshipextreme.model.player.Player;

public class NotAllShipsPlacedException extends Exception {
	private Player player;
	
	public NotAllShipsPlacedException(Player player) {
		super(player + " has not placed his ships!");
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
}

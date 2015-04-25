package de.hsbremen.battleshipextreme.model;

import de.hsbremen.battleshipextreme.model.player.Player;

public class NotAllShipsPlacedException extends Exception {
	public NotAllShipsPlacedException(Player player) {
		super(player + " has not placed his ships!");
	}
}

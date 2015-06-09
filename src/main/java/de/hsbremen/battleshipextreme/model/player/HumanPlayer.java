package de.hsbremen.battleshipextreme.model.player;

import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.ship.Ship;

import java.util.ArrayList;
import java.util.HashMap;

public class HumanPlayer extends Player {

	public HumanPlayer(int boardSize, int destroyers, int frigates, int corvettes, int submarines) {
		super(boardSize, destroyers, frigates, corvettes, submarines);
		this.name = "Player";
		this.type = PlayerType.HUMAN;
	}

	public HumanPlayer(int boardSize, HashMap<Ship, ArrayList<Field>> shipMap) {
		super(boardSize, shipMap);
		this.type = PlayerType.HUMAN;
		this.name = "Player";
	}
}

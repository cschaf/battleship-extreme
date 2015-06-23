package de.hsbremen.battleshipextreme.model.player;

import java.util.ArrayList;
import java.util.HashMap;

import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.ship.Ship;

public class HumanPlayer extends Player {
	private static final long serialVersionUID = -2357605691030300521L;

	public HumanPlayer(int boardSize, int destroyers, int frigates,
			int corvettes, int submarines) {
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

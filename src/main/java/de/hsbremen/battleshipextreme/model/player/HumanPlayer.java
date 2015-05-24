package de.hsbremen.battleshipextreme.model.player;

import de.hsbremen.battleshipextreme.model.Board;

public class HumanPlayer extends Player {

	public HumanPlayer(Board board, int destroyers, int frigates, int corvettes, int submarines) {
		super(board, destroyers, frigates, corvettes, submarines);
		this.name = "Player";
		this.type = PlayerType.HUMAN;
	}
}

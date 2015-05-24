package de.hsbremen.battleshipextreme.model.player;

public class HumanPlayer extends Player {

	public HumanPlayer(int boardSize, int destroyers, int frigates, int corvettes, int submarines) {
		super(boardSize, destroyers, frigates, corvettes, submarines);
		this.name = "Player";
		this.type = PlayerType.HUMAN;
	}
}

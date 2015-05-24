package de.hsbremen.battleshipextreme.model.player;

import java.util.ArrayList;

import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.Orientation;

/**
 * Dumb AI - shoots randomly
 * 
 * AI-Benchmark: ~ 77 rounds
 *
 */

public class DumbAIPlayer extends AIPlayer {
	Field[] nextTargetsArray;

	public DumbAIPlayer(int boardSize, int destroyers, int frigates, int corvettes, int submarines) {
		super(boardSize, destroyers, frigates, corvettes, submarines);
		this.name = "Dumme KI";
	}

	@Override
	public void makeAiTurn(ArrayList<Player> availablePlayers) throws Exception {
		Orientation orientation;
		boolean hasTurnBeenMade = false;

		chooseShipToShootWithRandomly();

		// Gegner zuf‰llig w‰hlen
		int randomEnemyIndex = createRandomNumber(0, availablePlayers.size() - 1);
		this.currentEnemy = availablePlayers.get(randomEnemyIndex);

		// zuf‰llig schieﬂen
		do {
			orientation = (createRandomNumber(0, 1) == 0) ? Orientation.Horizontal : Orientation.Vertical;
			Field field = createRandomField(0, board.getSize() - 1, 0, board.getSize() - 1);
			hasTurnBeenMade = makeTurn(this.currentEnemy, field.getXPos(), field.getYPos(), orientation);
		} while (!hasTurnBeenMade);
	}

}

package de.hsbremen.battleshipextreme.integration;

import static org.junit.Assert.assertEquals;

import org.junit.Before;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.model.ship.Ship;

/**
 * Provides methods that are used by multiple integration tests.
 *
 */
public abstract class GameTest {
	protected Player player;

	@Before
	public void setUp() throws Exception {
		player = new Player(10, 2, 1, 1, 1);
	}

	protected void placeAllShipsRandomly(Player player) throws Exception {
		// +++++-----
		// ----------
		// +++++-----
		// ----------
		// ++++------
		// ----------
		// +++-------
		// ----------
		// ++--------
		// ----------
		for (int i = 0; i < player.getShips().length; i++) {
			Ship ship = player.getShips()[i];
			int row = 2 * i;
			int column = 0;
			Orientation orientation = Orientation.Horizontal;
			player.placeShip(ship, column, row, orientation);
		}
	}

	protected void checkFieldsForState(Board board, int range, int startX, int startY, Orientation orientation, FieldState expectedState) throws FieldOutOfBoardException {
		int xDirection = orientation == Orientation.Horizontal ? 1 : 0;
		int yDirection = orientation == Orientation.Vertical ? 1 : 0;
		int x;
		int y;
		for (int i = 0; i < range; i++) {
			x = startX + xDirection * i;
			y = startY + yDirection * i;
			FieldState actual = board.getField(startX + xDirection * i, startY + yDirection * i).getState();
			assertEquals("field x=" + x + "y=" + y + "does not have the expected state", expectedState, actual);
		}
	}

}

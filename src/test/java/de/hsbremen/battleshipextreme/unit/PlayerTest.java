package de.hsbremen.battleshipextreme.unit;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.exception.FieldOccupiedException;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.exception.ShipAlreadyPlacedException;
import de.hsbremen.battleshipextreme.model.exception.ShipOutOfBoardException;
import de.hsbremen.battleshipextreme.model.player.HumanPlayer;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.model.ship.Destroyer;
import de.hsbremen.battleshipextreme.model.ship.Ship;

/**
 * Tests the Player class
 * 
 * TODO: Mock ship object
 * 
 */

public class PlayerTest {

	private Player player;

	@Mock
	private Destroyer destroyer;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// wird vor jedem Test ausgeführt
		player = new HumanPlayer(10, 2, 1, 1, 1);

	}

	/**
	 * Test method for
	 * {@link de.hsbremen.battleshipextreme.model.player.Player#placeShip()} .
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPlaceShipsHorizontally() throws Exception {
		// links oben
		Destroyer destroyer = (Destroyer) player.getShips()[0];
		testPlaceShipAtPosition(destroyer, 0, 0, Orientation.Horizontal);

		// rechts unten
		destroyer = (Destroyer) player.getShips()[1];
		testPlaceShipAtPosition(destroyer, 5, 9, Orientation.Horizontal);
	}

	@Test
	public void testPlaceShipsVertically() throws Exception {
		Destroyer destroyer = (Destroyer) player.getShips()[0];
		testPlaceShipAtPosition(destroyer, 0, 0, Orientation.Vertical);

		destroyer = (Destroyer) player.getShips()[1];
		testPlaceShipAtPosition(destroyer, 9, 5, Orientation.Vertical);
	}

	@Test(expected = ShipAlreadyPlacedException.class)
	public void testShipAlreadyPlacedException() throws Exception {
		Destroyer destroyer = (Destroyer) player.getShips()[0];
		this.player.placeShip(0, 9, Orientation.Horizontal);
		this.player.placeShip(5, 3, Orientation.Horizontal);
	}

	@Test(expected = FieldOccupiedException.class)
	public void testPlaceShipsOnTheSameFieldHorizontally() throws Exception {
		Ship ship = this.player.getShips()[0];
		this.player.placeShip(0, 9, Orientation.Horizontal);
		ship = player.getShips()[1];
		this.player.placeShip(0, 9, Orientation.Horizontal);
	}

	@Test(expected = FieldOccupiedException.class)
	public void testPlaceShipsOnTheSameFieldVertically() throws Exception {
		Ship ship = this.player.getShips()[0];
		this.player.placeShip(0, 0, Orientation.Vertical);
		ship = player.getShips()[1];
		this.player.placeShip(0, 0, Orientation.Vertical);
	}

	@Test(expected = FieldOccupiedException.class)
	public void testPlaceShipsNextToEachOtherHorizontally() throws ShipAlreadyPlacedException, FieldOutOfBoardException, Exception {
		// Schiffe dürfen nicht ohne Freiraum nebeneinander stehen
		// deshalb muss eine Exception geworfen werden
		Ship ship = player.getShips()[0];
		this.player.placeShip(0, 8, Orientation.Horizontal);
		ship = player.getShips()[1];
		this.player.placeShip(0, 9, Orientation.Horizontal);
	}

	@Test(expected = FieldOccupiedException.class)
	public void testPlaceShipsNextToEachOtherVertically() throws ShipAlreadyPlacedException, FieldOutOfBoardException, Exception {
		// Schiffe dürfen nicht ohne Freiraum nebeneinander stehen
		// deshalb muss eine Exception geworfen werden
		Ship ship = player.getShips()[0];
		this.player.placeShip(0, 0, Orientation.Vertical);
		ship = player.getShips()[1];
		this.player.placeShip(1, 0, Orientation.Vertical);
	}

	@Test(expected = FieldOutOfBoardException.class)
	public void testFieldOutOfBoardException() throws Exception {
		player.placeShip(1000, 10000, Orientation.Horizontal);
	}

	@Test(expected = ShipOutOfBoardException.class)
	public void testPlaceShipOutOfBoardHorizontally() throws Exception {
		player.placeShip(8, 8, Orientation.Horizontal);
	}

	@Test(expected = ShipOutOfBoardException.class)
	public void testPlaceShipOutOfBoardVertically() throws Exception {
		player.placeShip(8, 8, Orientation.Vertical);
	}

	private void testPlaceShipAtPosition(Ship ship, int startX, int startY, Orientation orientation) throws ShipAlreadyPlacedException, FieldOutOfBoardException, Exception {
		this.player.placeShip(startX, startY, orientation);
		checkFieldsForState(this.player.getBoard(), ship.getSize(), startX, startY, orientation, FieldState.HasShip);
	}

	private void checkFieldsForState(Board board, int range, int startX, int startY, Orientation orientation, FieldState expectedState) throws FieldOutOfBoardException {
		// prüft ob mehrere Felder den erwarteten Feldstatus haben
		int xDirection = orientation == Orientation.Horizontal ? 1 : 0;
		int yDirection = orientation == Orientation.Vertical ? 1 : 0;
		for (int i = 0; i < range; i++) {
			FieldState actual = board.getField(startX + xDirection * i, startY + yDirection * i).getState();
			assertEquals(expectedState, actual);
		}
	}
}

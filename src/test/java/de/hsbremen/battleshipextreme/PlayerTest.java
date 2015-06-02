package test.java.de.hsbremen.battleshipextreme;

import de.hsbremen.battleshipextreme.model.player.HumanPlayer;
import de.hsbremen.battleshipextreme.model.player.Player;
import org.junit.Before;

/**
 * Tests the Player class
 * TODO: Mock ship object
 */

public class PlayerTest {

    private Player player;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        // wird vor jedem Test ausgef�hrt
        player = new HumanPlayer(10, 2, 1, 1, 1);
    }

    /**
     * Test method for
     *
     * @throws Exception
     */
/*	@Test
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
		this.player.placeShip(destroyer, 0, 9, Orientation.Horizontal);
		this.player.placeShip(destroyer, 5, 3, Orientation.Horizontal);
	}

	@Test(expected = FieldOccupiedException.class)
	public void testPlaceShipsOnTheSameFieldHorizontally() throws Exception {
		Ship ship = this.player.getShips()[0];
		this.player.placeShip(ship, 0, 9, Orientation.Horizontal);
		ship = player.getShips()[1];
		this.player.placeShip(ship, 0, 9, Orientation.Horizontal);
	}

	@Test(expected = FieldOccupiedException.class)
	public void testPlaceShipsOnTheSameFieldVertically() throws Exception {
		Ship ship = this.player.getShips()[0];
		this.player.placeShip(ship, 0, 0, Orientation.Vertical);
		ship = player.getShips()[1];
		this.player.placeShip(ship, 0, 0, Orientation.Vertical);
	}

	@Test(expected = FieldOccupiedException.class)
	public void testPlaceShipsNextToEachOtherHorizontally() throws ShipAlreadyPlacedException, FieldOutOfBoardException, Exception {
		// Schiffe d�rfen nicht ohne Freiraum nebeneinander stehen
		// deshalb muss eine Exception geworfen werden
		Ship ship = player.getShips()[0];
		this.player.placeShip(ship, 0, 8, Orientation.Horizontal);
		ship = player.getShips()[1];
		this.player.placeShip(ship, 0, 9, Orientation.Horizontal);
	}

	@Test(expected = FieldOccupiedException.class)
	public void testPlaceShipsNextToEachOtherVertically() throws ShipAlreadyPlacedException, FieldOutOfBoardException, Exception {
		// Schiffe d�rfen nicht ohne Freiraum nebeneinander stehen
		// deshalb muss eine Exception geworfen werden
		Ship ship = player.getShips()[0];
		this.player.placeShip(ship, 0, 0, Orientation.Vertical);
		ship = player.getShips()[1];
		this.player.placeShip(ship, 1, 0, Orientation.Vertical);
	}

	@Test(expected = FieldOutOfBoardException.class)
	public void testFieldOutOfBoardException() throws Exception {
		player.placeShip(player.getShips()[0], 1000, 10000, Orientation.Horizontal);
	}

	@Test(expected = ShipOutOfBoardException.class)
	public void testPlaceShipOutOfBoardHorizontally() throws Exception {
		player.placeShip(player.getShips()[0], 8, 8, Orientation.Horizontal);
	}

	@Test(expected = ShipOutOfBoardException.class)
	public void testPlaceShipOutOfBoardVertically() throws Exception {
		player.placeShip(player.getShips()[0], 8, 8, Orientation.Vertical);
	}

	private void testPlaceShipAtPosition(Ship ship, int startX, int startY, Orientation orientation) throws ShipAlreadyPlacedException, FieldOutOfBoardException, Exception {
		this.player.placeShip(ship, startX, startY, orientation);
		checkFieldsForState(this.player.getBoard(), ship.getSize(), startX, startY, orientation, FieldState.HasShip);
	}

	private void checkFieldsForState(Board board, int range, int startX, int startY, Orientation orientation, FieldState expectedState) throws FieldOutOfBoardException {
		// pr�ft ob mehrere Felder den erwarteten Feldstatus haben
		int xDirection = orientation == Orientation.Horizontal ? 1 : 0;
		int yDirection = orientation == Orientation.Vertical ? 1 : 0;
		for (int i = 0; i < range; i++) {
			FieldState actual = board.getField(startX + xDirection * i, startY + yDirection * i).getState();
			assertEquals(expectedState, actual);
		}
	}*/
}

package de.hsbremen.battleshipextreme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.exception.FieldOccupiedException;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.exception.ShipAlreadyPlacedException;
import de.hsbremen.battleshipextreme.model.exception.ShipOutOfBoardException;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.model.ship.Destroyer;
import de.hsbremen.battleshipextreme.model.ship.Ship;

/**
 * Tests the Player class
 * 
 */
public class PlayerTest {

	private Player player;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// wird vor jedem Test ausgeführt
		player = new Player(10, 2, 1, 1, 1);
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
		this.player.placeShip(destroyer, 0, 9, Orientation.Horizontal);
		this.player.placeShip(destroyer, 5, 3, Orientation.Horizontal);
	}

	@Test(expected = FieldOccupiedException.class)
	public void testPlaceShipsOnTheSameField() throws Exception {
		Ship ship = this.player.getShips()[0];
		this.player.placeShip(ship, 0, 9, Orientation.Horizontal);
		ship = player.getShips()[1];
		this.player.placeShip(ship, 0, 9, Orientation.Horizontal);
	}

	@Test(expected = FieldOccupiedException.class)
	public void testPlaceShipsNextToEachOther() throws ShipAlreadyPlacedException, FieldOutOfBoardException, Exception {
		// Schiffe dürfen nicht ohne Freiraum nebeneinander stehen
		// deshalb muss eine Exception geworfen werden
		Ship ship = player.getShips()[0];
		this.player.placeShip(ship, 0, 8, Orientation.Horizontal);
		ship = player.getShips()[1];
		this.player.placeShip(ship, 0, 9, Orientation.Horizontal);
	}

	@Test(expected = FieldOutOfBoardException.class)
	public void testFieldOutOfBoardException() throws Exception {
		player.placeShip(player.getShips()[0], 1000, 10000, Orientation.Horizontal);
	}

	@Test(expected = ShipOutOfBoardException.class)
	public void testShipOutOfBoardException() throws ShipAlreadyPlacedException, FieldOutOfBoardException, Exception {
		player.placeShip(player.getShips()[0], 8, 8, Orientation.Horizontal);
	}

	@Test
	public void testHasPlacedAllShips() throws ShipAlreadyPlacedException, FieldOutOfBoardException, Exception {
		placeAllShipsRandomly(this.player);
		boolean actual = player.hasPlacedAllShips();
		assertTrue(actual);
	}

	@Test
	public void testMakeTurn() throws Exception {
		Player enemy = new Player(10, 1, 1, 1, 1);
		int startX = 5;
		int startY = 5;
		Ship ship = player.getShips()[0];
		Orientation orientation = Orientation.Horizontal;
		placeAllShipsRandomly(enemy);
		this.player.selectShip(ship);
		this.player.makeTurn(enemy, startX, startY, orientation);
		checkFieldsForState(enemy.getBoard(), ship.getShootingRange(), startX, startY, orientation, FieldState.Missed);
	}

	@Test
	public void testAreAllShipsReloading() throws Exception {
		Player enemy = new Player(10, 1, 1, 1, 1);
		int startX = 0;
		int startY = 0;
		Orientation orientation = Orientation.Horizontal;
		placeAllShipsRandomly(enemy);
		assertFalse(this.player.areAllShipsReloading());
		for (int i = 0; i < this.player.getShips().length; i++) {
			this.player.selectShip(player.getShips()[i]);
			this.player.makeTurn(enemy, startX, startY + i, orientation);
		}
		assertTrue(this.player.areAllShipsReloading());
	}

	@Test
	public void testDecreaseCurrentReloadTime() throws Exception {
		Player enemy = new Player(10, 1, 1, 1, 1);
		int startX = 0;
		int startY = 0;
		Ship ship = player.getShips()[0];
		Orientation orientation = Orientation.Horizontal;
		placeAllShipsRandomly(enemy);
		assertEquals(0, ship.getCurrentReloadTime());
		this.player.selectShip(ship);
		this.player.makeTurn(enemy, startX, startY, orientation);
		assertEquals(ship.getMaxReloadTime(), ship.getCurrentReloadTime());
		player.decreaseCurrentReloadTimeOfShips();
		// Reload time des Schiffs wird nicht verringert, da es gerade
		// geschossen hat
		assertEquals(ship.getMaxReloadTime(), ship.getCurrentReloadTime());
		// neue Runde mit anderem Schiff simulieren
		this.player.selectShip(player.getShips()[1]);
		this.player.makeTurn(enemy, startX, startY, orientation);
		player.decreaseCurrentReloadTimeOfShips();
		// diesmal muss sich die Reload Time um 1 verringern
		assertEquals(ship.getMaxReloadTime() - 1, ship.getCurrentReloadTime());
	}

	@Test
	public void testPlayerHasLost() {
		for (Ship ship : player.getShips()) {
			ship.setSize(0);
		}
		assertTrue(player.hasLost());
	}

	private void placeAllShipsRandomly(Player player) throws Exception {
		for (int i = 0; i < player.getShips().length; i++) {
			Ship ship = player.getShips()[i];
			int row = 2 * i;
			int column = 0;
			Orientation orientation = Orientation.Horizontal;
			this.player.placeShip(ship, column, row, orientation);
		}
	}

	private void testPlaceShipAtPosition(Ship ship, int startX, int startY, Orientation orientation) throws ShipAlreadyPlacedException, FieldOutOfBoardException, Exception {
		this.player.placeShip(ship, startX, startY, orientation);
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

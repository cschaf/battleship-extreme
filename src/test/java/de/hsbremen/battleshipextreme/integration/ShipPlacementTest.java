package de.hsbremen.battleshipextreme.integration;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.exception.FieldOccupiedException;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.exception.ShipAlreadyPlacedException;
import de.hsbremen.battleshipextreme.model.exception.ShipOutOfBoardException;
import de.hsbremen.battleshipextreme.model.ship.Destroyer;
import de.hsbremen.battleshipextreme.model.ship.Ship;

/**
 * This Test checks if the placement of ships works as expected. Classes that
 * are combined and tested as a group: Board, Field, Player, Ship
 * 
 */
public class ShipPlacementTest extends GameTest {

	@Test
	public void testPlaceShipsHorizontally() throws Exception {
		// links oben
		Orientation orientation = Orientation.Horizontal;
		int x = 0;
		int y = 0;
		Destroyer destroyer = (Destroyer) player.getShips()[0];
		this.player.placeShip(destroyer, x, y, orientation);
		checkFieldsForState(this.player.getBoard(), destroyer.getSize(), x, y, orientation, FieldState.HasShip);

		x = 5;
		y = 9;
		// rechts unten
		destroyer = (Destroyer) player.getShips()[1];
		this.player.placeShip(destroyer, x, y, orientation);
		checkFieldsForState(this.player.getBoard(), destroyer.getSize(), x, y, orientation, FieldState.HasShip);
	}

	@Test
	public void testPlaceShipsVertically() throws Exception {
		// links oben
		Orientation orientation = Orientation.Vertical;
		int x = 0;
		int y = 0;
		Destroyer destroyer = (Destroyer) player.getShips()[0];
		this.player.placeShip(destroyer, x, y, orientation);
		checkFieldsForState(this.player.getBoard(), destroyer.getSize(), x, y, orientation, FieldState.HasShip);

		orientation = Orientation.Vertical;
		x = 9;
		y = 5;
		destroyer = (Destroyer) player.getShips()[1];
		this.player.placeShip(destroyer, x, y, orientation);
		checkFieldsForState(this.player.getBoard(), destroyer.getSize(), x, y, orientation, FieldState.HasShip);
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
	public void testShipOutOfBoardExceptionHorizontally() throws ShipAlreadyPlacedException, FieldOutOfBoardException, Exception {
		player.placeShip(player.getShips()[0], 8, 8, Orientation.Horizontal);
	}

	@Test(expected = ShipOutOfBoardException.class)
	public void testShipOutOfBoardExceptionVertically() throws ShipAlreadyPlacedException, FieldOutOfBoardException, Exception {
		player.placeShip(player.getShips()[0], 8, 8, Orientation.Vertical);
	}

	@Test
	public void testHasPlacedAllShips() throws ShipAlreadyPlacedException, FieldOutOfBoardException, Exception {
		placeAllShipsRandomly(this.player);
		boolean actual = player.hasPlacedAllShips();
		assertTrue(actual);
	}
}
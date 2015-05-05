package de.hsbremen.battleshipextreme.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.model.ship.Ship;

/**
 * This class tests if the shooting actions work as expected.
 *
 */

public class ShootingTest extends GameTest {

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
		// es wurde nicht geschossen, die ReloadTime ist 0
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

	@Test
	public void testMakeTurnAndMiss() throws Exception {
		// Schießt auf Gegner und verfehlt Schiffe
		Player enemy = new Player(10, 2, 1, 1, 1);
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
	public void testMakeTurnAndHit() throws Exception {
		// Schießt auf Gegner und trifft ein Schiff
		Player enemy = new Player(10, 2, 1, 1, 1);
		int startX = 0;
		int startY = 0;
		Ship ship = player.getShips()[0];
		Orientation orientation = Orientation.Horizontal;
		placeAllShipsRandomly(enemy);
		this.player.selectShip(ship);
		this.player.makeTurn(enemy, startX, startY, orientation);
		checkFieldsForState(enemy.getBoard(), ship.getShootingRange(), startX, startY, orientation, FieldState.Hit);
	}

	@Test
	public void testMakeTurnAndDestroy() throws Exception {
		// schießt auf U-Boot und zerstört es
		Player enemy = new Player(10, 2, 1, 1, 1);
		int startX = 0;
		int startY = 8;
		Ship ship = player.getShips()[0];
		Orientation orientation = Orientation.Horizontal;
		placeAllShipsRandomly(enemy);
		this.player.selectShip(ship);
		this.player.makeTurn(enemy, startX, startY, orientation);
		checkFieldsForState(enemy.getBoard(), 2, startX, startY, orientation, FieldState.Destroyed);
	}
}

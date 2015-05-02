package de.hsbremen.battleshipextreme;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.model.ship.Destroyer;

@RunWith(MockitoJUnitRunner.class)
public class ShipTest {
	private Destroyer destroyer;

	// Die Player-Klasse hat ihren eigenen Unit-Test und soll hier nicht
	// getestet werden.
	// Deshalb wird mithilfe von Mockito ein Player-Dummy-Objekt erzeugt.
	// Wenn dann Fehler in der Player-Klasse auftreten führt das nicht
	// dazu, dass Tests in der ShipTest-Klasse fehlschlagen. So ist dann der
	// Fehler einfacher zu lokalisieren.
	@Mock
	private Player player;

	private Board board;

	@Before
	public void setUp() {
		this.board = new Board(10);
		// Da kein echtes Player-Objekt verwendet wird, muss festgelegt werden,
		// was passiert, wenn die player.getBoard()-Methode aufgerufen wird.
		when(player.getBoard()).thenReturn(this.board);
		this.destroyer = new Destroyer();
	}

	@Test
	public void testShootHorizontallyWithinBoard() throws FieldOutOfBoardException {
		int x = 0;
		int y = 1;
		Orientation orientation = Orientation.Horizontal;
		this.destroyer.shoot(player, x, y, orientation);
		checkFieldsForState(this.board, this.destroyer.getShootingRange(), x, y, orientation, FieldState.Missed);

	}

	@Test
	public void testShootVerticallyWithinBoard() throws FieldOutOfBoardException {
		int x = 0;
		int y = 1;
		Orientation orientation = Orientation.Vertical;
		this.destroyer.shoot(player, x, y, orientation);
		checkFieldsForState(this.board, this.destroyer.getShootingRange(), x, y, orientation, FieldState.Missed);

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

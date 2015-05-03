package de.hsbremen.battleshipextreme;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.ship.Destroyer;

@RunWith(MockitoJUnitRunner.class)
public class ShipTest {
	private Destroyer destroyer;

	// Unit-Tests sollen eine Klasse isoliert testen, damit auftretende Fehler
	// besser lokalisiert werden können. Tritt zum Beispiel ein Fehler in der
	// BoardTest-Klasse auf, dürfen Tests in der ShipTest-Klasse nicht
	// zwangsläufig auch fehlschlagen. Deshalb werden für Objekte, die nicht
	// Gegenstand des Tests sind, mithilfe des Mockito-Frameworks Dummy-Objekte
	// (Mocks) erstellt.
	@Mock
	private Board board;
	@Mock
	private Field field;

	@Before
	public void setUp() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, FieldOutOfBoardException {

		int size = 10;

		Field[][] fields = new Field[size][size];
		for (int y = 0; y < fields.length; y++) {
			for (int x = 0; x < fields.length; x++) {
				fields[y][x] = this.field;
				when(this.board.getField(x, y)).thenReturn(fields[y][x]);
			}
		}

		// Da es sich lediglich um Dummy-Objekte handelt, muss festgelegt
		// werden, was die gemockten Objekte zurückgeben, wenn Methoden
		// aufgerufen werden.
		when(this.field.isHit()).thenReturn(false);
		when(this.board.getFields()).thenReturn(fields);
		when(this.board.getSize()).thenReturn(size);

		this.destroyer = new Destroyer();
	}

	@Test
	public void testShootHorizontallyWithinBoard() throws FieldOutOfBoardException {
		Field field = mock(Field.class);
		when(field.getXPos()).thenReturn(0);
		when(field.getYPos()).thenReturn(1);
		Orientation orientation = Orientation.Horizontal;
		assertTrue(this.destroyer.shoot(this.board, field, orientation));
	}

	@Test
	public void testShootVerticallyWithinBoard() throws FieldOutOfBoardException {
		Field field = mock(Field.class);
		when(field.getXPos()).thenReturn(0);
		when(field.getYPos()).thenReturn(1);
		Orientation orientation = Orientation.Vertical;
		assertTrue(this.destroyer.shoot(this.board, field, orientation));
	}

	@Test
	public void testShootAtFieldThatWasAlreadyHit() throws FieldOutOfBoardException {
		Field field = mock(Field.class);
		when(field.getXPos()).thenReturn(9);
		when(field.getYPos()).thenReturn(0);
		when(field.isHit()).thenReturn(true);
		Orientation orientation = Orientation.Horizontal;
		boolean wasShotSuccessFul = this.destroyer.shoot(this.board, field, orientation);
		assertFalse(wasShotSuccessFul);
	}

	@Test
	public void testShootPartiallyOutsideBoard() throws FieldOutOfBoardException {
		Field field = mock(Field.class);
		when(field.getXPos()).thenReturn(9);
		when(field.getYPos()).thenReturn(0);
		Orientation orientation = Orientation.Horizontal;
		boolean wasShotSuccessFul = this.destroyer.shoot(this.board, field, orientation);
		assertTrue(wasShotSuccessFul);
	}
}

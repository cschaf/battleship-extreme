package de.hsbremen.battleshipextreme.client;

import de.hsbremen.battleshipextreme.model.Game;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.exception.ShipAlreadyPlacedException;
import de.hsbremen.battleshipextreme.model.exception.ShipOutOfBoardException;
import de.hsbremen.battleshipextreme.model.player.Player;

public class Controller {

	private Game game;
	private GUI gui;

	public Controller(Game game) {
		this.game = game;
		gui = new GUI(this, game);
		gui.createView();
		gui.createControls();
	}

	public void initializeGame(Settings settings) {
		game.initialize(settings);
		gui.createPlayerBoards(settings.getBoardSize());
		gui.showPanel(GUI.GAME_PANEL);
	}

	public void ownBoardClicked(int xPos, int yPos, boolean isHorizontal) throws ShipAlreadyPlacedException, FieldOutOfBoardException, ShipOutOfBoardException {
		Orientation orientation = isHorizontal ? Orientation.HORIZONTAL : Orientation.VERTICAL;
		System.out.println("gameready" + game.isReady());
		if (!game.isReady()) {
			Player currentPlayer = game.getCurrentPlayer();
			currentPlayer.placeShip(xPos, yPos, orientation);
			System.out.println("" + currentPlayer.getCurrentShip() + currentPlayer.getCurrentShip().isPlaced());
			currentPlayer.nextShip();
			System.out.println("" + currentPlayer.getCurrentShip() + currentPlayer.getCurrentShip().isPlaced());
			if (currentPlayer.hasPlacedAllShips()) {
				game.nextPlayer();
			}
		} else {
			System.out.println("allShipsPlaced");
		}
		gui.updateBoard(true);
	}

}

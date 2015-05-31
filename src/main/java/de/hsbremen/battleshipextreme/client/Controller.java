package de.hsbremen.battleshipextreme.client;

import de.hsbremen.battleshipextreme.model.Game;
import de.hsbremen.battleshipextreme.model.GameState;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.exception.ShipAlreadyPlacedException;
import de.hsbremen.battleshipextreme.model.exception.ShipOutOfBoardException;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.model.ship.ShipType;

public class Controller {

	private Game game;
	private GUI gui;

	public Controller(Game game) {
		this.game = game;
		gui = new GUI(this, game);
		gui.createView();
		gui.createMenuControls();
	}

	public void initializeGame(Settings settings) {
		game.initialize(settings);
		gui.createGameControl(settings.getBoardSize());
		gui.showPanel(GUI.GAME_PANEL);
	}

	public void placeShip(int xPos, int yPos, boolean isHorizontal) throws ShipAlreadyPlacedException, FieldOutOfBoardException, ShipOutOfBoardException {
		Orientation orientation = isHorizontal ? Orientation.HORIZONTAL : Orientation.VERTICAL;
		Player currentPlayer = game.getCurrentPlayer();

		switch (game.getState()) {
		case PLACEMENT_PHASE:
			boolean possible = currentPlayer.placeShip(xPos, yPos, orientation);
			if (possible)
				currentPlayer.nextShip();

			if (currentPlayer.hasPlacedAllShips())
				game.nextPlayer();

			if (game.isReady()) {
				game.setState(GameState.SHOOTING_PHASE);
				gui.updateEnemySelection();
			}

			gui.updatePlayerBoard();
			gui.updateGamePanel();
			break;
		case SHOOTING_PHASE:
			break;
		}

	}

	public boolean selectShip(ShipType shipType) {
		return game.getCurrentPlayer().setCurrentShipByType(shipType);
	}

	public boolean makeTurn(String enemyName, int xPos, int yPos, boolean isHorizontal) throws FieldOutOfBoardException {
		Orientation orientation = isHorizontal ? Orientation.HORIZONTAL : Orientation.VERTICAL;

		boolean possible = false;
		switch (game.getState()) {
		case PLACEMENT_PHASE:
			break;
		case SHOOTING_PHASE:
			Player currentPlayer = game.getCurrentPlayer();
			if (!currentPlayer.getCurrentShip().isDestroyed() && !currentPlayer.getCurrentShip().isReloading()) {
				Player enemy = game.getPlayerByName(enemyName);
				possible = game.makeTurn(enemy, xPos, yPos, orientation);
				gui.updateEnemyBoard();
				if (possible) {
					game.nextPlayer();
					System.out.println(game.getCurrentPlayer() + " greift " + enemy + " an mit " + game.getCurrentPlayer().getCurrentShip());
				}
			} else {
				System.out.println("nicht möglich");
			}

			gui.updateEnemyBoard();
			gui.updatePlayerBoard();
			gui.updateGamePanel();
			gui.updateEnemySelection();
			break;

		}
		return possible;
	}

}

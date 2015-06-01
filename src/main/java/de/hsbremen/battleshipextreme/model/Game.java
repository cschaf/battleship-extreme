package de.hsbremen.battleshipextreme.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.player.AIPlayer;
import de.hsbremen.battleshipextreme.model.player.HumanPlayer;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.model.player.PlayerType;
import de.hsbremen.battleshipextreme.model.player.Target;
import de.hsbremen.battleshipextreme.model.ship.Ship;

public class Game implements Serializable {
	private Player[] players;
	private Player currentPlayer;
	private Player winner;
	private int turnNumber;
	private int roundNumber;
	private int boardSize;

	/**
	 * Reads the settings and initializes the necessary game objects.
	 * 
	 * @param settings
	 *            the game settings.
	 */
	public void initialize(Settings settings) {
		createPlayers(settings);

		// Spielernummern setzen
		for (int i = 0; i < players.length; i++) {
			players[i].setName(players[i].getName() + (i + 1));
		}

		boardSize = settings.getBoardSize();
		turnNumber = 0;
		currentPlayer = players[0];
	}

	private void createPlayers(Settings settings) {
		// Spieler erzeugen
		int numberOfHumanPlayers = settings.getPlayers();
		int numberOfAIPlayers = settings.getSmartAiPlayers();
		int numberOfDumbAiPlayers = settings.getDumbAiPlayers();
		int numberOfPlayers = numberOfAIPlayers + numberOfHumanPlayers
				+ numberOfDumbAiPlayers;
		players = new Player[numberOfPlayers];
		for (int i = 0; i < numberOfPlayers; i++) {
			if (i < numberOfHumanPlayers) {
				players[i] = new HumanPlayer(settings.getBoardSize(),
						settings.getDestroyers(), settings.getFrigates(),
						settings.getCorvettes(), settings.getSubmarines());
			} else {
				if (i < numberOfAIPlayers + numberOfHumanPlayers) {
					players[i] = new AIPlayer(settings.getBoardSize(),
							settings.getDestroyers(), settings.getFrigates(),
							settings.getCorvettes(), settings.getSubmarines(),
							PlayerType.SMART_AI);
				} else {
					players[i] = new AIPlayer(settings.getBoardSize(),
							settings.getDestroyers(), settings.getFrigates(),
							settings.getCorvettes(), settings.getSubmarines(),
							PlayerType.DUMB_AI);
				}
			}
		}
	}

	public void makeAiTurn() throws Exception {
		boolean wasShotPossible = false;
		// AI soll Zug automatisch machen
		AIPlayer ai = (AIPlayer) currentPlayer;

		do {
			Player currentEnemy = selectAiEnemy(ai);
			ai.selectShip(ai.getAvailableShips(true).get(0));
			Target shot = ai.getTarget(currentEnemy.getFieldStates(false));
			wasShotPossible = makeTurn(currentEnemy, shot.getX(), shot.getY(),
					shot.getOrientation());
		} while (!wasShotPossible);
	}

	private Player selectAiEnemy(AIPlayer ai) {
		Player currentEnemy;
		do {
			currentEnemy = players[ai.getCurrentEnemyIndex()];
			// zufälligen Gegner auswählen, wenn die KI keine Spur verfolgt,
			// ansonsten gemerkten Gegner beibehalten
			if (!ai.hasTargets() || currentEnemy.hasLost()
					|| ai.getType() == PlayerType.DUMB_AI
					|| ai.equals(currentEnemy)) {
				ai.setRandomEnemyIndex(players.length - 1);
				currentEnemy = players[ai.getCurrentEnemyIndex()];
			}
		} while (currentEnemy.hasLost() || ai.equals(currentEnemy));
		return currentEnemy;
	}

	public boolean makeTurn(Player enemy, int xPos, int yPos,
			Orientation orientation) throws FieldOutOfBoardException {
		int xDirection = orientation == Orientation.HORIZONTAL ? 1 : 0;
		int yDirection = orientation == Orientation.VERTICAL ? 1 : 0;
		int x;
		int y;
		for (int i = 0; i < currentPlayer.getCurrentShip().getShootingRange(); i++) {
			x = xPos + i * xDirection;
			y = yPos + i * yDirection;
			boolean isShotPossible = enemy.markBoard(x, y);
			if (i == 0) {
				if (!isShotPossible) {
					// erstes Feld belegt, Schuss nicht möglich
					return false;
				}
			}
		}
		currentPlayer.getCurrentShip().shoot();
		return true;
	}

	/**
	 * Saves the this Game to a File
	 * 
	 * @param destinationPath
	 * @throws Exception
	 *             if the file could not be saved
	 */
	public void save(String destinationPath) throws Exception {
		FileOutputStream saveFile = null;
		ObjectOutputStream save = null;
		try {
			saveFile = new FileOutputStream(destinationPath);
			save = new ObjectOutputStream(saveFile);
			save.writeObject(this);
			save.close();
		} catch (Exception ex) {
			throw ex;
		} finally {
			closeQuietly(saveFile);
			closeQuietly(save);
		}
	}

	/**
	 * Load a saved Game object
	 * 
	 * @param destinationPath
	 * @throws Exception
	 *             if the game could not be loaded
	 */
	public void load(String destinationPath) throws Exception {
		FileInputStream saveFile = null;
		ObjectInputStream save = null;
		try {
			saveFile = new FileInputStream(destinationPath);
			save = new ObjectInputStream(saveFile);
			Game game = (Game) save.readObject();
			players = game.players;
			currentPlayer = game.currentPlayer;
			winner = game.winner;
			turnNumber = game.turnNumber;
			boardSize = game.boardSize;
			save.close();
		} catch (Exception ex1) {
			throw ex1;
		} finally {
			closeQuietly(saveFile);
			closeQuietly(save);
		}
	}

	/**
	 * Close a Stream quietly
	 * 
	 * @param stream
	 */
	private void closeQuietly(InputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Close a Stream quietly
	 * 
	 * @param stream
	 */
	private void closeQuietly(OutputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * Increase turnNumber.
	 * 
	 * Decrease the reload time of the current players' ships.
	 * 
	 * Set the currentPlayer to the next player.
	 * 
	 */
	public void nextPlayer() {
		turnNumber++;
		decreaseCurrentReloadTimeOfShips(currentPlayer);
		int currentPlayerIndex = Arrays.asList(players).indexOf(currentPlayer);
		// wenn letzter Spieler im Array, dann Index wieder auf 0 setzen,
		// ansonsten hochzählen
		currentPlayerIndex = (currentPlayerIndex >= players.length - 1) ? currentPlayerIndex = 0
				: currentPlayerIndex + 1;
		if (currentPlayerIndex == 0)
			roundNumber++;
		currentPlayer = players[currentPlayerIndex];
	}

	/**
	 * Decreases the reload time of the ships, except for the ship that just
	 * shot.
	 */
	private void decreaseCurrentReloadTimeOfShips(Player player) {
		Ship[] ships = player.getShips();
		for (Ship ship : ships) {
			ship.decreaseCurrentReloadTime();
		}
	}

	/**
	 * Provides a list of enemies the current player may attack. Players that
	 * are lost or equal to the current player are filtered.
	 * 
	 * @return an ArrayList of Players
	 */
	public ArrayList<Player> getEnemiesOfCurrentPlayer() {
		// angreifbare Gegner des currentPlayers zurückgeben
		ArrayList<Player> enemies = new ArrayList<Player>();
		for (int i = 0; i < players.length; i++) {
			if (!players[i].hasLost()) {
				if (!currentPlayer.equals(players[i])) {
					enemies.add(players[i]);
				}
			}
		}
		return enemies;
	}

	public Player getPlayerByName(String name) {
		for (Player player : players) {
			if (player.getName().equals(name)) {
				return player;
			}
		}
		return null;
	}

	/**
	 * Returns true if the ships of all players have been placed. The method is
	 * used to determine if a game is ready to start.
	 * 
	 * @return true if all ships by all players are placed, else false
	 */
	public boolean isReady() {
		// prüft ob alle Schiffe gesetzt sind
		for (Player player : players)
			if (!player.hasPlacedAllShips()) {
				return false;
			}
		return true;
	}

	/**
	 * Check if the game is over. Set the game winner if the game is over.
	 * 
	 * @return true if the game is over, false if not
	 */
	public boolean isGameover() {
		int numberOfPlayersLeft = 0;
		Player potentialWinner = null;
		for (Player player : players) {
			if (!player.hasLost()) {
				numberOfPlayersLeft++;
				potentialWinner = player;
			}
		}
		if (numberOfPlayersLeft <= 1) {
			winner = potentialWinner;
		}
		return numberOfPlayersLeft <= 1;
	}

	public Player[] getPlayers() {
		return players;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public Player getWinner() {
		return winner;
	}

	public int getTurnNumber() {
		return turnNumber;
	}

	public int getRoundNumber() {
		return roundNumber;
	}

	public int getBoardSize() {
		return boardSize;
	}
}

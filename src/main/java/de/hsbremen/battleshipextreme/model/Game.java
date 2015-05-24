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
import de.hsbremen.battleshipextreme.model.player.Shot;

public class Game implements Serializable {
	private Player[] players;
	private Board[] boards;
	private Player currentPlayer;
	private Player winner;
	private int turnNumber;

	/**
	 * Reads the settings and initializes the necessary game objects.
	 * 
	 * @param settings
	 *            the game settings.
	 */
	public Game(Settings settings) {
		// Spieler erzeugen
		int numberOfHumanPlayers = settings.getPlayers();
		int numberOfAIPlayers = settings.getSmartAiPlayers();
		int numberOfDumbAiPlayers = settings.getDumbAiPlayers();
		int numberOfPlayers = numberOfAIPlayers + numberOfHumanPlayers + numberOfDumbAiPlayers;
		System.out.println(numberOfPlayers);

		players = new Player[numberOfPlayers];
		boards = new Board[numberOfPlayers];

		// Boards erzeugen
		Board board;
		for (int i = 0; i < numberOfPlayers; i++) {
			board = new Board(settings.getBoardSize());
			boards[i] = board;
			if (i < numberOfHumanPlayers) {
				this.players[i] = new HumanPlayer(board, settings.getDestroyers(), settings.getFrigates(), settings.getCorvettes(), settings.getSubmarines());
			} else {
				if (i < numberOfAIPlayers + numberOfHumanPlayers) {
					this.players[i] = new AIPlayer(board, settings.getDestroyers(), settings.getFrigates(), settings.getCorvettes(), settings.getSubmarines(), PlayerType.SMART_AI);
				} else {

					this.players[i] = new AIPlayer(board, settings.getDestroyers(), settings.getFrigates(), settings.getCorvettes(), settings.getSubmarines(), PlayerType.DUMB_AI);
				}
			}
		}

		// Spielernummern setzen
		for (int i = 0; i < this.players.length; i++) {
			this.players[i].setName(this.players[i].getName() + (i + 1));
		}

		this.turnNumber = 0;
		this.currentPlayer = players[0];
	}

	/**
	 * This constructor is used when a game is loaded.
	 */
	public Game(String pathToSaveGame) throws Exception {
		this.load(pathToSaveGame);
	}

	/**
	 * Returns true if the ships of all players have been placed. The method is
	 * used to determine if a game is ready to start.
	 * 
	 * @return true if all ships by all players are placed, else false
	 */
	public boolean isReady() {
		// prüft ob alle Schiffe gesetzt sind
		for (Player player : this.players)
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
		for (Player player : this.players) {
			if (!player.hasLost()) {
				numberOfPlayersLeft++;
				potentialWinner = player;
			}
		}
		if (numberOfPlayersLeft <= 1) {
			this.winner = potentialWinner;
		}
		return numberOfPlayersLeft <= 1;
	}

	/**
	 * This method is used for AI-Players. Call the makeTurnAutomatically-method
	 * of the AI-Player and pass a list of enemies that can be attacked.
	 * 
	 * @throws Exception
	 */
	public void makeAiTurn() throws Exception {
		boolean wasShotPossible = false;
		// AI soll Zug automatisch machen
		AIPlayer ai = (AIPlayer) this.currentPlayer;
		Player currentEnemy;
		do {
			do {
				currentEnemy = players[ai.getCurrentEnemyIndex()];
				// zufälligen Gegner auswählen, wenn die KI keine Spur verfolgt,
				// ansonsten gemerkten Gegner beibehalten
				if (!ai.hasTargets() || currentEnemy.hasLost() || ai.equals(currentEnemy) || ai.getType() == PlayerType.DUMB_AI) {
					ai.setRandomEnemyIndex(players.length - 1);
					currentEnemy = players[ai.getCurrentEnemyIndex()];
				}
			} while (currentEnemy.hasLost() || currentEnemy == null || ai.equals(currentEnemy));

			System.out.println("gegner gewählt");
			// Schiff auswählen
			ai.selectShip(ai.getAvailableShipsToShoot().get(0));
			Shot shot = ai.getTarget(getFieldStates(currentEnemy));
			wasShotPossible = makeTurn(currentEnemy, shot.getX(), shot.getY(), shot.getOrientation());
			System.out.println(wasShotPossible);
		} while (!wasShotPossible);

	}

	public boolean makeTurn(Player enemy, int xPos, int yPos, Orientation orientation) throws FieldOutOfBoardException {
		int xDirection = orientation == Orientation.Horizontal ? 1 : 0;
		int yDirection = orientation == Orientation.Vertical ? 1 : 0;
		int x;
		int y;
		for (int i = 0; i < currentPlayer.getCurrentShip().getShootingRange(); i++) {
			x = xPos + i * xDirection;
			y = yPos + i * yDirection;
			boolean isShotPossible = enemy.markBoard(x, y);
			if (i == 0) {
				// erstes Feld belegt
				if (!isShotPossible)
					return false;
			}
		}
		currentPlayer.getCurrentShip().setReloadTimeToMax();
		// Schuss erfolgreich
		return true;
	}

	public Player getWinner() {
		return this.winner;
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
		currentPlayer.decreaseCurrentReloadTimeOfShips();
		int currentPlayerIndex = Arrays.asList(players).indexOf(currentPlayer);
		// wenn letzter Spieler im Array, dann Index wieder auf 0 setzen,
		// ansonsten hochzählen
		currentPlayerIndex = (currentPlayerIndex >= this.players.length - 1) ? currentPlayerIndex = 0 : currentPlayerIndex + 1;
		this.currentPlayer = this.players[currentPlayerIndex];
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
		for (int i = 0; i < this.players.length; i++) {
			if (!this.players[i].hasLost()) {
				if (!this.currentPlayer.equals(players[i])) {
					enemies.add(this.players[i]);
				}
			}
		}
		return enemies;
	}

	public Player[] getPlayers() {
		return players;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
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
			this.players = game.players;
			this.currentPlayer = game.currentPlayer;
			this.winner = game.winner;
			this.turnNumber = game.turnNumber;
			save.close();
		} catch (Exception ex1) {
			throw ex1;
		} finally {
			closeQuietly(saveFile);
			closeQuietly(save);
		}
	}

	private int getIndexOfCurrentPlayer() {
		return Arrays.asList(players).indexOf(currentPlayer);
	}

	public FieldState[][] getFieldStates(int playerIndex) throws FieldOutOfBoardException {
		boolean isOwnBoard = playerIndex == getIndexOfCurrentPlayer();
		int size = boards[playerIndex].getSize();
		FieldState[][] fieldStates = new FieldState[size][size];
		Board board = boards[playerIndex];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				FieldState state = board.getField(j, i).getState();
				if ((state == FieldState.HasShip || state == FieldState.IsEmpty) && (!isOwnBoard)) {
					fieldStates[i][j] = null;
				} else {
					fieldStates[i][j] = state;
				}
			}
		}
		return fieldStates;
	}

	public FieldState[][] getFieldStates(Player player) throws FieldOutOfBoardException {
		int index = Arrays.asList(players).indexOf(player);
		return getFieldStates(index);
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

	public int getTurnNumber() {
		return turnNumber;
	}

	public int getBoardSize() {
		return boards[0].getSize();
	}

}

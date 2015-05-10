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
import java.util.Random;

import de.hsbremen.battleshipextreme.model.exception.FieldOccupiedException;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.exception.ShipAlreadyPlacedException;
import de.hsbremen.battleshipextreme.model.exception.ShipOutOfBoardException;
import de.hsbremen.battleshipextreme.model.player.AIPlayer;
import de.hsbremen.battleshipextreme.model.player.HumanPlayer;
import de.hsbremen.battleshipextreme.model.player.Player;

public class Game implements Serializable {
	private Player[] players;
	private Player currentPlayer;
	private Player winner;
	private int turnNumber;

	public Game(Settings settings) {
		int numberOfHumanPlayers = settings.getPlayers();
		int numberOfAIPlayers = settings.getAiPlayers();
		int numberOfPlayers = numberOfAIPlayers + numberOfHumanPlayers;
		this.players = new Player[numberOfPlayers];
		// menschliche Spieler erzeugen
		for (int i = 0; i < numberOfHumanPlayers; i++)
			this.players[i] = new HumanPlayer(settings.getBoardSize(), settings.getDestroyers(), settings.getFrigates(), settings.getCorvettes(), settings.getSubmarines());
		// KI-Spieler erzeugen
		for (int i = numberOfHumanPlayers; i < numberOfPlayers; i++)
			this.players[i] = new AIPlayer(settings.getBoardSize(), settings.getDestroyers(), settings.getFrigates(), settings.getCorvettes(), settings.getSubmarines());

		this.turnNumber = 0;
		this.currentPlayer = null;
	}

	/**
	 * This constructor is used when a game is loaded.
	 */
	public Game() {
		this.players = new Player[0];
		this.currentPlayer = null;
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
	 * Call the placeShip-method of the current player. If the current player
	 * has placed all of its ships, call the nextPlayer-method, else call the
	 * next ship method.
	 * 
	 * @param xPos
	 * @param yPos
	 * @param orientation
	 * @throws ShipAlreadyPlacedException
	 * @throws FieldOutOfBoardException
	 * @throws ShipOutOfBoardException
	 * @throws FieldOccupiedException
	 */
	public void placeShip(int xPos, int yPos, Orientation orientation) throws ShipAlreadyPlacedException, FieldOutOfBoardException, ShipOutOfBoardException, FieldOccupiedException {
		this.currentPlayer.placeShip(xPos, yPos, orientation);
		if (!this.currentPlayer.hasPlacedAllShips()) {
			this.currentPlayer.nextShip();
		} else {
			this.nextPlayer();
		}
	}

	public void placeShipsAutomatically() {
		((AIPlayer) this.currentPlayer).placeShipsAutomatically();
		this.nextPlayer();
	}

	/**
	 * Call the makeTurn method of the current player. If the turn was
	 * successfully executed, call the nextPlayer-method.
	 * 
	 * @param enemy
	 * @param xPos
	 * @param yPos
	 * @param orientation
	 * @return
	 * @throws Exception
	 */
	public boolean makeTurn(Player enemy, int xPos, int yPos, Orientation orientation) throws Exception {
		boolean hasTurnBeenMade;
		hasTurnBeenMade = this.currentPlayer.makeTurn(enemy, xPos, yPos, orientation);
		if (hasTurnBeenMade)
			this.nextPlayer();
		return hasTurnBeenMade;
	}

	/**
	 * This method is used for AI-Players. Call the makeTurnAutomatically-method
	 * of the AI-Player. Then call the nextPlayer-method.
	 * 
	 * @throws Exception
	 */
	public void makeTurnAutomatically() throws Exception {
		// AI soll Zug automatisch machen
		AIPlayer ai = (AIPlayer) this.currentPlayer;
		ai.makeTurnAutomatically(this.getEnemiesOfCurrentPlayer());
		this.nextPlayer();
	}

	public Player getWinner() {
		return this.winner;
	}

	/**
	 * 
	 * Increase turnNumber and roundNumber.
	 * 
	 * Decrease the reload time of the current players' ships.
	 * 
	 * Set the currentPlayer to the next available player. If a player is not
	 * able to make a turn, skip player.
	 * 
	 * Let AI make its turn automatically.
	 */
	public void nextPlayer() {
		this.turnNumber++;
		this.currentPlayer.decreaseCurrentReloadTimeOfShips();
		int currentPlayerIndex = Arrays.asList(players).indexOf(currentPlayer);
		// wenn letzter Spieler im Array, dann Index wieder auf 0 setzen,
		// ansonsten hochzählen
		currentPlayerIndex = (currentPlayerIndex >= this.players.length - 1) ? currentPlayerIndex = 0 : currentPlayerIndex + 1;
		this.currentPlayer = this.players[currentPlayerIndex];

		// Spieler überspringen, wenn alle Schiffe nachladen oder er tot ist
		if (this.currentPlayer.areAllShipsReloading() || this.currentPlayer.hasLost()) {
			this.nextPlayer();
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
		for (int i = 0; i < this.players.length; i++) {
			if (!this.players[i].hasLost()) {
				if (!this.currentPlayer.equals(players[i])) {
					enemies.add(this.players[i]);
				}
			}
		}
		return enemies;
	}

	/**
	 * Set beginning player by valid id or randomly.
	 */
	public void setBeginningPlayer(int playerId) {
		if (this.currentPlayer == null) {
			if ((playerId < this.players.length) || (playerId < 0)) {
				this.currentPlayer = this.players[playerId];
			} else {
				setBeginningPlayerRandomly();
			}
		}
	}

	public void setBeginningPlayerRandomly() {
		Random rand = new Random();
		if (this.currentPlayer == null) {
			this.currentPlayer = this.players[rand.nextInt(this.players.length)];
		}
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

	public int getTurnNumber() {
		return turnNumber;
	}

	public boolean isNewRound() {
		return turnNumber % players.length == 0;
	}

}

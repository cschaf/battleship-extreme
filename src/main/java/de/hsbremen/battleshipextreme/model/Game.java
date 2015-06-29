package de.hsbremen.battleshipextreme.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.player.AIPlayer;
import de.hsbremen.battleshipextreme.model.player.HumanPlayer;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.model.player.PlayerType;
import de.hsbremen.battleshipextreme.model.player.Target;
import de.hsbremen.battleshipextreme.model.ship.Ship;
import de.hsbremen.battleshipextreme.network.TransferableType;
import de.hsbremen.battleshipextreme.network.transfarableObject.TransferableObject;

public class Game extends TransferableObject {
	private static final long serialVersionUID = -8672232283887859447L;
	private Player[] players;
	private Player currentPlayer;
	private Player winner;
	private int turnNumber;
	private int roundNumber;
	private int boardSize;
	private boolean hasCurrentPlayerMadeTurn;
	private Settings settings;
	private Field[] markedFieldOfLastTurn;

	/**
	 * Initialisiert die benötigten Objekte für das Spiel anhand der übergebenen
	 * Settings.
	 * 
	 * @param settings
	 * 
	 */
	public void initialize(Settings settings) {
		this.settings = settings;
		createPlayers(settings);

		// Spielernummern setzen
		for (int i = 0; i < players.length; i++) {
			players[i].setName(players[i].getName() + (i + 1));
		}

		boardSize = settings.getBoardSize();
		turnNumber = 0;
		roundNumber = 0;
		currentPlayer = players[0];
	}

	/**
	 * Erzeugt die Spieler anhand der übergebenen Settings.
	 * 
	 * @param settings
	 */
	private void createPlayers(Settings settings) {
		int numberOfHumanPlayers = settings.getPlayers();
		int numberOfAIPlayers = settings.getSmartAiPlayers();
		int numberOfDumbAiPlayers = settings.getDumbAiPlayers();
		int numberOfPlayers = numberOfAIPlayers + numberOfHumanPlayers + numberOfDumbAiPlayers;
		players = new Player[numberOfPlayers];
		for (int i = 0; i < numberOfPlayers; i++) {
			if (i < numberOfHumanPlayers) {
				players[i] = new HumanPlayer(settings.getBoardSize(), settings.getDestroyers(), settings.getFrigates(), settings.getCorvettes(), settings.getSubmarines());
			} else {
				if (i < numberOfAIPlayers + numberOfHumanPlayers) {
					players[i] = new AIPlayer(settings.getBoardSize(), settings.getDestroyers(), settings.getFrigates(), settings.getCorvettes(), settings.getSubmarines(), PlayerType.SMART_AI);
				} else {
					players[i] = new AIPlayer(settings.getBoardSize(), settings.getDestroyers(), settings.getFrigates(), settings.getCorvettes(), settings.getSubmarines(), PlayerType.DUMB_AI);
				}
			}
		}
	}

	/**
	 * Die Methode dient zum Ausführen eines Zugs. Sie bekommt den
	 * anzugreifenden Gegner sowie die Position und Ausrichtung des Schusses.
	 * Sie liefert false zurück, wenn ein Schuss nicht möglich ist. Dies ist der
	 * Fall, wenn das Ausgangsfeld bereits beschossen wurde.
	 * 
	 * @param enemy
	 *            der anzugreifende Gegner
	 * @param xPos
	 *            Start-X-Position des Schusses
	 * @param yPos
	 *            Start-Y-Position des Schusses
	 * @param orientation
	 *            Ausrichtung des Schusses
	 * @return true, wenn der Schuss möglich war, false wenn nicht
	 * @throws FieldOutOfBoardException
	 */
	public boolean makeTurn(Player enemy, int xPos, int yPos, Orientation orientation) throws FieldOutOfBoardException {
		Field[] markedFields = new Field[currentPlayer.getCurrentShip().getShootingRange()];
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
			markedFields[i] = enemy.getBoard().getField(x, y);
		}
		boolean isDestroyed = false;
		ArrayList<Field> missedFields = new ArrayList<Field>();
		Field source = null;
		for (int i = 0; i < markedFields.length; i++) {
			if (markedFields[i].hasShip()) {
				if (markedFields[i].getShip().isDestroyed()) {
					source = markedFields[i];
					isDestroyed = true;
				}
			} else {
				missedFields.add(markedFields[i]);
			}
		}
		if (isDestroyed) {
			ArrayList<Field> shipFields = enemy.getBoard().getFieldsOfShip(source);
			shipFields.addAll(missedFields);
			markedFields = shipFields.toArray(markedFields);
		}
		currentPlayer.getCurrentShip().shoot();
		hasCurrentPlayerMadeTurn = true;
		this.markedFieldOfLastTurn = markedFields;
		return true;
	}

	/**
	 * Die Methode dient zum Ausführen eines KI-Zugs. Sie liefert das
	 * Angriffziel (bestehend aus Position und Ausrichtung des Schusses).
	 * 
	 * @return das Angriffsziel
	 * @throws Exception
	 */
	public Target makeAiTurn() throws Exception {
		boolean wasShotPossible = false;
		// AI soll Zug automatisch machen
		AIPlayer ai = (AIPlayer) currentPlayer;
		Target shot = null;

		do {
			Player currentEnemy = selectAiEnemy();
			ai.selectShip(ai.getAvailableShips(true).get(0));
			shot = ai.getTarget(currentEnemy.getFieldStates(false));
			wasShotPossible = makeTurn(currentEnemy, shot.getX(), shot.getY(), shot.getOrientation());
		} while (!wasShotPossible);

		return shot;
	}

	/**
	 * Die Methode dient zum Auswählen eines Gegners für die KI. Dabei wird
	 * geprüft, ob die AI sich einen Gegner vorgemerkt hat. Wenn ja, wird er
	 * beibehalten, wenn nicht, wird ein zufälliger neuer Gegner ausgesucht.
	 * 
	 * @param ai
	 * @return
	 */
	private Player selectAiEnemy() {
		AIPlayer ai = (AIPlayer) currentPlayer;
		Player currentEnemy;
		do {
			currentEnemy = players[ai.getCurrentEnemyIndex()];
			// zufälligen Gegner auswählen, wenn die KI keine Spur verfolgt,
			// ansonsten gemerkten Gegner beibehalten
			if (!ai.hasTargets() || currentEnemy.hasLost() || ai.getType() == PlayerType.DUMB_AI || ai.equals(currentEnemy)) {
				ai.setRandomEnemyIndex(players.length - 1);
				currentEnemy = players[ai.getCurrentEnemyIndex()];
			}
		} while (currentEnemy.hasLost() || ai.equals(currentEnemy));
		return currentEnemy;
	}

	/**
	 * Die Methode dient zum Speichern eines Spiels.
	 * 
	 * @throws Exception
	 *             wenn das Spiel nicht gespeichert werden konnte
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
	 * Die Methode dient zum Laden eines Spiels.
	 * 
	 * @throws Exception
	 *             wenn das Spiel nicht geladen werden konnte.
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
			hasCurrentPlayerMadeTurn = game.hasCurrentPlayerMadeTurn;
			save.close();
		} catch (Exception ex1) {
			throw ex1;
		} finally {
			closeQuietly(saveFile);
			closeQuietly(save);
		}
	}

	/**
	 * InputStream schließen
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
	 * OutputStream schließen
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
	 * Die Methode dient zum Setzen des aktuellen Spielers auf den nächsten
	 * Spieler. Zusätzlich werden die Eigenschaften turnNumber und roundNumber
	 * erhöht, sowie die reloadTime der Schiffe heruntergezählt.
	 */
	public void nextPlayer() {
		turnNumber++;
		decreaseCurrentReloadTimeOfShips(currentPlayer);
		int currentPlayerIndex = Arrays.asList(players).indexOf(currentPlayer);
		// wenn letzter Spieler im Array, dann Index wieder auf 0 setzen,
		// ansonsten hochzählen
		currentPlayerIndex = (currentPlayerIndex >= players.length - 1) ? currentPlayerIndex = 0 : currentPlayerIndex + 1;
		if (currentPlayerIndex == 0) {
			roundNumber++;
		}
		currentPlayer = players[currentPlayerIndex];
		hasCurrentPlayerMadeTurn = false;
	}

	/**
	 * Verringert die reloadTime aller Schiffe des übergebenen Spielers.
	 */
	private void decreaseCurrentReloadTimeOfShips(Player player) {
		Ship[] ships = player.getShips();
		for (Ship ship : ships) {
			ship.decreaseCurrentReloadTime();
		}
	}

	/**
	 * Liefert eine Liste von Spielern die der aktuelle Spieler angreifen kann.
	 * Spieler die verloren haben oder der aktuelle Spieler selbst sind, werden
	 * gefiltert.
	 * 
	 * @return eine Liste von angreifbaren Spielern
	 */
	public ArrayList<Player> getEnemiesOfCurrentPlayer() {
		// angreifbare Gegner des currentPlayers zur�ckgeben
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

	/**
	 * Liefert einen Spieler mit bestimmten Namen.
	 * 
	 * @param name
	 *            Spielername
	 * @return der Spieler mit dem übergebenen Namen
	 */
	public Player getPlayerByName(String name) {
		for (Player player : players) {
			if (player.getName().equals(name)) {
				return player;
			}
		}
		return null;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	/**
	 * Liefert true, wenn alle Spieler ihre Schiffe gesetzt haben. Die Methode
	 * wird benutzt um zu prüfen, ob das Spiel bereit zum Starten ist.
	 * 
	 * @return true, wenn alle Spieler ihre Schiffe gesetzt haben, false wenn
	 *         nicht
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
	 * Prüft ob nur noch ein Spieler übrig ist. Zusätzlich setzt sie die
	 * Eigenschaft winner auf den Spieler der gewonnen hat, wenn das Spiel
	 * vorbei ist.
	 * 
	 * @return true wenn das Spiel vorbei ist, false wenn nicht
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

	public void setCurrentPlayer(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
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

	public boolean hasCurrentPlayerMadeTurn() {
		return hasCurrentPlayerMadeTurn;
	}

	public void setPlayerBoards(ArrayList<Board> playerBoards) {

		for (int i = 0; i < players.length; i++) {
			HashMap<Ship, ArrayList<Field>> shipMap = getShipMap(playerBoards.get(i));

			Player player = new HumanPlayer(boardSize, shipMap);
			player.setName(players[i].getName());
			for (Ship ship : player.getShips()) {
				ship.setPlaced(true);
			}
			players[i] = player;
		}
		currentPlayer = players[0];
	}

	private HashMap<Ship, ArrayList<Field>> getShipMap(Board board) {
		HashMap<Ship, ArrayList<Field>> shipMap = new HashMap<Ship, ArrayList<Field>>();
		Field[][] fields = board.getFields();
		for (int row = 0; row < board.getSize(); row++) {
			for (int column = 0; column < board.getSize(); column++) {
				if (fields[row][column].getShip() != null) {
					if (!shipMap.containsKey(fields[row][column].getShip())) {
						ArrayList<Field> shipFields = new ArrayList<Field>();
						shipFields.add(fields[row][column]);
						shipMap.put(fields[row][column].getShip(), shipFields);
					} else {
						ArrayList<Field> shipFields = shipMap.get(fields[row][column].getShip());
						shipFields.add(fields[row][column]);
						shipMap.put(fields[row][column].getShip(), shipFields);
					}
				}
			}
		}

		return shipMap;
	}

	public TransferableType getType() {
		return TransferableType.Game;
	}

	public Field[] getMarkedFieldOfLastTurn() {
		return markedFieldOfLastTurn;
	}
}

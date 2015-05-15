package de.hsbremen.battleshipextreme;

import java.util.ArrayList;
import java.util.Scanner;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.model.Game;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.model.exception.BoardTooSmallException;
import de.hsbremen.battleshipextreme.model.exception.FieldOccupiedException;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.exception.InvalidNumberOfShipsException;
import de.hsbremen.battleshipextreme.model.exception.InvalidPlayerNumberException;
import de.hsbremen.battleshipextreme.model.exception.ShipAlreadyPlacedException;
import de.hsbremen.battleshipextreme.model.exception.ShipOutOfBoardException;
import de.hsbremen.battleshipextreme.model.player.AIPlayer;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.model.player.PlayerType;
import de.hsbremen.battleshipextreme.model.ship.Ship;

public class Main {
	public static final void main(String[] args) {
		new ConsoleGame();
	}
}

class ConsoleGame {
	private Scanner input;
	private static final String SAVEGAME_FILENAME = "savegame.sav";
	private Game game;

	public ConsoleGame() {
		input = new Scanner(System.in);
		createGame();
		gameLoop();
		printGameStats();
		input.close();
	}

	private void createGame() {
		// bietet mehrere Wege ein Spiel zu erstellen
		// wird solange wiederholt, bis ein Spiel erstellt werden konnte
		this.game = null;
		do {
			System.out.println("(1) Erzeuge Spiel manuell");
			System.out.println("(2) AI-Kampf (1 schlaue KI und 1 dumme KI)");
			System.out.println("(3) AI-Kampf (1 schlaue KIs und 5 dumme KIs)");
			System.out
					.println("(4) AI-Benchmark (Zeigt Runden-Durchschnitt von 1000 Spielen mit 2 schlauen KIs)");
			System.out.println("(5) Zuletzt gespeichertes Spiel fortsetzen");
			int choice = readIntegerWithMinMax(1, 5);
			switch (choice) {
			case 1:
				createGameManually();
				break;
			case 2:
				createAiGame(1, 1);
				break;
			case 3:
				createAiGame(1, 5);
				break;
			case 4:
				createKIBenchmark();
				break;
			case 5:
				tryToLoadGame();
			}
		} while (this.game == null);
	}

	private void createGameManually() {
		// Spiel mit manuellen Einstellungen erzeugen
		Settings settings = createSettings();
		if (settings != null) {
			game = new Game(settings);
			game.setBeginningPlayer(0);
			placeShips();
		}
	}

	private void createAiGame(int numberOfSmartAis, int numberOfDumbAis) {
		Settings settings = null;
		try {
			settings = new Settings(0, numberOfSmartAis, numberOfDumbAis, 10,
					1, 1, 1, 1);
		} catch (BoardTooSmallException e) {
			e.printStackTrace();
		} catch (InvalidPlayerNumberException e) {
			e.printStackTrace();
		} catch (InvalidNumberOfShipsException e) {
			e.printStackTrace();
		}

		if (settings != null) {
			game = new Game(settings);
			game.setBeginningPlayer(0);
			placeShips();
		}
	}

	private void createKIBenchmark() {
		// Methode dient zum Auswerten der Effektivität einer KI
		//
		// erzeugt mehrere Spiele und gibt am Ende den
		// Durchschnitt der benötigten Rundenanzahl aus
		//
		// eine AI die zufällig schießt, braucht im Schnitt ca. 70 Runden

		int numberOfGames = 1000;
		int[] roundNumbersOfEachGame = new int[numberOfGames];

		for (int i = 0; i < numberOfGames; i++) {
			System.out.println("Spiel-Nr" + i);
			// Spiel mit zwei schlauen Ais erzeugen
			createAiGame(0, 2);
			gameLoop();
			roundNumbersOfEachGame[i] = (int) Math.floor(game.getTurnNumber()
					/ game.getPlayers().length);
		}
		// Durchschnitt ausrechnen
		int sum = 0;
		for (Integer i : roundNumbersOfEachGame) {
			sum += i;
		}
		float average = (float) sum / (float) numberOfGames;
		System.out
				.println("------------------------------------------------------------------------");
		System.out.println("Durchschnittliche Rundenanzahl aus "
				+ numberOfGames + ": " + average);
		System.exit(0);
	}

	private Game tryToLoadGame() {
		// gespeichertes Spiel fortsetzen
		game = new Game();
		try {
			game.load(SAVEGAME_FILENAME);
		} catch (Exception e) {
			System.out.println("Spiel konnte nicht geladen werden");
			game = null;
		}
		return game;
	}

	private Settings createSettings() {
		// Settings manuell einlesen
		System.out.println("Einstellungen:");
		System.out.print("Anzahl der menschlichen Spieler (0-4): ");
		int players = readIntegerWithMinMax(0, 4);
		System.out.print("Anzahl der KI-Spieler (0-4): ");
		int aiPlayers = readIntegerWithMinMax(0, 4);
		System.out.print("Groesse des Spielfeldes (10-20): ");
		int boardSize = readIntegerWithMinMax(10, 20);
		System.out.print("Zerstoerer: ");
		int destroyers = readInteger();
		System.out.print("Fregatten: ");
		int frigates = readInteger();
		System.out.print("Korvetten: ");
		int corvettes = readInteger();
		System.out.print("U-Boote: ");
		int submarines = readInteger();

		try {
			return new Settings(players, aiPlayers, 0, boardSize, destroyers,
					frigates, corvettes, submarines);
		} catch (BoardTooSmallException e1) {
			System.out
					.println("Das Board ist zu klein! Benötigte Prozentzahl freier Felder: "
							+ e1.getMinPercentageOfFieldsThatShouldBeEmpty()
							+ "%, dein Feld hat nur: "
							+ e1.getEmptyFieldPercentage() + "%");
		} catch (InvalidPlayerNumberException e) {
			System.out.println("Spieleranzahl muss zwischen "
					+ e.getMinPlayers() + " und " + e.getMaxPlayers()
					+ " liegen.");
		} catch (InvalidNumberOfShipsException e) {
			System.out.println("Es muss mindestens ein Schiff existieren!");
		}
		return null;
	}

	private void setPlayerNames(Player[] players) {
		System.out.println("\nSpielernamen:");
		for (Player player : players) {
			System.out.print("Name für " + player + " : ");
			player.setName(input.nextLine());
		}
	}

	private void placeShips() {
		// Schiffe setzen
		do {
			Player currentPlayer = game.getCurrentPlayer();
			if (currentPlayer.getType() == PlayerType.AI) {
				// wenn KI dran ist, keine Koordinaten einlesen und automatisch
				// platzieren
				System.out.println("Ai setzt Schiffe...");
				((AIPlayer) currentPlayer).placeShips();
				game.nextPlayer();
			} else {
				// wenn nicht, Koordinaten einlesen
				placeShipsManually();
			}
			System.out.println();
			System.out.println("Board von " + currentPlayer);
			printBoard(currentPlayer.getBoard(), true);
		} while (!game.isReady());

	}

	private void placeShipsManually() {
		Player currentPlayer = game.getCurrentPlayer();
		System.out.println(currentPlayer + " setzt Schiff: "
				+ currentPlayer.getCurrentShip());
		boolean isItPossibleToPlaceShip;
		do {
			// solange Schiffskoordinaten einlesen, bis keine Exception
			// auftritt
			int[] coordinates = readCoordinates(game.getCurrentPlayer()
					.getBoard().getSize());
			Orientation orientation = readOrientation();
			isItPossibleToPlaceShip = false;
			try {
				game.getCurrentPlayer().placeShip(coordinates[1],
						coordinates[0], orientation);
				isItPossibleToPlaceShip = true;
			} catch (ShipAlreadyPlacedException e) {
				System.out.println("Schiff bereits gesetzt!");
			} catch (FieldOutOfBoardException e) {
				System.out.println("Feld nicht im Board!");
			} catch (ShipOutOfBoardException e) {
				System.out.println("Schiff (teilweise) nicht im Board!");
			} catch (FieldOccupiedException e) {
				System.out.println("Feld bereits belegt!");
			}
		} while (!isItPossibleToPlaceShip);
		if (!currentPlayer.hasPlacedAllShips()) {
			currentPlayer.nextShip();
		} else {
			game.nextPlayer();
		}

	}

	private void gameLoop() {
		do {
			Player currentPlayer = game.getCurrentPlayer();
			// Rundenanzahl ausgeben
			if (game.getTurnNumber() % game.getPlayers().length == 0) {
				System.out.println();
				System.out.println("Runde "
						+ (int) Math.floor(game.getTurnNumber()
								/ game.getPlayers().length));
				System.out
						.println("------------------------------------------------------------------------");
				System.out.println();
			}
			// Spieler überspringen wenn er tot ist
			if (currentPlayer.hasLost()) {
				System.out.println(currentPlayer
						+ " ist tot und kann nicht schießen.");
			} else if (currentPlayer.areAllShipsReloading()) {
				System.out.println(currentPlayer
						+ " kann nicht schießen, da alle Schiffe nachladen.");
			} else {
				// ist der aktuelle Spieler eine KI
				if (game.getCurrentPlayer().getType() == PlayerType.AI) {
					makeAITurn();
				} else {
					makePlayerTurn();
				}

			}
			game.nextPlayer();
		} while (!game.isGameover());
	}

	private void makeAITurn() {
		AIPlayer ai = (AIPlayer) game.getCurrentPlayer();
		try {
			game.makeTurnAutomatically();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// TODO
		// von AI beschossenes Board ausgeben
		if (ai.getName().equals("Schlaue KI1")) {
			System.out.println(ai + " greift " + ai.getCurrentEnemy() + " mit "
					+ ai.getCurrentShip() + " an.");
			System.out.println();
			System.out.println("Board von " + ai.getCurrentEnemy());
			System.out.println();

			printBoard(ai.getCurrentEnemy().getBoard(), false);
			System.out.println();
		}
	}

	private void makePlayerTurn() {
		Player currentPlayer = game.getCurrentPlayer();
		// mögliche Spieleraktionen auflisten
		System.out.println();
		System.out.println(currentPlayer + " ist an der Reihe.");
		System.out.println();
		System.out.println("Was möchtest du tun?");
		System.out.println("(1) Gegner angreifen");
		System.out.println("(2) Spiel speichern");
		System.out.println("(3) Spiel beenden");
		boolean hasAttacked = false;
		do {
			// Wahl einlesen
			int choice = readIntegerWithMinMax(1, 3);
			switch (choice) {
			case 1:
				attackManually();
				hasAttacked = true;
				break;
			case 2:
				saveGame();
				break;
			case 3:
				System.exit(0);
			}
		} while (!hasAttacked);
	}

	private void attackManually() {
		Player enemy;
		Player currentPlayer;
		currentPlayer = game.getCurrentPlayer();

		// Auswahl des zu schießenden Schiffs
		System.out.println("Welches Schiff soll schießen?");
		selectShip();

		// Auswahl des Gegners, auf den geschossen werden soll
		System.out.println("Auf welchen Spieler?");
		enemy = selectEnemy(game.getEnemiesOfCurrentPlayer());
		printBoard(enemy.getBoard(), false);
		boolean isShotPossible = false;
		do {
			// Koordinaten einlesen, bis Schuss erfolgreich ausgeführt wurde
			int[] coordinates = readCoordinates(currentPlayer.getBoard()
					.getSize());
			try {
				isShotPossible = currentPlayer.makeTurn(enemy, coordinates[1],
						coordinates[0], readOrientation());
				if (!isShotPossible)
					System.out.println("Feld wurde bereits beschossen!");
				else
					printBoards(currentPlayer.getBoard(), enemy.getBoard());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (!isShotPossible);

	}

	private Ship selectShip() {
		Player currentPlayer = game.getCurrentPlayer();
		ArrayList<Ship> availableShips = currentPlayer.getAvailableShips();
		Ship selectedShip;
		boolean isShipSelected = false;
		do {
			// Eingabe wiederholen bis Schiff gewählt wurde, das schießen kann
			for (Ship s : availableShips) {
				System.out.println("(" + availableShips.indexOf(s) + ") "
						+ s.getType() + "(reload:" + s.getCurrentReloadTime()
						+ "," + " health:" + s.getSize() + ")");
			}
			selectedShip = availableShips.get(readIntegerWithMinMax(0,
					availableShips.size() - 1));
			isShipSelected = currentPlayer.selectShip(selectedShip);
			if (!isShipSelected)
				System.out.println("Schiff lädt nach");
		} while (!isShipSelected);
		return selectedShip;
	}

	private Player selectEnemy(ArrayList<Player> enemies) {
		// angreifbare Gegner anzeigen
		for (int i = 0; i < enemies.size(); i++) {
			System.out.println("(" + i + ")" + enemies.get(i));
		}
		return enemies.get(readIntegerWithMinMax(0, enemies.size() - 1));
	}

	private void saveGame() {
		try {
			game.save(SAVEGAME_FILENAME);
		} catch (Exception e) {
			System.err.print("Das Spiel konnte nicht gespeichert werden.");
			e.printStackTrace();
		}
		System.out.println();
		System.out.println("Spiel gespeichert.");
		System.out.println();
	}

	private void printBoards(Board ownBoard, Board enemyBoard) {
		System.out.println();
		System.out.println("Eigenes Board");
		System.out.println();
		printBoard(ownBoard, true);
		System.out.println();
		System.out.println("Board des Gegners");
		System.out.println();
		printBoard(enemyBoard, false);
		System.out
				.println("O = getroffenes Schiff\nX = daneben\n+ = eigenes Schiff\n- = leer \nU = unbekannt\n! = zerstörtes Schiff\n");
	}

	private void printBoard(Board board, boolean isOwnBoard) {
		Field[][] fields = board.getFields();
		for (int row = 0; row < fields.length; row++) {
			for (int column = 0; column < fields[row].length; column++) {
				Field field = fields[row][column];
				printState(field.getState(), isOwnBoard);
				System.out.print(" ");
			}
			System.out.println();
		}
		System.out.println();
	}

	private void printGameStats() {
		System.out.println();
		System.out.println("Spiel zu Ende");
		System.out.println((int) Math.floor(game.getTurnNumber()
				/ game.getPlayers().length)
				+ " Runden");
		for (Player player : game.getPlayers()) {
			if (player.hasLost()) {
				System.out.println(player + " ist tot.");
			}
		}
		System.out.println(game.getWinner() + " hat gewonnen!");
	}

	private void printState(FieldState fieldState, boolean isOwnBoard) {
		String s = "";
		switch (fieldState) {
		case Destroyed:
			s = "!";
			break;
		case Hit:
			s = "O";
			break;
		case Missed:
			s = "X";
			break;
		case HasShip:
			s = isOwnBoard ? "+" : "?";
			break;
		case IsEmpty:
			s = isOwnBoard ? "-" : "?";
			break;
		default:
			break;
		}
		System.out.print(s);
	}

	private int readInteger() {
		while (!input.hasNextInt()) {
			System.out.println("Eine Zahl eingeben!");
			input.next();
		}
		return input.nextInt();
	}

	private int readIntegerWithMinMax(int min, int max) {
		int i;
		boolean isValid = false;
		do {
			i = readInteger();
			isValid = (i >= min) && (i <= max);
			if (!isValid)
				System.out.println("Zahl zwischen min " + min + " und " + max
						+ " eingeben!");
		} while (!isValid);
		return i;
	}

	private int[] readCoordinates(int boardSize) {
		// Zeile einlesen
		System.out.print("Zeile (1-" + boardSize + "): ");
		int row = readIntegerWithMinMax(1, boardSize) - 1;

		// Spalte einlesen
		System.out.print("Spalte (1-" + boardSize + "): ");
		int column = readIntegerWithMinMax(1, boardSize) - 1;
		return new int[] { row, column };
	}

	private Orientation readOrientation() {
		// Ausrichtung einlesen
		System.out.print("Ausrichtung (H/V): ");
		Orientation orientation = input.next().toUpperCase().charAt(0) == 'V' ? Orientation.Vertical
				: Orientation.Horizontal;
		return orientation;
	}
}

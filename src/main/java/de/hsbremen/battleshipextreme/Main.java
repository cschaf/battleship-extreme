package de.hsbremen.battleshipextreme;

import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.model.Game;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.model.exception.*;
import de.hsbremen.battleshipextreme.model.player.AIPlayer;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.model.ship.Ship;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		new ConsoleGame();
	}
}

class ConsoleGame {
	private static final String SAVEGAME_FILENAME = "savegame.sav";
	private Scanner input;
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
			System.out.println("(4) AI-Benchmark (Zeigt Runden-Durchschnitt von 1000 Spielen mit 2 schlauen KIs)");
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
				createAiGame(1, 4);
				break;
			case 4:
				createKIBenchmark();
				break;
			case 5:
				tryToLoadGame();
				if (game != null)
					this.makePlayerTurn();
			}
		} while (this.game == null);
	}

	private void createGameManually() {
		// Spiel mit manuellen Einstellungen erzeugen
		Settings settings = createSettings();
		if (settings != null) {
			game = new Game();
			game.initialize(settings);
			placeShips();
		}
	}

	private void createAiGame(int numberOfSmartAis, int numberOfDumbAis) {
		Settings settings = null;
		settings = new Settings(0, numberOfSmartAis, numberOfDumbAis, 10, 1, 1, 1, 1);
		try {
			settings.validate();
		} catch (BoardTooSmallException e) {
			e.printStackTrace();
		} catch (InvalidPlayerNumberException e) {
			e.printStackTrace();
		} catch (InvalidNumberOfShipsException e) {
			e.printStackTrace();
		}

		if (settings != null) {
			game = new Game();
			game.initialize(settings);
			placeShips();
		}
	}

	private void createKIBenchmark() {
		// Methode dient zum Auswerten der Effektivität einer KI
		//
		// erzeugt mehrere Spiele und gibt am Ende den
		// Durchschnitt der benötigten Rundenanzahl aus
		//
		// eine AI die zufällig schießt, braucht im Schnitt ca. 77 Runden

		int numberOfGames = 1000;
		int[] roundNumbersOfEachGame = new int[numberOfGames];

		for (int i = 0; i < numberOfGames; i++) {
			System.out.println("Spiel-Nr" + i);
			// Spiel mit zwei schlauen Ais erzeugen
			createAiGame(1, 1);
			gameLoop();
			roundNumbersOfEachGame[i] = game.getRoundNumber();
		}
		// Durchschnitt ausrechnen
		int sum = 0;
		for (Integer i : roundNumbersOfEachGame) {
			sum += i;
		}
		float average = (float) sum / (float) numberOfGames;
		System.out.println("------------------------------------------------------------------------");
		System.out.println("Durchschnittliche Rundenanzahl aus " + numberOfGames + ": " + average);
		System.exit(0);
	}

	private void tryToLoadGame() {
		// gespeichertes Spiel fortsetzen
		try {
			game = new Game();
			game.load(SAVEGAME_FILENAME);
		} catch (Exception e) {
			System.out.println("Spiel konnte nicht geladen werden");
			game = null;
		}
	}

	private Settings createSettings() {
		// Settings manuell einlesen
		int maxPlayers = Settings.MAX_PLAYERS;
		int minPlayers = Settings.MIN_PLAYERS;
		int players = 0;
		int aiPlayers = 0;

		System.out.println("Einstellungen:");
		do {
			System.out.print("Anzahl der menschlichen Spieler (0-" + maxPlayers + "): ");
			players = readIntegerWithMinMax(0, maxPlayers);
			int playersLeft = maxPlayers - players;

			if (playersLeft > 0) {
				System.out.print("Anzahl der KI-Spieler (0-" + playersLeft + "): ");
				aiPlayers = readIntegerWithMinMax(0, playersLeft);
			} else {
				aiPlayers = 0;
				System.out.println("Kein Platz mehr für KI-Spieler.");
			}
			if (players + aiPlayers < minPlayers)
				System.out.println("Zu wenig Spieler!");
		} while (players + aiPlayers < minPlayers);

		System.out.println("Anzahl der Schiffe:");
		System.out.print("Zerstoerer: ");
		int destroyers = readIntegerWithMinMax(0, 100);
		System.out.print("Fregatten: ");
		int frigates = readIntegerWithMinMax(0, 100);
		System.out.print("Korvetten: ");
		int corvettes = readIntegerWithMinMax(0, 100);
		System.out.print("U-Boote: ");
		int submarines = readIntegerWithMinMax(0, 100);

		// Mindest Größe des Feldes berechnen
		int requiredFields = Settings.getRequiredFields(destroyers, corvettes, frigates, submarines);
		int requiredBoardSize = Settings.getRequiredBoardSize(requiredFields);
		System.out.println("Ermittle MindestGröße des Boards...");
		int boardSize = 0;
		if (requiredBoardSize <= Settings.MAX_BOARD_SIZE) {
			System.out.print("Groesse des Spielfeldes (" + requiredBoardSize + "-" + Settings.MAX_BOARD_SIZE + "): ");
			boardSize = readIntegerWithMinMax(requiredBoardSize, Settings.MAX_BOARD_SIZE);
		} else {
			System.out.println("Die ermittelte Mindestgröße des Boards übersteigt die maximale Größe von " + Settings.MAX_BOARD_SIZE + "!");
		}

		Settings settings = new Settings(players, aiPlayers, 0, boardSize, destroyers, frigates, corvettes, submarines);

		try {
			settings.validate();
			return settings;
		} catch (BoardTooSmallException e1) {
			System.out.println("Das Board ist zu klein!");
		} catch (InvalidPlayerNumberException e) {
			System.out.println("Spieleranzahl muss zwischen " + e.getMinPlayers() + " und " + e.getMaxPlayers() + " liegen.");
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
			if (currentPlayer instanceof AIPlayer) {
				// wenn KI dran ist, keine Koordinaten einlesen und automatisch
				// platzieren
				System.out.println(currentPlayer + " setzt Schiffe...");
				try {
					((AIPlayer) currentPlayer).placeShips();
				} catch (ShipAlreadyPlacedException e) {
					e.printStackTrace();
				} catch (FieldOutOfBoardException e) {
					e.printStackTrace();
				} catch (ShipOutOfBoardException e) {
					e.printStackTrace();
				}

				System.out.println();
				System.out.println("Board von " + currentPlayer);
				System.out.println();
				try {
					printBoard(game.getCurrentPlayer().getFieldStates(true));
				} catch (FieldOutOfBoardException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				game.nextPlayer();
			} else {
				// wenn nicht, Koordinaten einlesen
				placeShipsManually();
			}

		} while (!game.isReady());

	}

	private void placeShipsManually() {
		Player currentPlayer = game.getCurrentPlayer();
		boolean isItPossibleToPlaceShip;
		do {
			System.out.println(currentPlayer + " setzt Schiff: " + currentPlayer.getCurrentShip());
			// solange Schiffskoordinaten einlesen, bis keine Exception
			// auftritt
			int[] coordinates = readCoordinates(game.getBoardSize());
			Orientation orientation = readOrientation();
			isItPossibleToPlaceShip = false;
			try {
				isItPossibleToPlaceShip = game.getCurrentPlayer().placeShip(coordinates[1], coordinates[0], orientation);
				if (!isItPossibleToPlaceShip) {
					System.out.println("Feld bereits belegt oder darf nicht belegt werden");
					System.out.println("Board zurücksetzen? (J/N)");
					boolean reset = input.next().toUpperCase().charAt(0) == 'J' ? true : false;
					if (reset) {
						currentPlayer.resetBoard();
						System.out.println("Board zurückgesetzt.");
					}

				}
			} catch (ShipAlreadyPlacedException e) {
				System.out.println("Schiff bereits gesetzt!");
			} catch (FieldOutOfBoardException e) {
				System.out.println("Feld nicht im Board!");
			} catch (ShipOutOfBoardException e) {
				System.out.println("Schiff (teilweise) nicht im Board!");
			}
		} while (!isItPossibleToPlaceShip);

		try {
			System.out.println();
			printBoard(currentPlayer.getFieldStates(true));
		} catch (FieldOutOfBoardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
				System.out.println("Runde " + game.getRoundNumber());
				System.out.println("------------------------------------------------------------------------");
				System.out.println();
			}
			// Spieler überspringen wenn er tot ist
			if (currentPlayer.hasLost()) {
				System.out.println(currentPlayer + " ist besiegt und kann nicht schießen.");
			} else if (currentPlayer.areAllShipsReloading()) {
				System.out.println(currentPlayer + " kann nicht schießen, da alle Schiffe nachladen.");
			} else {
				// ist der aktuelle Spieler eine KI
				if (currentPlayer instanceof AIPlayer) {
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
			game.makeAiTurn();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// TODO
		// von AI beschossenes Board ausgeben
		// if (ai.getName().equals("SMART_AI1")) {
		System.out.println(ai + " greift " + game.getPlayers()[ai.getCurrentEnemyIndex()] + " mit " + ai.getCurrentShip() + " an.");
		System.out.println();
		System.out.println("Board von " + game.getPlayers()[ai.getCurrentEnemyIndex()]);
		try {
			printBoard(game.getPlayers()[ai.getCurrentEnemyIndex()].getFieldStates(false));
		} catch (FieldOutOfBoardException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// }
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

		// Auswahl des zu schießenden Schiffs
		System.out.println("Welches Schiff soll schießen?");
		selectShip();

		// Auswahl des Gegners, auf den geschossen werden soll
		System.out.println("Auf welchen Spieler?");
		enemy = selectEnemy(game.getEnemiesOfCurrentPlayer());
		try {
			printBoard(enemy.getFieldStates(false));
		} catch (FieldOutOfBoardException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		boolean isShotPossible = false;
		do {
			// Koordinaten einlesen, bis Schuss erfolgreich ausgeführt wurde
			int[] coordinates = readCoordinates(game.getBoardSize());
			try {
				isShotPossible = game.makeTurn(enemy, coordinates[1], coordinates[0], readOrientation());
				if (!isShotPossible)
					System.out.println("Feld wurde bereits beschossen!");
				else
					printBoards(game.getCurrentPlayer().getFieldStates(true), enemy.getFieldStates(false));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (!isShotPossible);

	}

	private void selectShip() {
		Player currentPlayer = game.getCurrentPlayer();
		ArrayList<Ship> availableShips = currentPlayer.getAvailableShips(false);
		Ship selectedShip;
		do {
			// Eingabe wiederholen bis Schiff gewählt wurde, das schießen kann
			for (Ship s : availableShips) {
				System.out.println("(" + availableShips.indexOf(s) + ") " + s.getType() + "(reload:" + s.getCurrentReloadTime() + "," + " health:" + s.getSize() + ")");
			}
			selectedShip = availableShips.get(readIntegerWithMinMax(0, availableShips.size() - 1));
			try {
				currentPlayer.selectShip(selectedShip);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (currentPlayer.getCurrentShip().isReloading()) {
				System.out.println("Schiff lädt nach");
			}
		} while (currentPlayer.getCurrentShip().isReloading());
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

	private void printBoards(FieldState[][] ownBoard, FieldState[][] enemyBoard) {
		System.out.println();
		System.out.println("Eigenes Board");
		System.out.println();
		printBoard(ownBoard);
		System.out.println();
		System.out.println("Board des Gegners");
		System.out.println();
		printBoard(enemyBoard);
		System.out.println("O = getroffenes Schiff\nX = daneben\n+ = eigenes Schiff\n- = leer \nU = unbekannt\n! = zerstörtes Schiff\n");
	}

	private void printBoard(FieldState[][] fieldStates) {

		this.printFieldColumnNumbers(fieldStates[0].length);
		System.out.println();
		for (int row = 0; row < fieldStates.length; row++) {
			String number = row + 1 < 10 ? "0" + (row + 1) : "" + (row + 1);
			System.out.print(number + "\t");
			for (int column = 0; column < fieldStates[row].length; column++) {
				FieldState field = fieldStates[row][column];
				printState(field);
				System.out.print("\t");
			}
			System.out.println();
		}
		System.out.println();
	}

	private void printFieldColumnNumbers(int length) {
		String result = "\t";
		for (int i = 1; i <= length; i++) {
			String number = "" + i + "\t";
			if (i < 10) {
				number = "0" + i + "\t";
			}
			result += number;
		}
		System.out.println(result);
	}

	private void printGameStats() {
		System.out.println();
		System.out.println("Spiel zu Ende");
		System.out.println((int) Math.floor(game.getTurnNumber() / game.getPlayers().length) + " Runden");
		for (Player player : game.getPlayers()) {
			if (player.hasLost()) {
				System.out.println(player + " ist besiegt.");
			}
		}
		System.out.println(game.getWinner() + " hat gewonnen!");
	}

	private void printState(FieldState fieldState) {
		String s = "";
		if (fieldState == null) {
			s = "?";
		} else {
			switch (fieldState) {
			case DESTROYED:
				s = "!";
				break;
			case HIT:
				s = "O";
				break;
			case MISSED:
				s = "X";
				break;
			case HAS_SHIP:
				s = "+";
				break;
			case IS_EMPTY:
				s = "-";
				break;
			default:
				break;
			}
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
				System.out.println("Zahl zwischen min " + min + " und " + max + " eingeben!");
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
		Orientation orientation = input.next().toUpperCase().charAt(0) == 'V' ? Orientation.VERTICAL : Orientation.HORIZONTAL;
		return orientation;
	}
}

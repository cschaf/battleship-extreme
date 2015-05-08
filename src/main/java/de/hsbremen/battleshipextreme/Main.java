package de.hsbremen.battleshipextreme;

import java.util.ArrayList;
import java.util.Scanner;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.model.Game;
import de.hsbremen.battleshipextreme.model.InvalidNumberOfShipsException;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.model.exception.BoardTooSmallException;
import de.hsbremen.battleshipextreme.model.exception.FieldOccupiedException;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.exception.InvalidPlayerNumberException;
import de.hsbremen.battleshipextreme.model.exception.ShipAlreadyPlacedException;
import de.hsbremen.battleshipextreme.model.exception.ShipOutOfBoardException;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.model.ship.Ship;

public class Main {
	static Scanner input = new Scanner(System.in);
	private static final String SAVEGAME_FILENAME = "savegame.sav";

	public static void main(String[] args) {
		Game game = createGame();
		gameLoop(game);
		System.out.println("Spiel zu Ende");
		System.out.println(game.getWinner() + " hat gewonnen!");
		input.close();
	}

	private static Game createGame() {
		// bietet mehrere Wege ein Spiel zu erstellen
		Game game = null;
		do {
			System.out.println("(1) Erzeuge Spiel manuell");
			System.out.println("(2) Erzeuge Spiel automatisch");
			System.out.println("(3) Zuletzt gespeichertes Spiel fortsetzen");
			int choice = readIntegerWithMinMax(1, 3);
			switch (choice) {
			case 1:
				game = createGameManually();
				break;
			case 2:
				game = createGameWithoutInput();
				break;
			case 3:
				game = tryToLoadGame();
			}
		} while (game == null);
		return game;
	}

	private static Game createGameManually() {
		// Spiel mit manuellen Einstellungen erzeugen
		Game game = null;
		Settings settings = generateSettings();
		if (settings != null) {
			game = new Game(settings);
			game.setBeginningPlayer(0);
			placeShips(game);
		}
		return game;
	}

	private static Game createGameWithoutInput() {
		// Spiel mit automatischen Einstellungen erzeugen
		Game game = null;
		Settings settings = null;

		try {
			settings = new Settings(3, 10, 2, 1, 1, 1);
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
			placeShipsWithoutInput(game);
		}
		return game;
	}

	private static Game tryToLoadGame() {
		// gespeichertes Spiel fortsetzen
		Game game = new Game();
		try {
			game.load(SAVEGAME_FILENAME);
		} catch (Exception e) {
			System.out.println("Spiel konnte nicht geladen werden");
			game = null;
		}
		return game;
	}

	private static void placeShipsWithoutInput(Game game) {
		// schnell Schiffe ohne Eingabe setzen
		Player player;
		do {
			player = game.getCurrentPlayer();
			for (int i = 0; i < player.getShips().length; i++) {
				Ship ship = player.getShips()[i];
				int row = 2 * i;
				int column = 0;
				Orientation orientation = Orientation.Horizontal;
				try {
					player.placeShip(ship, column, row, orientation);
				} catch (ShipAlreadyPlacedException e) {
					e.printStackTrace();
				} catch (FieldOutOfBoardException e) {
					e.printStackTrace();
				} catch (ShipOutOfBoardException e) {
					e.printStackTrace();
				} catch (FieldOccupiedException e) {
					e.printStackTrace();
				}
			}
			System.out.println();
			System.out.println("Board von " + player);
			printBoard(player.getBoard(), true);
			game.nextPlayer();
		} while (!game.isReady());
	}

	private static Settings generateSettings() {
		System.out.println("Einstellungen:");
		System.out.print("Anzahl der Spieler (2-6): ");
		int players = readIntegerWithMinMax(2, 6);
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
			return new Settings(players, boardSize, destroyers, frigates, corvettes, submarines);
		} catch (BoardTooSmallException e1) {
			System.out.println("Das Board ist zu klein! Benˆtigte Prozentzahl freier Felder: " + e1.getMinPercentageOfFieldsThatShouldBeEmpty() + "%, dein Feld hat nur: "
					+ e1.getEmptyFieldPercentage() + "%");
		} catch (InvalidPlayerNumberException e) {
			System.out.println("Spieleranzahl muss zwischen " + e.getMinPlayers() + " und " + e.getMaxPlayers() + " liegen.");
		} catch (InvalidNumberOfShipsException e) {
			// TODO Auto-generated catch block
			System.out.println("Es muss mindestens ein Schiff existieren!");
		}
		return null;
	}

	private static void setPlayerNames(Player[] players) {
		System.out.println("\nSpielernamen:");
		for (Player player : players) {
			System.out.print("Name f¸r " + player + " : ");
			player.setName(input.nextLine());
		}
	}

	private static void placeShips(Game game) {
		// Schiffe manuell setzen
		Player currentPlayer;
		Ship currentShip;
		do {
			currentPlayer = game.getCurrentPlayer();
			System.out.println("\nPlatziere Schiffe fuer " + currentPlayer + ":");
			do {
				currentShip = currentPlayer.getCurrentShip();
				placeShip(currentShip, currentPlayer, currentPlayer.getBoard());
				System.out.println();
				System.out.println("Board von " + currentPlayer);
				printBoard(currentPlayer.getBoard(), true);
				currentPlayer.nextShip();
			} while (!currentPlayer.hasPlacedAllShips());
			game.nextPlayer();
		} while (!game.isReady());

	}

	private static void placeShip(Ship ship, Player player, Board board) {
		boolean isItPossibleToPlaceShip;
		do {
			// solange Schiffskoordinaten einlesen, bis keine Exception
			// auftritt
			System.out.println("\nPlatziere " + ship + ":");

			// Zeile einlesen
			System.out.print("Zeile (1-" + board.getSize() + "): ");
			int row = readIntegerWithMinMax(1, board.getSize()) - 1;

			// Spalte einlesen
			System.out.print("Spalte (1-" + board.getSize() + "): ");
			int column = readIntegerWithMinMax(1, board.getSize()) - 1;

			// Ausrichtung einlesen
			System.out.print("Orientierung (H/V): ");
			Orientation orientation = input.next().toUpperCase().charAt(0) == 'V' ? Orientation.Vertical : Orientation.Horizontal;

			isItPossibleToPlaceShip = false;
			try {
				player.placeShip(ship, column, row, orientation);
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

	}

	private static void gameLoop(Game game) {
		Player currentPlayer;
		do {
			currentPlayer = game.getCurrentPlayer();

			// Spieler-Status ausgeben
			printPlayerStates(game.getPlayers());

			// mˆgliche Spieleraktionen auflisten
			System.out.println();
			System.out.println(currentPlayer + " ist an der Reihe.");
			System.out.println();
			System.out.println("Was mˆchtest du tun?");
			System.out.println("(1) Gegner angreifen");
			System.out.println("(2) Spiel speichern");
			System.out.println("(3) Spiel beenden");

			// Wahl einlesen
			int choice = readIntegerWithMinMax(1, 3);
			switch (choice) {
			case 1:
				attackPlayer(currentPlayer, game);
				break;
			case 2:
				saveGame(game);
				break;
			case 3:
				System.exit(0);
			}
			game.nextPlayer();
		} while (!game.isGameover());
	}

	private static void attackPlayer(Player currentPlayer, Game game) {
		Ship ship;
		Player enemy;

		// Auswahl des zu schieﬂenden Schiffs
		System.out.println("Welches Schiff soll schieﬂen?");
		ship = selectShipFromPlayer(currentPlayer);

		// Auswahl des Gegners, auf den geschossen werden soll
		System.out.println("Auf welchen Spieler?");
		enemy = selectEnemy(game.getEnemiesOfCurrentPlayer());
		printBoard(enemy.getBoard(), false);

		// Zug mit ausgew‰hltem Schiff und Gegner ausf¸hren
		makeTurn(currentPlayer, ship, enemy);

		// Boards ausgeben
		printBoards(currentPlayer.getBoard(), enemy.getBoard());
	}

	private static void saveGame(Game game) {
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

	private static Ship selectShipFromPlayer(Player player) {
		ArrayList<Ship> availableShips = player.getAvailableShips();
		Ship selectedShip;
		boolean isShipSelected = false;
		do {
			// Eingabe wiederholen bis Schiff gew‰hlt wurde, das schieﬂen kann
			for (Ship s : availableShips) {
				System.out.println("(" + availableShips.indexOf(s) + ") " + s.getType() + "(reload:" + s.getCurrentReloadTime() + "," + " health:" + s.getSize() + ")");
			}
			selectedShip = availableShips.get(readIntegerWithMinMax(0, availableShips.size() - 1));
			isShipSelected = player.selectShip(selectedShip);
			if (!isShipSelected)
				System.out.println("Schiff l‰dt nach");
		} while (!isShipSelected);
		return selectedShip;
	}

	private static Player selectEnemy(ArrayList<Player> enemies) {
		// angreifbare Gegner anzeigen
		for (int i = 0; i < enemies.size(); i++) {
			System.out.println("(" + i + ")" + enemies.get(i));
		}
		return enemies.get(readIntegerWithMinMax(0, enemies.size() - 1));
	}

	private static void makeTurn(Player player, Ship ship, Player enemy) {
		boolean hasTurnBeenMade = false;
		do {
			// Koordinaten einlesen, bis Schuss erfolgreich ausgef¸hrt werden
			// kann
			System.out.print("Zeile (1-" + player.getBoard().getSize() + "): ");
			int row = readIntegerWithMinMax(1, player.getBoard().getSize()) - 1;
			System.out.print("Spalte (1-" + player.getBoard().getSize() + "): ");
			int column = readIntegerWithMinMax(1, player.getBoard().getSize()) - 1;
			System.out.print("Orientierung (H/V): ");
			Orientation orientation = input.next().toUpperCase().charAt(0) == 'V' ? Orientation.Vertical : Orientation.Horizontal;

			try {
				hasTurnBeenMade = player.makeTurn(enemy, column, row, orientation);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!hasTurnBeenMade) {
				System.out.println("Feld wurde bereits beschossen");
			}
		} while (!hasTurnBeenMade);
	}

	private static void printBoards(Board ownBoard, Board enemyBoard) {
		System.out.println();
		System.out.println("Eigenes Board");
		System.out.println();
		printBoard(ownBoard, true);
		System.out.println();
		System.out.println("Board des Gegners");
		System.out.println();
		printBoard(enemyBoard, false);
		System.out.println("O = getroffenes Schiff\nX = daneben\n+ = eigenes Schiff\n- = leer \nU = unbekannt\n! = zerstˆrtes Schiff\n");
	}

	private static void printBoard(Board board, boolean isOwnBoard) {
		Field[][] fields = board.getFields();
		for (int row = 0; row < fields.length; row++) {
			for (int column = 0; column < fields[row].length; column++) {
				Field field = fields[row][column];
				printState(field.getState(), isOwnBoard);
			}
			System.out.println();
		}
	}

	private static void printPlayerStates(Player[] players) {
		System.out.println();
		String s;
		for (Player player : players) {
			s = player.hasLost() ? "ist tot" : "lebt noch";
			System.out.println(player + " " + s);
		}
	}

	private static void printState(FieldState fieldState, boolean isOwnBoard) {
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

	private static int readInteger() {
		while (!input.hasNextInt()) {
			System.out.println("Eine Zahl eingeben!");
			input.next();
		}
		return input.nextInt();
	}

	private static int readIntegerWithMinMax(int min, int max) {
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
}

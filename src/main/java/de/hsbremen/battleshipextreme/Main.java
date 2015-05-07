package de.hsbremen.battleshipextreme;

import java.util.ArrayList;
import java.util.Scanner;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.model.Game;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.model.exception.FieldOccupiedException;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.exception.ShipAlreadyPlacedException;
import de.hsbremen.battleshipextreme.model.exception.ShipOutOfBoardException;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.model.ship.Ship;

public class Main {
	static Scanner input = new Scanner(System.in);

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
		System.out.println("(1) Erzeuge Spiel manuell");
		System.out.println("(2) Erzeuge Spiel automatisch");
		System.out.println("(3) Letztes Spiel fortsetzen");
		boolean couldGameBeCreated;
		do {
			couldGameBeCreated = true;
			int choice = readIntegerWithMinMax(1, 3);
			switch (choice) {
			case 1:
				game = new Game(generateSettings());
				game.setBeginningPlayer(1);
				placeShips(game);
				break;
			case 2:
				game = new Game(new Settings(3, 10, 2, 1, 1, 1));
				game.setBeginningPlayer(1);
				placeShipsWithoutInput(game);
				break;
			case 3:
				game = new Game();
				try {
					game.load("saveGame.sav");
				} catch (Exception e) {
					System.out.println("Spiel konnte nicht geladen werden");
					couldGameBeCreated = false;
				}
				break;
			}
		} while (!couldGameBeCreated);
		return game;
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

		return new Settings(players, boardSize, destroyers, frigates, corvettes, submarines);
	}

	private static void setPlayerNames(Player[] players) {
		System.out.println("\nSpielernamen:");
		for (Player player : players) {
			System.out.print("Name für " + player + " : ");
			player.setName(input.nextLine());
		}
	}

	private static void placeShips(Game game) {
		// Schiffe manuell setzen
		boolean isItPossibleToPlaceShip;
		Player player;
		do {
			player = game.getCurrentPlayer();
			System.out.println("\nPlatziere Schiffe fuer " + player + ":");
			for (Ship ship : player.getShips()) {
				do {
					// solange Schiffskoordinaten einlesen, bis keine Exception
					// auftritt
					System.out.println("\nPlatziere " + ship + ":");

					// Zeile einlesen
					System.out.print("Zeile (1-" + player.getBoard().getSize() + "): ");
					int row = readIntegerWithMinMax(1, player.getBoard().getSize()) - 1;

					// Spalte einlesen
					System.out.print("Spalte (1-" + player.getBoard().getSize() + "): ");
					int column = readIntegerWithMinMax(1, player.getBoard().getSize()) - 1;

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

				System.out.println();
				System.out.println("Board von " + player);
				printBoard(player.getBoard(), true);
			}
			game.nextPlayer();
		} while (!game.isReady());

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

	private static void gameLoop(Game game) {
		Ship ship;
		Player enemy;
		Player player;
		do {
			player = game.getCurrentPlayer();
			System.out.println(player + " ist an der Reihe.");

			// Auswahl des zu schießenden Schiffs
			System.out.println("Welches Schiff soll schießen?");
			ship = selectShip(player);

			// Auswahl des Gegners, auf den geschossen werden soll
			System.out.println("Auf welchen Spieler?");
			enemy = selectEnemy(game.getEnemiesOfCurrentPlayer());

			// Zug mit ausgewähltem Schiff und Gegner ausführen
			makeTurn(player, ship, enemy);

			printBoards(player.getBoard(), enemy.getBoard());
			game.nextPlayer();
			try {
				game.save("saveGame.sav");
			} catch (Exception e) {
				System.err.print("Game could not be saved");
				e.printStackTrace();
			}
		} while (!game.isGameover());
	}

	private static Ship selectShip(Player player) {
		Ship ship;
		Ship[] ships = player.getShips();
		boolean isShipSelected = false;
		do {
			// Eingabe wiederholen bis Schiff gewählt wurde, das schießen kann
			printShips(ships);
			ship = ships[readIntegerWithMinMax(0, ships.length - 1)];
			isShipSelected = player.selectShip(ship);
			if (!isShipSelected)
				System.out.println("Schiff lädt nach");
		} while (!isShipSelected);
		return ship;
	}

	private static void printShips(Ship[] ships) {
		// Schiffe des Spielers und deren Munition/Leben anzeigen
		for (int i = 0; i < ships.length; i++) {
			System.out.println("(" + i + ") " + ships[i].getType() + "(reload:" + ships[i].getCurrentReloadTime() + "," + " health:" + ships[i].getSize() + ")");
		}
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
			// Koordinaten einlesen, bis Schuss erfolgreich ausgeführt werden
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
		printBoard(ownBoard, true);
		printBoard(enemyBoard, false);
		System.out.println("O = getroffenes Schiff\nX = daneben\n+ = eigenes Schiff\n- = leer \nU = unbekannt\n! = zerstörtes Schiff\n");
	}

	private static void printBoard(Board board, boolean isOwnBoard) {
		String s = isOwnBoard ? "\nEigenes Board" : "\nGegnerisches Board";
		System.out.println(s);
		Field[][] fields = board.getFields();
		for (int row = 0; row < fields.length; row++) {
			for (int column = 0; column < fields[row].length; column++) {
				Field field = fields[row][column];
				printState(field.getState(), isOwnBoard);
			}
			System.out.println();
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

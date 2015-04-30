package de.hsbremen.battleshipextreme;

import java.util.Scanner;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.Field;
import de.hsbremen.battleshipextreme.model.Game;
import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.model.ship.Ship;

public class Main {
	static Scanner input = new Scanner(System.in);

	public static void main(String[] args) throws Exception {
		Game game = createGame();
		if (game.isReady()) {
			do {
				shootLoop(game, game.getPlayers());
			} while (!game.isGameover());
			System.out.println("Spiel zu Ende");
			System.out.println(game.determineWinner() + " hat gewonnen!");
		}
		input.close();
	}

	private static Game createGame() throws Exception {
		Game game = null;
		System.out.println("(1) Erzeuge Spiel manuell");
		System.out.println("(2) Erzeuge Spiel automatisch");
		int choice = input.nextInt();
		switch (choice) {
		case 1:
			game = new Game(generateSettings());
			placeShips(game.getPlayers());
			break;
		case 2:
			game = new Game(new Settings(3, 10, 2, 1, 1, 1));
			placeShipsWithoutInput(game.getPlayers());
			break;
		}
		game.setBeginningPlayer(1);
		return game;
	}

	private static Settings generateSettings() {
		System.out.println("Einstellungen:");
		System.out.print("Anzahl der Spieler (2-6): ");
		int players = input.nextInt();
		System.out.print("Groesse des Spielfeldes: ");
		int boardSize = input.nextInt();
		System.out.print("Zerstoerer: ");
		int destroyers = input.nextInt();
		System.out.print("Fregatten: ");
		int frigates = input.nextInt();
		System.out.print("Korvetten: ");
		int corvettes = input.nextInt();
		System.out.print("U-Boote: ");
		int submarines = input.nextInt();

		return new Settings(players, boardSize, destroyers, frigates, corvettes, submarines);
	}

	private static void setPlayerNames(Player[] players) {
		System.out.println("\nSpielernamen:");

		for (Player player : players) {
			System.out.print("Name für " + player + " : ");
			player.setName(input.nextLine());
		}
	}

	private static void placeShips(Player[] players) throws Exception {
		for (Player player : players) {
			System.out.println("\nPlatziere Schiffe fuer " + player + ":");

			for (Ship ship : player.getShips()) {
				System.out.println("\nPlatziere " + ship + ":");

				System.out.print("Zeile (1-" + player.getBoard().getSize() + "): ");
				int row = input.nextInt() - 1;
				System.out.print("Spalte (1-" + player.getBoard().getSize() + "): ");
				int column = input.nextInt() - 1;

				System.out.print("Orientierung (H/V): ");
				Orientation orientation = input.next().toUpperCase().charAt(0) == 'V' ? Orientation.Vertical : Orientation.Horizontal;

				player.placeShip(ship, column, row, orientation);

				System.out.println();
				System.out.println("Board von " + player);
				printOwnBoard(player.getBoard());
			}
		}
	}

	private static void placeShipsWithoutInput(Player[] players) throws Exception {
		for (Player player : players) {
			for (int i = 0; i < player.getShips().length; i++) {
				Ship ship = player.getShips()[i];
				int row = 2 * i;
				int column = 0;
				Orientation orientation = Orientation.Horizontal;
				player.placeShip(ship, column, row, orientation);
			}
			System.out.println();
			System.out.println("Board von " + player);
			printOwnBoard(player.getBoard());
		}
	}

	private static void shootLoop(Game game, Player[] players) throws Exception {
		Ship ship;
		Player enemy;
		for (Player player : players) {
			if (!game.isGameover()) {
				if (!player.hasLost()) {
					if (!player.AreAllShipsReloading()) {
						ship = selectOwnShip(player);
						enemy = selectEnemy(player, players);
						fireShot(player, ship, enemy);
						printBoards(player.getBoard(), enemy.getBoard());
					} else {
						System.out.println(player + " kann nicht schießen, da alle Schiffe nachladen");
					}
				} else {
					System.out.println(player + " ist tot.");
				}
				player.decreaseCurrentReloadTimeOfAllShips();
			}
		}
	}

	private static Ship selectOwnShip(Player player) {
		Ship ship;
		System.out.println(player + " ist an der Reihe");
		System.out.println("Welches Schiff soll schießen?");
		Ship[] ships = player.getShips();
		do {
			for (int i = 0; i < ships.length; i++) {
				System.out.println("(" + i + ")" + ships[i].getType() + "(reload:" + ships[i].getCurrentReloadTime() + "," + " health:" + ships[i].getSize() + ")");
			}
			int shipIndex = input.nextInt();
			ship = ships[shipIndex];
			if (ship.isReloading()) {
				System.out.println("Schiff lädt gerade nach");
			}
			if (ship.isDestroyed()) {
				System.out.println("Schiff ist kaputt");
			}
			System.out.println();
		} while (ship.isReloading() || (ship.isDestroyed()));
		return ship;
	}

	private static Player selectEnemy(Player player, Player[] players) {
		System.out.println("Auf welchen Spieler?");
		for (int i = 0; i < players.length; i++) {
			if (!players[i].hasLost()) {
				if (!player.equals(players[i])) {
					System.out.println("(" + i + ")" + players[i].getName());
				}
			}
		}
		int playerIndex = input.nextInt();
		return players[playerIndex];
	}

	private static void fireShot(Player player, Ship ship, Player enemy) throws Exception {
		boolean wasShotFired = false;
		do {
			System.out.print("Zeile (1-" + player.getBoard().getSize() + "): ");
			int row = input.nextInt() - 1;
			System.out.print("Spalte (1-" + player.getBoard().getSize() + "): ");
			int column = input.nextInt() - 1;

			System.out.print("Orientierung (H/V): ");
			Orientation orientation = input.next().toUpperCase().charAt(0) == 'V' ? Orientation.Vertical : Orientation.Horizontal;
			wasShotFired = player.shoot(ship, enemy, column, row, orientation);
			if (!wasShotFired)
				System.out.println("\nFeld wurde bereits beschossen\n");
		} while (!wasShotFired);
	}

	private static void printBoards(Board ownBoard, Board enemyBoard) {
		printOwnBoard(ownBoard);
		printEnemyBoard(enemyBoard);
		System.out.println("O = getroffenes Schiff\nX = daneben\n+ = eigenes Schiff\n- = leer \nU = unbekannt\n! = zerstörtes Schiff\n");
	}

	private static void printOwnBoard(Board board) {
		System.out.println("\nEigenes Board");
		Field[][] fields = board.getFields();
		for (int row = 0; row < fields.length; row++) {
			for (int column = 0; column < fields[row].length; column++) {
				Field field = fields[row][column];
				if (field.isHit()) {
					if (field.hasShip()) {
						if (field.getShip().isDestroyed()) {
							System.out.print("!"); // zerstörtes Schiff
						} else {
							System.out.print("O"); // getroffenes Schiff
						}
					} else {
						System.out.print("X"); // daneben
					}
				} else {
					if (field.hasShip()) {
						System.out.print("+"); // hat Schiff
					} else {
						System.out.print("-"); // leer
					}
				}
			}
			System.out.println();
		}

	}

	private static void printEnemyBoard(Board board) {
		System.out.println("\nBeschossenes Board");
		Field[][] fields = board.getFields();
		for (int row = 0; row < fields.length; row++) {
			for (int column = 0; column < fields[row].length; column++) {
				Field field = fields[row][column];
				if (field.isHit()) {
					if (field.hasShip()) {
						if (field.getShip().isDestroyed()) {
							System.out.print("!"); // zerstörtes Schiff
						} else {
							System.out.print("O"); // getroffenes Schiff
						}
					} else {
						System.out.print("X"); // daneben
					}
				} else {
					System.out.print("?"); // unbekannt
				}
			}
			System.out.println();
		}
	}

}

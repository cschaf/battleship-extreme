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
	public static void main(String[] args) throws Exception {

		// Game game = new Game(generateSettings());
		// placeShips(game.getPlayers());
		// game.setBeginningPlayerRandomly();

		Game game = new Game(new Settings(3, 10, 0, 0, 0, 1));
		Player[] players = game.getPlayers();
		placeShipsWithoutInput(players);
		game.setBeginningPlayer(1);
		if (game.isReady()) {
			do {
				shootAtShips(game, players);
			} while (!game.isGameover());
			System.out.println("Spiel zu Ende");
		}
	}

	private static Settings generateSettings() {
		Scanner input = new Scanner(System.in);

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

		return new Settings(players, boardSize, destroyers, frigates,
				corvettes, submarines);
	}

	private static void setPlayerNames(Player[] players) {
		Scanner input = new Scanner(System.in);

		System.out.println("\nSpielernamen:");

		for (Player player : players) {
			System.out.print("Name für " + player + " : ");
			player.setName(input.nextLine());
		}
	}

	private static void placeShips(Player[] players) throws Exception {
		Scanner input = new Scanner(System.in);

		for (Player player : players) {
			System.out.println("\nPlatziere Schiffe fuer " + player + ":");

			for (Ship ship : player.getShips()) {
				System.out.println("\nPlatziere " + ship + ":");

				System.out.print("Zeile (1-" + player.getBoard().getSize()
						+ "): ");
				int row = input.nextInt() - 1;
				System.out.print("Spalte (1-" + player.getBoard().getSize()
						+ "): ");
				int column = input.nextInt() - 1;

				System.out.print("Orientierung (H/V): ");
				Orientation orientation = input.next().toUpperCase().charAt(0) == 'V' ? Orientation.Vertical
						: Orientation.Horizontal;

				player.placeShip(ship, column, row, orientation);

				System.out.println();
				System.out.println("Board von " + player);
				printOwnBoard(player.getBoard());
			}
		}
	}

	private static void placeShipsWithoutInput(Player[] players)
			throws Exception {
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

	private static void shootAtShips(Game game, Player[] players)
			throws Exception {
		Scanner input = new Scanner(System.in);
		Ship ship;
		for (Player player : players) {
			if (!game.isGameover()) {
				if (!player.hasLost()) {
					if (!player.AreAllShipsReloading()) {
						System.out.println(player + " ist an der Reihe");
						System.out.println("Welches Schiff soll schießen?");
						Ship[] ships = player.getShips();
						do {
							for (int i = 0; i < ships.length; i++) {
								System.out.println("(" + i + ")" + "(reload:"
										+ ships[i].getCurrentReloadTime() + ")"
										+ "(health:" + ships[i].getSize() + ")"
										+ ships[i].getType());
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

						System.out.println("Auf welchen Spieler?");
						for (int i = 0; i < players.length; i++) {
							if (!players[i].hasLost()) {
								if (!player.equals(players[i])) {
									System.out.println("(" + i + ")"
											+ players[i].getName());
								}
							}
						}
						int playerIndex = input.nextInt();
						Player playerShotAt = players[playerIndex];

						boolean wasShotFired = false;
						do {
							System.out.print("Zeile (1-"
									+ player.getBoard().getSize() + "): ");
							int row = input.nextInt() - 1;
							System.out.print("Spalte (1-"
									+ player.getBoard().getSize() + "): ");
							int column = input.nextInt() - 1;

							System.out.print("Orientierung (H/V): ");
							Orientation orientation = input.next()
									.toUpperCase().charAt(0) == 'V' ? Orientation.Vertical
									: Orientation.Horizontal;
							System.out.println(orientation);
							wasShotFired = player.shoot(ship, playerShotAt,
									column, row, orientation);
							if (!wasShotFired)
								System.out.println("\nSchuss nicht im Feld\n");
						} while (!wasShotFired);

						printBoards(player.getBoard(), playerShotAt.getBoard());
					} else {
						System.out
								.println(player
										+ " kann nicht schießen, da alle Schiffe nachladen");
					}
				} else {
					System.out.println(player + " ist tot.");
				}
				player.decreaseCurrentReloadTimeOfAllShips();
			}
		}

	}

	private static void printBoards(Board ownBoard, Board enemyBoard) {
		printOwnBoard(ownBoard);
		printEnemyBoard(enemyBoard);
		System.out
				.println("O = getroffenes Schiff\nX = daneben\n+ = eigenes Schiff\n- = leer \nU = unbekannt\n! = zerstörtes Schiff\n");
	}

	private static void printOwnBoard(Board board) {
		System.out.println("Eigenes Board");
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
		System.out.println("Gegnerisches Board");
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
					System.out.print("U"); // unbekannt
				}
			}
			System.out.println();
		}
	}

}

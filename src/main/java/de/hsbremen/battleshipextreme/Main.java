package de.hsbremen.battleshipextreme;

import java.util.Scanner;

import de.hsbremen.battleshipextreme.model.*;
import de.hsbremen.battleshipextreme.model.exception.FieldOutOfBoardException;
import de.hsbremen.battleshipextreme.model.exception.ShipAlreadyPlacedException;
import de.hsbremen.battleshipextreme.model.player.*;
import de.hsbremen.battleshipextreme.model.ship.*;

public class Main {
	
	public static void main(String[] args) throws Exception {

//		Game game = new Game(generateSettings());
//		placeShips(game.getPlayers());
//		game.setBeginningPlayerRandomly();
		
		Game game = new Game(new Settings(2, 10, 1, 1, 1, 1));
		Player[] players = game.getPlayers();
		placeShipsWithoutInput(players);		
		game.setBeginningPlayer(1);		
		if (game.isReady()) {	
			shootAtShips(players);
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
		
		return new Settings(players, boardSize, destroyers, frigates, corvettes, submarines);
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
				int row = 2*i;
				int column = 0;
				Orientation orientation = Orientation.Horizontal;				
				player.placeShip(ship, column, row, orientation);		
			}
			System.out.println();
			System.out.println("Board von " + player);
			printOwnBoard(player.getBoard());
		}
	}
	
	
	private static void shootAtShips(Player[] players) throws Exception {
		Scanner input = new Scanner(System.in);		
		for (Player player : players) {	
			
			if (!player.AreAllShipsReloading()) {
				System.out.println(player + " ist an der Reihe");
				System.out.println("Welches Schiff soll schießen?");
				Ship[] ships = player.getShips();				
				for (int i = 0; i < ships.length; i++) {
					System.out.println("(" + i + ")" + ships[i].getType() );
				}
				
				int shipIndex = input.nextInt();
				Ship ship = ships[shipIndex];
				
				System.out.println();
				System.out.println("Auf welchen Spieler?");				
				for (int i = 0; i < players.length; i++) {
					System.out.println("(" + i +")" + players[i].getName());
				}				
				int playerIndex = input.nextInt();
				Player playerShotAt = players[playerIndex];
								
				boolean wasShotFired = false;
				do {	
					System.out.print("Zeile (1-" + player.getBoard().getSize() + "): ");
					int row = input.nextInt() - 1;
					System.out.print("Spalte (1-" + player.getBoard().getSize() + "): ");
					int column = input.nextInt() - 1;
	
					System.out.print("Orientierung (H/V): ");
					Orientation orientation = input.next().toUpperCase().charAt(0) == 'V' ? Orientation.Vertical : Orientation.Horizontal;
					wasShotFired = player.shoot(ship, playerShotAt, column, row, orientation);
					if (!wasShotFired) System.out.println("\nSchuss nicht im Feld\n");
				} while (!wasShotFired);				
				printBoards(player.getBoard(), playerShotAt.getBoard());
			} else {
				System.out.println(player + " kann nicht schießen, da alle Schiffe nachladen");
			}
		}
	}
	
	private static void printBoards(Board ownBoard, Board enemyBoard) {
		printOwnBoard(ownBoard);
		printEnemyBoard(enemyBoard);
		System.out.println("O = getroffenes Schiff\nX = daneben\n+ = eigenes Schiff\n- = leer \n? = unbekannt\n");
	}
	
	private static void printOwnBoard(Board board) {
		System.out.println("Eigenes Board");
		Field[][] fields = board.getFields();
		for (int row = 0; row < fields.length; row++) {
			for (int column = 0; column < fields[row].length; column++) {
				Field field = fields[row][column];
				if (field.isHit()) {
					if (field.hasShip()) {
						System.out.print("O"); //Treffer
					} else {
						System.out.print("X"); //daneben
					}					
				} else {
					if (field.hasShip()) {
						System.out.print("+"); //hat Schiff
					} else {
						System.out.print("-"); //leer
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
						System.out.print("O"); //Treffer
					} else {
						System.out.print("X"); //daneben
					}					
				} else {
						System.out.print("?"); //unbekannt
				}
			}
			System.out.println();
		}
	}
	

	
}

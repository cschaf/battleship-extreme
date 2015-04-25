package de.hsbremen.battleshipextreme;

import java.util.Scanner;

import de.hsbremen.battleshipextreme.model.*;
import de.hsbremen.battleshipextreme.model.player.*;
import de.hsbremen.battleshipextreme.model.ship.*;

public class Main {
	
	public static void main(String[] args) throws Exception {

		Game game = new Game(generateSettings());
		//setPlayerNames(game.getPlayers());
		placeShips(game.getPlayers());
		game.setBeginningPlayerRandomly();
		
		if (game.isReady()) {
			
			System.out.println(game.getCurrentPlayer());
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
				printBoard(player.getBoard());
			}
		}
	}
	
	private static void printBoard(Board board) {
		Field[][] fields = board.getFields();
		for (int row = 0; row < fields.length; row++) {
			for (int column = 0; column < fields[row].length; column++) {
				System.out.print(fields[row][column].getShip() != null ? "X" : "-");
			}
			System.out.println();
		}
	}
}

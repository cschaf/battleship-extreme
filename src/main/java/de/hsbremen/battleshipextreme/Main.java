package de.hsbremen.battleshipextreme;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import de.hsbremen.battleshipextreme.model.*;
import de.hsbremen.battleshipextreme.model.player.*;
import de.hsbremen.battleshipextreme.model.ship.*;

public class Main {
	public static void main(String[] args) throws Exception {
		// Eingabeobjekt erzeugen
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		
		Game game = new Game(generateSettings());
		setPlayerNames(game.getPlayers());
		placeShips(game.getPlayers());
		
		if (game.isReady()) {
			
		}
	}
	
	private static Settings generateSettings() throws Exception {
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Einstellungen:");
		System.out.print("Anzahl der Spieler (2-6): ");
		int players = Integer.parseInt(input.readLine());
		System.out.print("Groesse des Spielfeldes: ");
		int boardSize = Integer.parseInt(input.readLine());
		System.out.print("Zerstoerer: ");
		int destroyers = Integer.parseInt(input.readLine());
		System.out.print("Fregatten: ");
		int frigates = Integer.parseInt(input.readLine());
		System.out.print("Korvetten: ");
		int corvettes = Integer.parseInt(input.readLine());
		System.out.print("U-Boote: ");
		int submarines = Integer.parseInt(input.readLine());
		
		return new Settings(players, boardSize, destroyers, frigates, corvettes, submarines);
	}
	
	private static void setPlayerNames(Player[] players) throws Exception {
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("\nSpielernamen:");

		for (Player player : players) {
			System.out.print("Name für " + player + " : ");
			player.setName(input.readLine());
		}
	}
	
	private static void placeShips(Player[] players) throws Exception {
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		
		for (Player player : players) {
			System.out.println("\nPlatziere Schiffe fuer " + player + ":");
			
			for (Ship ship : player.getShips()) {
				System.out.println("\nPlatziere " + ship + ":");

				System.out.print("Zeile (1-" + player.getBoard().getSize() + "): ");
				int row = Integer.parseInt(input.readLine()) - 1;
				System.out.print("Spalte (1-" + player.getBoard().getSize() + "): ");
				int column = Integer.parseInt(input.readLine()) - 1;
				
				Field field = player.getBoard().getField(column, row);
				
				System.out.print("Orientierung (H/V): ");
				Orientation orientation = input.readLine().toUpperCase().charAt(0) == 'V' ? Orientation.Vertical : Orientation.Horizontal;
				
				player.placeShip(ship, orientation, field);
				
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

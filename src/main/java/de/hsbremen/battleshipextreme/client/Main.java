package de.hsbremen.battleshipextreme.client;

import de.hsbremen.battleshipextreme.model.Game;
import de.hsbremen.battleshipextreme.model.Settings;

public class Main {
	
	public static void main(String[] args) {

		Game game = new Game(new Settings());
		GUI gui = new GUI();
		Controller controller = new Controller(game, gui);
		
	}
}

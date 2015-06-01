package de.hsbremen.battleshipextreme.client;

import de.hsbremen.battleshipextreme.model.Game;
import de.hsbremen.battleshipextreme.model.network.NetworkClient;

public class Main {

	public static void main(String[] args) {
		Game game = new Game();
		GUI gui = new GUI();
		String ip = gui.getPanelServerConnection().getPnlServerConnectionBar().getTbxIp().getText();
		//String port = gui.getPanelServerConnection().getPnlServerConnectionBar().getTbxPort().getText();
		String username = gui.getPanelServerConnection().getPnlServerConnectionBar().getTbxUsername().getText();
		NetworkClient network = new NetworkClient(ip, 1337, username);
		Controller controller = new Controller(game, network, gui);

	}
}

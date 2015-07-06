package de.hsbremen.battleshipextreme.server;

import de.hsbremen.battleshipextreme.server.view.Gui;
import de.hsbremen.battleshipextreme.server.view.ServerController;

/**
 * Created on 14.05.2015.
 */
public class Main {
// --------------------------- main() method ---------------------------

    public static void main(String[] args) {
        Gui gui = new Gui();
        Server server = new Server(1337);
        ServerController controller = new ServerController(gui, server);
    }
}

package de.hsbremen.battleshipextreme.server.listener;

import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.transfarableObject.Game;
import de.hsbremen.battleshipextreme.network.transfarableObject.Turn;

/**
 * Created by cschaf on 15.05.2015.
 */
public class ClientObjectReceivedListener implements IClientObjectReceivedListener {
    @Override
    public void onObjectReceived(EventArgs<ITransferable> eventArgs) {
        ITransferable receivedObject = eventArgs.getItem();
        switch (receivedObject.getType()) {
            case Message:
                System.out.println(receivedObject);
                break;
            case ClientMessage:
                System.out.println(receivedObject);
                break;
            case Game:
                Game game = (Game) receivedObject;
                System.out.println("New game settings has been received for game " + game.getName());
                break;
            case Turn:
                Turn turn = (Turn) receivedObject;
                System.out.println("New turn has been received for game " + turn.getGameId());
                break;
            case Join:
                break;
        }
    }
}

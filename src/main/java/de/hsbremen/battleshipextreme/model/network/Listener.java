package de.hsbremen.battleshipextreme.model.network;



import de.hsbremen.battleshipextreme.network.IDisposable;
import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.TransferableObjectFactory;
import de.hsbremen.battleshipextreme.network.eventhandling.ErrorHandler;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.transfarableObject.*;

import javax.swing.event.EventListenerList;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by cschaf on 15.05.2015.
 */
public class Listener extends Thread implements IDisposable {
    private final ErrorHandler errorHandler;
    protected EventListenerList listeners = new EventListenerList();
    private ObjectInputStream in = null;
    private boolean disposed;
    public Listener(ObjectInputStream in, ErrorHandler errorHandler){
        this.errorHandler = errorHandler;
        this.disposed = false;
        this.in = in;
        this.setName("Client-Listenerthread");
    }

    public void run(){
        try {
            ITransferable receivedObj;
            while (!this.disposed && (receivedObj = (ITransferable) in.readObject()) != null) {
                objectReceived(new EventArgs<ITransferable>(this, receivedObj));
                switch (receivedObj.getType()){
                    case Message:
                        Message message = (Message)receivedObj;
                        messageObjectReceived(new EventArgs<Message>(this, message));
                        break;
                    case ClientMessage:
                        ClientMessage clientMessage = (ClientMessage)receivedObj;
                        messageObjectReceived(new EventArgs<Message>(this, clientMessage));
                        break;
                    case ClientInfo:
                        ClientInfo clientInfo = (ClientInfo) receivedObj;
                        clientInfoObjectReceived(new EventArgs<ClientInfo>(this, clientInfo));
                        break;
                    case Game:
                        NetGame netGame = (NetGame)receivedObj;
                        gameObjectReceived(new EventArgs<NetGame>(this, netGame));
                        break;

                    case GameList:
                        GameList gameList = (GameList)receivedObj;
                        gameListObjectReceived(new EventArgs<GameList>(this, gameList));
                        break;
                    case Turn:
                        Turn turn = (Turn)receivedObj;
                        turnObjectReceived(new EventArgs<Turn>(this, turn));
                        break;
                    case ServerInfo:
                        ServerInfo serverInfo = (ServerInfo)receivedObj;
                        switch (serverInfo.getReason()){
                            case GameClosed:
                                errorHandler.errorHasOccurred(new EventArgs<ITransferable>(this, TransferableObjectFactory.CreateMessage("Your game has been closed!")));
                                break;
                        }
                        break;
                }
            }
        } catch (IOException e) {
            errorHandler.errorHasOccurred(new EventArgs<ITransferable>(this, TransferableObjectFactory.CreateMessage("Connection to server has been broken.")));
        } catch (ClassNotFoundException e) {
            errorHandler.errorHasOccurred(new EventArgs<ITransferable>(this, TransferableObjectFactory.CreateMessage(e.getMessage())));
        }
    }



    public void addServerObjectReceivedListener(IServerObjectReceivedListener listener) {
        this.listeners.add(IServerObjectReceivedListener.class, listener);
    }

    public void removeServerObjectReceivedListener(IServerObjectReceivedListener listener) {
        this.listeners.remove(IServerObjectReceivedListener.class, listener);
    }

    private void objectReceived(EventArgs<ITransferable> eventArgs) {
        Object[] listeners = this.listeners.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == IServerObjectReceivedListener.class) {
                ((IServerObjectReceivedListener) listeners[i+1]).onObjectReceived(eventArgs);
            }
        }
    }
    private void messageObjectReceived(EventArgs<Message> eventArgs) {
        Object[] listeners = this.listeners.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == IServerObjectReceivedListener.class) {
                ((IServerObjectReceivedListener) listeners[i+1]).onMessageObjectReceived(eventArgs);
            }
        }
    }

    private void gameObjectReceived(EventArgs<NetGame> eventArgs) {
        Object[] listeners = this.listeners.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == IServerObjectReceivedListener.class) {
                ((IServerObjectReceivedListener) listeners[i+1]).onGameObjectReceived(eventArgs);
            }
        }
    }

    private void gameListObjectReceived(EventArgs<GameList> eventArgs) {
        Object[] listeners = this.listeners.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == IServerObjectReceivedListener.class) {
                ((IServerObjectReceivedListener) listeners[i+1]).onGameListObjectReceived(eventArgs);
            }
        }
    }

    private void turnObjectReceived(EventArgs<Turn> eventArgs) {
        Object[] listeners = this.listeners.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == IServerObjectReceivedListener.class) {
                ((IServerObjectReceivedListener) listeners[i+1]).onTurnObjectReceived(eventArgs);
            }
        }
    }

    private void clientInfoObjectReceived(EventArgs<ClientInfo> eventArgs) {
        Object[] listeners = this.listeners.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == IServerObjectReceivedListener.class) {
                ((IServerObjectReceivedListener) listeners[i+1]).onClientInfoObjectReceived(eventArgs);
            }
        }
    }

    public void dispose() {
        this.disposed = true;
    }
}

package de.hsbremen.battleshipextreme.server.view;

import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.eventhandling.listener.IErrorListener;
import de.hsbremen.battleshipextreme.network.transfarableObject.ClientInfo;
import de.hsbremen.battleshipextreme.network.transfarableObject.Game;
import de.hsbremen.battleshipextreme.network.transfarableObject.Turn;
import de.hsbremen.battleshipextreme.server.ClientHandler;
import de.hsbremen.battleshipextreme.server.ClientJListItem;
import de.hsbremen.battleshipextreme.server.Server;
import de.hsbremen.battleshipextreme.server.TestClient.ErrorListener;
import de.hsbremen.battleshipextreme.server.listener.IClientConnectionListener;
import de.hsbremen.battleshipextreme.server.listener.IClientObjectReceivedListener;
import de.hsbremen.battleshipextreme.server.listener.IServerListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Created by cschaf on 28.05.2015.
 */
public class ServerController {
    private Gui gui;
    private Server server;
    private DefaultListModel<ClientJListItem> userModel;


    public ServerController(Gui gui, Server server) {
        this.gui = gui;
        this.server = server;
        this.userModel = new DefaultListModel<ClientJListItem>();
        this.addGuiEvents();
        this.addServerEvents();
    }

    private void addServerEvents() {
        server.addErrorListener(new IErrorListener() {
            public void onError(EventArgs<ITransferable> eventArgs) {
                gui.getTraMessages().append(eventArgs.getItem().toString() + "\r\n");
            }
        });

        server.addServerListener(new IServerListener() {
            public void onInfo(EventArgs<ITransferable> eventArgs) {
                gui.getTraMessages().append(eventArgs.getItem().toString() + "\r\n");
            }
        });

        server.addClientConnectionListener(new IClientConnectionListener() {
            public void onClientHasConnected(EventArgs<ITransferable> eventArgs) {
                gui.getTraMessages().append(eventArgs.getItem() + "\r\n");
            }

            public void onClientHasDisconnected(EventArgs<ITransferable> eventArgs) {
                ClientInfo client = (ClientInfo)eventArgs.getItem();
                removeClientItemFromUserList(client);
                gui.getTraMessages().append(client.getUsername() + ":" + client.getPort() + " has left\r\n");
            }
        });

        server.addClientObjectReceivedListener(new IClientObjectReceivedListener() {
            public void onObjectReceived(EventArgs<ITransferable> eventArgs) {
                ITransferable receivedObject = eventArgs.getItem();
                switch (receivedObject.getType()) {
                    case ClientMessage:
                        gui.getTraMessages().append(eventArgs.getItem().toString() + "\r\n");
                        break;
                    case Game:
                        Game game = (Game) receivedObject;
                        break;
                    case Turn:
                        Turn turn = (Turn) receivedObject;
                        break;
                    case ClientInfo:
                        ClientInfo info = (ClientInfo) receivedObject;
                        ClientJListItem item = new ClientJListItem(info.getIp(), info.getPort(), info.getUsername());
                        switch (info.getReason()) {
                            case Connect:
                                userModel.addElement(item);
                                gui.getListUsers().setModel(userModel);

                                break;
                            case Disconnect:
                                removeClientItemFromUserList(info);
                                gui.getTraMessages().append(info.getUsername() + "(" + info.getPort() + ") has left \r\n");
                                break;
                        }
                        break;
                }
            }
        });
    }

    private void removeClientItemFromUserList(ClientInfo item) {
        for (int i =0; i < userModel.getSize(); i++){
            String ip = userModel.getElementAt(i).getIp();
            int port = userModel.getElementAt(i).getPort();
            if(ip.equals(item.getIp()) && port == item.getPort()){
                userModel.remove(i);
            }
        }
    }

    private void addGuiEvents() {
        this.gui.getPnlServerControlBarPanel().getBtnStart().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                server.start();
            }
        });

        this.gui.getPnlServerControlBarPanel().getBtnStop().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                server.stop();
            }
        });
        this.gui.addWindowListener(new WindowListener() {
            public void windowOpened(WindowEvent e) {

            }

            public void windowClosing(WindowEvent e) {
                server.dispose();
            }

            public void windowClosed(WindowEvent e) {

            }

            public void windowIconified(WindowEvent e) {

            }

            public void windowDeiconified(WindowEvent e) {

            }

            public void windowActivated(WindowEvent e) {

            }

            public void windowDeactivated(WindowEvent e) {

            }
        });
    }
}

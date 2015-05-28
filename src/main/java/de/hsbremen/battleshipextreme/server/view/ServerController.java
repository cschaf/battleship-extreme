package de.hsbremen.battleshipextreme.server.view;

import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.eventhandling.listener.IErrorListener;
import de.hsbremen.battleshipextreme.network.transfarableObject.ClientInfo;
import de.hsbremen.battleshipextreme.network.transfarableObject.Game;
import de.hsbremen.battleshipextreme.network.transfarableObject.Turn;
import de.hsbremen.battleshipextreme.server.ClientJListItem;
import de.hsbremen.battleshipextreme.server.Server;
import de.hsbremen.battleshipextreme.server.TestClient.ErrorListener;
import de.hsbremen.battleshipextreme.server.listener.IClientConnectionListener;
import de.hsbremen.battleshipextreme.server.listener.IClientObjectReceivedListener;
import de.hsbremen.battleshipextreme.server.listener.IServerListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by cschaf on 28.05.2015.
 */
public class ServerController{
  private Gui gui;
  private Server server;
  private DefaultListModel<ClientJListItem> userModel;



  public ServerController(Gui gui, Server server){
    this.gui = gui;
    this.server = server;
    this.userModel = new DefaultListModel<ClientJListItem>();
    this.addGuiEvents();
    this.addServerEvents();
  }

  private void addServerEvents(){
    server.addErrorListener(new IErrorListener(){
      public void onError(EventArgs<ITransferable> eventArgs){
        gui.getTraMessages().append(eventArgs.getItem().toString() + "\r\n");
      }
    });

    server.addServerListener(new IServerListener(){
      public void onInfo(EventArgs<ITransferable> eventArgs){
        gui.getTraMessages().append(eventArgs.getItem().toString() + "\r\n");
      }
    });

    server.addClientConnectionListener(new IClientConnectionListener(){
      public void onClientHasConnected(EventArgs<ITransferable> eventArgs){
        ClientInfo info = (ClientInfo)eventArgs.getItem();
        ClientJListItem item = new ClientJListItem(info.getIp(), info.getPort(), info.getUsername());
        userModel.addElement(item);
        gui.getTraMessages().append(
            info.getUsername() + "(" + info.getPort() + ") has joined" + "\r\n");
        gui.getListUsers().setModel(userModel);
      }

      public void onClientHasDisconnected(EventArgs<ITransferable> eventArgs){

      }
    });

    server.addClientObjectReceivedListener(new IClientObjectReceivedListener(){
      public void onObjectReceived(EventArgs<ITransferable> eventArgs){
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
        }
      }
    });
  }

  private void addGuiEvents(){
    this.gui.getPnlServerControlBarPanel().getBtnStart().addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        server.start();
      }
    });

    this.gui.getPnlServerControlBarPanel().getBtnStop().addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        server.stop();
      }
    });
  }
}

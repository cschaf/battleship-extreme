package de.hsbremen.battleshipextreme.server.view;

import de.hsbremen.battleshipextreme.network.ITransferable;
import de.hsbremen.battleshipextreme.network.TransferableObjectFactory;
import de.hsbremen.battleshipextreme.network.eventhandling.EventArgs;
import de.hsbremen.battleshipextreme.network.eventhandling.listener.IErrorListener;
import de.hsbremen.battleshipextreme.network.transfarableObject.ClientInfo;
import de.hsbremen.battleshipextreme.network.transfarableObject.Game;
import de.hsbremen.battleshipextreme.network.transfarableObject.Join;
import de.hsbremen.battleshipextreme.network.transfarableObject.Turn;
import de.hsbremen.battleshipextreme.server.Server;
import de.hsbremen.battleshipextreme.server.listener.IClientConnectionListener;
import de.hsbremen.battleshipextreme.server.listener.IClientObjectReceivedListener;
import de.hsbremen.battleshipextreme.server.listener.IServerListener;

import javax.swing.*;
import java.awt.event.*;

/**
 * Created by cschaf on 28.05.2015.
 */
public class ServerController {
    private Gui gui;
    private Server server;

    public ServerController(Gui gui, Server server) {
        this.gui = gui;
        this.server = server;

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
                ClientInfo client = (ClientInfo) eventArgs.getItem();
                gui.removeUserFromUserList(client);
                gui.getTraMessages().append(client.getUsername() + ":" + client.getPort() + " has left\r\n");
                gui.getListGames().setListData(server.getGames());
                gui.getScrollPanelGames().revalidate();
                gui.getScrollPanelGames().repaint();
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
                        gui.addGameToGameList(game);
                        gui.getTraMessages().append("New Game was added, " + game.getName() + "(" + game.getId() + ")" + "\r\n");
                        break;
                    case Turn:
                        Turn turn = (Turn) receivedObject;
                        gui.getTraMessages().append("New Turn was added, " + turn.getFrom().getName() + " attacked " + turn.getTo().getName() + " in game " + turn.getGameId() + "\r\n");
                        break;
                    case Join:
                        Join join = (Join) receivedObject;
                        gui.getTraMessages().append(join.getClient() + " has joined game with id: " + join.getGameId() + "\r\n");
                        break;
                    case ClientInfo:
                        ClientInfo info = (ClientInfo) receivedObject;
                        ClientJListItem item = new ClientJListItem(info.getIp(), info.getPort(), info.getUsername());
                        switch (info.getReason()) {
                            case Connect:
                                gui.addUserToUserList(item);
                                break;
                            case Disconnect:
                                gui.removeUserFromUserList(info);
                                gui.getTraMessages().append(info.getUsername() + "(" + info.getPort() + ") has left \r\n");
                                break;
                        }
                        break;
                }
            }
        });
    }

    private void addGuiEvents() {
        this.gui.getPnlServerControlBarPanel().getBtnStart().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                server.start();
                gui.setControlsEnabledAfterStartStop(false);
            }
        });

        this.gui.getPnlServerControlBarPanel().getBtnStop().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                server.stop();
                gui.setControlsEnabledAfterStartStop(true);
                gui.getUserModel().removeAllElements();
                gui.getGameModel().removeAllElements();
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

        gui.getBtnSend().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                broadcastMessage();
            }
        });

        gui.getTbxMessage().addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {

            }

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    broadcastMessage();
                }
            }

            public void keyReleased(KeyEvent e) {

            }
        });

        gui.getListUsers().addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                JList l = (JList) e.getSource();
                ListModel m = l.getModel();
                int index = l.locationToIndex(e.getPoint());
                if (index > -1) {
                    ClientJListItem item = (ClientJListItem) m.getElementAt(index);
                    String name = "<p width=\"200\">" + "Name: " + item.getName() + "</p>";
                    String ip = "<p width=\"200\">" + "IP: " + item.getIp() + "</p>";
                    String port = "<p width=\"200\">" + "Port: " + item.getPort() + "</p>";
                    l.setToolTipText("<html>" + name + ip + port + "</html>");
                }
            }
        });

        gui.getListGames().addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                JList l = (JList) e.getSource();
                ListModel m = l.getModel();
                int index = l.locationToIndex(e.getPoint());
                if (index > -1) {
                    Game item = (Game) m.getElementAt(index);
                    String name = "<p width=\"300\">" + "Name: " + item.getName() + "</p>";
                    String id = "<p width=\"300\">" + "ID: " + item.getId() + "</p>";
                    String password = "<p width=\"300\">" + "Password: " + item.getPassword() + "</p>";
                    String players = "<p width=\"300\">" + "Players: " + item.getJoinedPlayers().size() + " / 6" + "</p>";
                    String createdAt = "<p width=\"300\">" + "Created at: " + item.getCreatedAt() + "</p>";
                    l.setToolTipText("<html>" + name + id + password + players + createdAt + "</html>");
                }
            }
        });


        gui.getListUsers().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (SwingUtilities.isRightMouseButton(me)    // if right mouse button clicked
                        && !gui.getListUsers().isSelectionEmpty()            // and list selection is not empty
                        && gui.getListUsers().locationToIndex(me.getPoint()) // and clicked point is
                        == gui.getListUsers().getSelectedIndex()) {       //   inside selected item bounds
                    gui.getUserPopupMenu().show(gui.getListUsers(), me.getX(), me.getY());
                }
            }
        });

        gui.getListGames().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (SwingUtilities.isRightMouseButton(me)    // if right mouse button clicked
                        && !gui.getListGames().isSelectionEmpty()            // and list selection is not empty
                        && gui.getListGames().locationToIndex(me.getPoint()) // and clicked point is
                        == gui.getListGames().getSelectedIndex()) {       //   inside selected item bounds
                    gui.getGamePopupMenu().show(gui.getListGames(), me.getX(), me.getY());
                }
            }
        });
    }

    private void broadcastMessage() {
        if (!this.server.isRunning()) {
            return;
        }
        String message = gui.getTbxMessage().getText();
        ITransferable msg = TransferableObjectFactory.CreateMessage("Server: " + message);
        server.broadcast(msg);
        gui.getTbxMessage().setText("");
        gui.getTraMessages().append("Server (broadcast): " + message + "\r\n");
    }
}

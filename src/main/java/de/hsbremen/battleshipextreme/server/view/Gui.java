package de.hsbremen.battleshipextreme.server.view;

import de.hsbremen.battleshipextreme.network.transfarableObject.ClientInfo;
import de.hsbremen.battleshipextreme.network.transfarableObject.NetGame;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by cschaf on 14.05.2015.
 */
public class Gui extends JFrame {
    private JPanel pnlMain;
    private ServerControlBar pnlServerControlBarPanel;
    private JTextField tbxMessage;
    private JButton btnSend;
    private JScrollPane scrollPanelUsers;
    private JList listUsers;
    private JScrollPane scrollPanelGames;
    private JList listGames;
    private JTextArea traMessages;
    private JScrollPane scrollPanelMessages;
    private GroupBox boxUsers;
    private GroupBox boxGames;
    private GroupBox boxMessages;
    private DefaultListModel<ClientJListItem> userModel;
    private DefaultListModel<NetGame> gameModel;
    private JPopupMenu userPopupMenu;
    private JPopupMenu gamePopupMenu;
    private JMenuItem gameCloseMenuItem;
    private JMenuItem gameDetailsMenuItem;
    private JMenuItem userKckMenuItem;
    private JMenuItem userBanMenuItem;

    private JMenuItem exitMenuItem;
    private JMenuItem refreshUserMenuItem;
    private JMenuItem refreshGamesMenuItem;
    private JMenuItem gameSendMessageMenuItem;


    public Gui() {
        super("Server Gui");
        this._initComponents();
        this._addComponents();
        this._createMenuBar();
        this.userModel = new DefaultListModel<ClientJListItem>();
        this.gameModel = new DefaultListModel<NetGame>();
        setContentPane(pnlMain);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension d = new Dimension(637, 500);
        setSize(d);
        setPreferredSize(d);
        pack();
        setVisible(true);
    }

    private void _addComponents() {
        // row 1
        pnlMain.add(pnlServerControlBarPanel, "spanx 3, growx, pushx, wrap");

// row 2
        pnlMain.add(boxMessages, "push, grow, spanx 2, spany 2");
        pnlMain.add(boxUsers, "wrap, pushy, growy");
        pnlMain.add(boxGames, "wrap");

// row 3
        pnlMain.add(tbxMessage, "spanx 3, pushx, growx, split");
        pnlMain.add(btnSend);
    }

    private void _initComponents() {
        pnlMain = new JPanel(new MigLayout());

        pnlServerControlBarPanel = new ServerControlBar();

        traMessages = new JTextArea(10, 10);
        traMessages.setEditable(false);
        scrollPanelMessages = new JScrollPane(traMessages);

        boxMessages = new GroupBox("Log");
        boxMessages.add(scrollPanelMessages);

        listUsers = new JList();
        listUsers.setFixedCellWidth(256);
        scrollPanelUsers = new JScrollPane(listUsers);

        boxUsers = new GroupBox("Connected Users");
        boxUsers.add(scrollPanelUsers);

        listGames = new JList();
        listGames.setFixedCellWidth(256);
        scrollPanelGames = new JScrollPane(listGames);

        boxGames = new GroupBox("Created Games");
        boxGames.add(scrollPanelGames);

        tbxMessage = new JTextField("");
        tbxMessage.setEnabled(false);
        btnSend = new JButton("Send");
        btnSend.setEnabled(false);

        this.userPopupMenu = new JPopupMenu();
        this.userKckMenuItem = new JMenuItem("Kick");
        this.userPopupMenu.add(this.userKckMenuItem);
        this.userBanMenuItem = new JMenuItem("Ban");
        this.userPopupMenu.add(userBanMenuItem);

        this.gamePopupMenu = new JPopupMenu();
        this.gameSendMessageMenuItem = new JMenuItem("Send Message");
        this.gameSendMessageMenuItem.setEnabled(false);
        this.gameDetailsMenuItem = new JMenuItem("Details");
        this.gameDetailsMenuItem.setEnabled(false);
        this.gameCloseMenuItem = new JMenuItem("Close");

        this.gamePopupMenu.add(gameSendMessageMenuItem);
        this.gamePopupMenu.add(gameDetailsMenuItem);
        this.gamePopupMenu.add(gameCloseMenuItem);
    }

    private void _createMenuBar() {

        JMenuBar menubar = new JMenuBar();
        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);

        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setMnemonic(KeyEvent.VK_E);
        exitMenuItem.setToolTipText("Exit application");
        file.add(exitMenuItem);
        menubar.add(file);

        JMenu user = new JMenu("Users");
        user.setMnemonic(KeyEvent.VK_U);
        refreshUserMenuItem = new JMenuItem("Refresh");
        refreshUserMenuItem.setToolTipText("Refresh userlist");
        user.add(refreshUserMenuItem);
        menubar.add(user);

        JMenu games = new JMenu("Games");
        user.setMnemonic(KeyEvent.VK_G);
        refreshGamesMenuItem = new JMenuItem("Refresh");
        refreshGamesMenuItem.setToolTipText("Refresh gamelist");
        games.add(refreshGamesMenuItem);
        menubar.add(games);

        setJMenuBar(menubar);
    }

    public JPanel getPnlMain() {
        return pnlMain;
    }

    public ServerControlBar getPnlServerControlBarPanel() {
        return pnlServerControlBarPanel;
    }

    public JTextField getTbxMessage() {
        return tbxMessage;
    }

    public JButton getBtnSend() {
        return btnSend;
    }

    public JList getListUsers() {
        return listUsers;
    }

    public JList getListGames() {
        return listGames;
    }

    public JTextArea getTraMessages() {
        return traMessages;
    }

    public void addUserToUserList(ClientJListItem item) {
        this.userModel.addElement(item);
        getListUsers().setModel(this.userModel);
    }

    public void removeUserFromUserList(ClientInfo item) {
        for (int i = 0; i < userModel.getSize(); i++) {
            String ip = userModel.getElementAt(i).getIp();
            int port = userModel.getElementAt(i).getPort();
            if (ip.equals(item.getIp()) && port == item.getPort()) {
                userModel.remove(i);
            }
        }
    }

    public void setControlsEnabledAfterStartStop(boolean state) {
        this.pnlServerControlBarPanel.setEnabledAfterStartStop(state);
        btnSend.setEnabled(!state);
        tbxMessage.setEnabled(!state);
    }

    public DefaultListModel<ClientJListItem> getUserModel() {
        return userModel;
    }

    public void addGameToGameList(NetGame item) {
        this.gameModel.addElement(item);
        getListGames().setModel(this.gameModel);
    }

    public void removeGameFromGameList(NetGame item) {
        for (int i = 0; i < gameModel.getSize(); i++) {
            String ip = gameModel.getElementAt(i).getId();
            if (ip.equals(item.getId())) {
                gameModel.remove(i);
                break;
            }
        }
    }

    public JPopupMenu getUserPopupMenu() {
        return userPopupMenu;
    }

    public DefaultListModel<NetGame> getGameModel() {
        return gameModel;
    }

    public JPopupMenu getGamePopupMenu() {
        return gamePopupMenu;
    }

    public JScrollPane getScrollPanelGames() {
        return scrollPanelGames;
    }

    public JMenuItem getGameDetailsMenuItem() {
        return gameDetailsMenuItem;
    }

    public JMenuItem getUserKickMenuItem() {
        return userKckMenuItem;
    }

    public JMenuItem getUserBanMenuItem() {
        return userBanMenuItem;
    }

    public JMenuItem getGameCloseMenuItem() {
        return gameCloseMenuItem;
    }

    public JMenuItem getGameSendMessageMenuItem() {
        return gameSendMessageMenuItem;
    }
    public JScrollPane getScrollPanelUsers() {
        return scrollPanelUsers;
    }

    public JMenuItem getExitMenuItem() {
        return exitMenuItem;
    }

    public JMenuItem getRefreshUserMenuItem() {
        return refreshUserMenuItem;
    }

    public JMenuItem getRefreshGamesMenuItem() {
        return refreshGamesMenuItem;
    }
}


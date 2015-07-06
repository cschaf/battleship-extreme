package de.hsbremen.battleshipextreme.client;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
/**
 * Eigenliches Fnster mit Menüs etc. Im Hauptbereich werden mithilfe des CardLayouts
 * unterschiedliche Panels geladen.
 */
public class GUI extends BasicTabbedPaneUI {
// ------------------------------ FIELDS ------------------------------

    public final static String MAIN_MENU_PANEL = "card with main menu panel";
    public final static String SETTINGS_PANEL = "card with settings panel";
    public final static String GAME_PANEL = "card with game panel";
    public final static String SERVER_CONNECTION_PANEL = "card with server connection panel";

    public final static Color EMPTY_COLOR = new Color(135, 206, 250);
    public final static Color PREVIEW_COLOR = Color.gray;
    public final static Color NOT_POSSIBLE_COLOR = Color.red;

    private ImageIcon hitIcon = new ImageIcon(getClass().getResource("/hit.png"));
    private ImageIcon missedIcon = new ImageIcon(getClass().getResource("/missed.png"));
    private ImageIcon destroyedIcon = new ImageIcon(getClass().getResource("/destroyed.png"));
    private ImageIcon shipIcon = new ImageIcon(getClass().getResource("/ship.png"));

    private JFrame frame;
    private MainMenuPanel panelMainMenu;
    private SettingsPanel panelSettings;
    private GamePanel panelGame;
    private ServerConnectionPanel panelServerConnection;
    private JMenuItem menuItemMainMenu, menuItemNewGame, menuItemSaveGame, menuItemLoadGame, menuItemQuitGame;
    private JMenuItem menuItemManual;
    private JMenuItem menuItemNextLookAndFeel;

    private JPanel cards;

// --------------------------- CONSTRUCTORS ---------------------------

    public GUI() {
        setDefaultLookAndFeel();

        frame = new JFrame("Battleship-Extreme");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.setResizable(false);
        frame.setJMenuBar(createMenuBar());

        initComponents();

        frame.pack();
        // frame.setMinimumSize(new Dimension(1500, 640));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        scaleIcons();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Game Menue
        JMenu gameMenu = new JMenu("Game");
        menuBar.add(gameMenu);

        menuItemMainMenu = new JMenuItem("Main Menu");
        menuItemNewGame = new JMenuItem("New Game");
        menuItemSaveGame = new JMenuItem("Save...");
        menuItemSaveGame.setEnabled(false);
        menuItemLoadGame = new JMenuItem("Load...");
        menuItemQuitGame = new JMenuItem("Quit");

        gameMenu.add(menuItemMainMenu);
        gameMenu.add(menuItemNewGame);
        gameMenu.add(menuItemSaveGame);
        gameMenu.add(menuItemLoadGame);
        gameMenu.add(menuItemQuitGame);

        // Window Menue
        JMenu windowMenu = new JMenu("Window");
        menuBar.add(windowMenu);
        menuItemNextLookAndFeel = new JMenuItem("Next Theme");
        windowMenu.add(menuItemNextLookAndFeel);

        // Help Menue
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);

        menuItemManual = new JMenuItem("Manual");
        helpMenu.add(menuItemManual);

        return menuBar;
    }

    private void initComponents() {
        panelMainMenu = new MainMenuPanel();
        panelSettings = new SettingsPanel();
        panelGame = new GamePanel();
        panelServerConnection = new ServerConnectionPanel();

        cards = new JPanel(new CardLayout());

        cards.add(panelMainMenu, MAIN_MENU_PANEL);
        cards.add(panelSettings, SETTINGS_PANEL);
        cards.add(panelGame, GAME_PANEL);
        cards.add(panelServerConnection, SERVER_CONNECTION_PANEL);

        frame.add(cards);

        panelSettings.resetFields();
    }

    private void scaleIcons() {
        hitIcon = getScaledIcon(hitIcon);
        missedIcon = getScaledIcon(missedIcon);
        destroyedIcon = getScaledIcon(destroyedIcon);
        shipIcon = getScaledIcon(shipIcon);
    }

    /**
     * Scales the button according to the current fieldButton-size.
     */
    private ImageIcon getScaledIcon(ImageIcon icon) {
        return new ImageIcon(icon.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH));
    }

    private void setDefaultLookAndFeel() {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look
            // and feel.
        }
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public ImageIcon getDestroyedIcon() {
        return destroyedIcon;
    }

    public JFrame getFrame() {
        return frame;
    }

    public ImageIcon getHitIcon() {
        return hitIcon;
    }

    public JMenuItem getMenuItemLoadGame() {
        return menuItemLoadGame;
    }

    public JMenuItem getMenuItemMainMenu() {
        return menuItemMainMenu;
    }

    public JMenuItem getMenuItemManual() {
        return menuItemManual;
    }

    public JMenuItem getMenuItemNewGame() {
        return menuItemNewGame;
    }

    public JMenuItem getMenuItemNextLookAndFeel() {
        return menuItemNextLookAndFeel;
    }

    public JMenuItem getMenuItemQuitGame() {
        return menuItemQuitGame;
    }

    public JMenuItem getMenuItemSaveGame() {
        return menuItemSaveGame;
    }

    public ImageIcon getMissedIcon() {
        return missedIcon;
    }

    public GamePanel getPanelGame() {
        return panelGame;
    }

    public MainMenuPanel getPanelMainMenu() {
        return panelMainMenu;
    }

    public ServerConnectionPanel getPanelServerConnection() {
        return panelServerConnection;
    }

    public SettingsPanel getPanelSettings() {
        return panelSettings;
    }

    public ImageIcon getShipIcon() {
        return shipIcon;
    }

// -------------------------- OTHER METHODS --------------------------

    public void showPanel(String card) {
        CardLayout c1 = (CardLayout) cards.getLayout();
        c1.show(cards, card);
    }
}

package de.hsbremen.battleshipextreme.client;

import javax.swing.*;
import java.awt.*;
/**
 * Representiert alle Komponenten des Spielbereiches
 */
public class GamePanel extends JPanel {
// ------------------------------ FIELDS ------------------------------

    private GameControlBarPanel panelGameControlBar;
    private JLabel labelInfo;
    private JPanel panelGameArea;
    private JPanel panelBoards;
    private BoardPanel panelEnemyBoard;
    private BoardPanel panelPlayerBoard;

    private JRadioButton radioButtonHorizontalOrientation;
    private JRadioButton radioButtonVerticalOrientation;
    private JComboBox comboBoxEnemySelection;
    private JButton buttonApplyEnemy;
    private JTextArea textAreaGameLog;
    private JTextArea textAreaChatLog;
    private JTextField textFieldChatMessage;
    private JButton buttonSendMessage;

    private JLabel[] labelShipCount;
    private JLabel[][] labelShip;
    private JRadioButton[] radioButtonShipSelection;

// --------------------------- CONSTRUCTORS ---------------------------

    public GamePanel() {
        this.setLayout(new BorderLayout());

        // Panel fuer die Spielsteuerung
        this.add(createNavigationPanel(), BorderLayout.WEST);

        // Panel fuer die eigentliche Spielflaeche
        panelGameArea = new JPanel(new BorderLayout());
        this.add(panelGameArea);

        // Panel fuer die beiden Boards
        panelBoards = new JPanel(new GridLayout(1, 2));

        // Label fuer die Spielinformationen
        labelInfo = new JLabel("Hier stehen aktuelle Spielinformationen!", SwingConstants.CENTER);
        labelInfo.setFont(new Font("Tahoma", Font.BOLD, 24));
        labelInfo.setBackground(Color.orange);
        labelInfo.setOpaque(true);
        panelGameArea.add(labelInfo, BorderLayout.NORTH);

        panelGameControlBar = new GameControlBarPanel();
        panelGameArea.add(panelGameControlBar, BorderLayout.SOUTH);


        // Spielbrett fuer den Gegner
        panelEnemyBoard = new BoardPanel();
        panelBoards.add(panelEnemyBoard);

        // Spielbrett fuer den Player
        panelPlayerBoard = new BoardPanel();
        panelBoards.add(panelPlayerBoard);

        // Panel fuer mit den Boards hinzufügen
        panelGameArea.add(panelBoards);
    }

    /**
     * Erzeugt das Navigationspanel
     */
    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;

        // ship selection
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;

        panel.add(createShipSelectionPanel(), c);

        // orientation panel
        c.gridy++;
        panel.add(createOrientationPanel(), c);

        // enemy panel
        c.gridy++;
        panel.add(createEnemySelectionPanel(), c);

        // game log panel
        c.gridy++;
        c.weighty = 1;
        panel.add(createGameLogPanel(), c);

        // chat panel
        c.gridy++;
        c.weighty = .5;
        panel.add(createChatPanel(), c);

        return panel;
    }

    /**
     * Erzeugt das Chatpanel
     */
    private JPanel createChatPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Chat"));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTHWEST;

        // Chat log
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 2;

        textAreaChatLog = new JTextArea();
        textAreaChatLog.setEditable(false);
        textAreaChatLog.setRows(4);
        textAreaChatLog.setLineWrap(true);
        textAreaChatLog.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(this.textAreaChatLog);
        panel.add(scroll, c);

        // Chat Message
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;

        textFieldChatMessage = new JTextField();
        panel.add(textFieldChatMessage, c);

        // send Button
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0;
        c.weighty = 0;

        buttonSendMessage = new JButton("send");
        panel.add(buttonSendMessage, c);

        return panel;
    }

    /**
     * Erzeugt das Gegner Selektierungs Panel
     */
    private JPanel createEnemySelectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Enemies"));

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;

        comboBoxEnemySelection = new JComboBox();
        panel.add(comboBoxEnemySelection, c);
        return panel;
    }

    /**
     * Erzeugt das Gamelog Panel
     */
    private JPanel createGameLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Game log"));

        textAreaGameLog = new JTextArea();
        textAreaGameLog.setEditable(false);
        textAreaGameLog.setRows(6);
        textAreaGameLog.setLineWrap(true);
        textAreaGameLog.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(textAreaGameLog);
        panel.add(scroll);

        return panel;
    }

    /**
     * Erzeugt das Panel für das wechseln der Ausrichtung
     */
    private JPanel createOrientationPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.setBorder(BorderFactory.createTitledBorder("Orientation"));

        radioButtonHorizontalOrientation = new JRadioButton("Horizontal");
        radioButtonVerticalOrientation = new JRadioButton("Vertical");

        ButtonGroup group = new ButtonGroup();
        group.add(radioButtonHorizontalOrientation);
        group.add(radioButtonVerticalOrientation);

        radioButtonHorizontalOrientation.setSelected(true);

        panel.add(radioButtonHorizontalOrientation);
        panel.add(radioButtonVerticalOrientation);

        return panel;
    }

    /**
     * Erzeugt das Panel für die Schiffselektion
     */
    private JPanel createShipSelectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Your Ships"));

        // GridBagLayout Einschränkungen
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0;
        c.weighty = 0;

        // Labels für die Anzahl der Schiffe
        labelShipCount = new JLabel[4];

        for (int i = 0; i < labelShipCount.length; i++) {
            c.insets = new Insets(0, 3, 0, 5);
            c.gridx = 0;
            c.gridy = i;
            labelShipCount[i] = new JLabel("0x");
            labelShipCount[i].setFont(new Font("Tahoma", Font.CENTER_BASELINE, 12));
            panel.add(labelShipCount[i], c);
        }

        // Labels für die Schiffe
        labelShip = new JLabel[4][];
        labelShip[0] = new JLabel[5];
        labelShip[1] = new JLabel[4];
        labelShip[2] = new JLabel[3];
        labelShip[3] = new JLabel[2];

        for (int y = 0; y < labelShip.length; y++) {
            for (int x = 0; x < labelShip[y].length; x++) {
                c.insets = new Insets(0, 0, 1, 1);
                c.gridx = x + 1;
                c.gridy = y;
                c.ipadx = c.ipady = 25;
                labelShip[y][x] = new JLabel();
                labelShip[y][x].setBackground(new Color(1, 124, 232));
                labelShip[y][x].setOpaque(true);
                panel.add(labelShip[y][x], c);
            }
        }

        // RadioButtons fuer Schiffauswahl
        c.ipadx = 0;
        c.ipady = 0;

        radioButtonShipSelection = new JRadioButton[4];
        radioButtonShipSelection[0] = new JRadioButton("Destroyer");
        radioButtonShipSelection[1] = new JRadioButton("Frigate");
        radioButtonShipSelection[2] = new JRadioButton("Corvette");
        radioButtonShipSelection[3] = new JRadioButton("Submarine");

        ButtonGroup group = new ButtonGroup();

        for (int i = 0; i < radioButtonShipSelection.length; i++) {
            c.insets = new Insets(0, 5, 0, 0);
            c.weightx = 1;
            c.gridx = 6;
            c.gridy = i;
            radioButtonShipSelection[i].setOpaque(false);
            panel.add(radioButtonShipSelection[i], c);
            group.add(radioButtonShipSelection[i]);
        }

        radioButtonShipSelection[0].setSelected(true);

        return panel;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public JButton getButtonApplyEnemy() {
        return buttonApplyEnemy;
    }

    public JButton getButtonSendMessage() {
        return buttonSendMessage;
    }

    public JComboBox getComboBoxEnemySelection() {
        return comboBoxEnemySelection;
    }

    public void setComboBoxEnemySelection(JComboBox comboBoxEnemySelection) {
        this.comboBoxEnemySelection = comboBoxEnemySelection;
    }

    public JLabel getLabelInfo() {
        return labelInfo;
    }

    public BoardPanel getPanelEnemyBoard() {
        return panelEnemyBoard;
    }

    public void setPanelEnemyBoard(BoardPanel panelEnemyBoard) {
        panelBoards.remove(this.panelEnemyBoard);
        this.panelEnemyBoard = panelEnemyBoard;
        panelBoards.add(this.panelEnemyBoard);
    }

    public GameControlBarPanel getPanelGameControlBar() {
        return panelGameControlBar;
    }

    public BoardPanel getPanelPlayerBoard() {
        return panelPlayerBoard;
    }

    public void setPanelPlayerBoard(BoardPanel panelPlayerBoard) {
        panelBoards.remove(this.panelPlayerBoard);
        this.panelPlayerBoard = panelPlayerBoard;
        panelBoards.add(this.panelPlayerBoard);
    }

    public JRadioButton getRadioButtonHorizontalOrientation() {
        return radioButtonHorizontalOrientation;
    }

    public JRadioButton getRadioButtonVerticalOrientation() {
        return radioButtonVerticalOrientation;
    }

    public JTextArea getTextAreaChatLog() {
        return textAreaChatLog;
    }

    public JButton getButtonDone() {
        return panelGameControlBar.getButtonDone();
    }

    public JToggleButton getButtonShowYourShips() {
        return panelGameControlBar.getButtonShowYourShips();
    }

    public JTextArea getTextAreaGameLog() {
        return textAreaGameLog;
    }

    public JTextField getTextFieldChatMessage() {
        return textFieldChatMessage;
    }

    	/* Ship Labels */

    public JLabel[] getLabelCorvette() {
        return labelShip[2];
    }

	/* Ship Count Labels */

    public JLabel getLabelCorvetteShipCount() {
        return labelShipCount[2];
    }

    public JLabel[] getLabelDestroyer() {
        return labelShip[0];
    }

    public JLabel getLabelDestroyerShipCount() {
        return labelShipCount[0];
    }

    public JLabel[] getLabelFrigate() {
        return labelShip[1];
    }

    public JLabel getLabelFrigateShipCount() {
        return labelShipCount[1];
    }

    public JLabel[] getLabelSubmarine() {
        return labelShip[3];
    }

	/* Radio Buttons */

    public JLabel getLabelSubmarineShipCount() {
        return labelShipCount[3];
    }

    public JRadioButton getRadioButtonCorvette() {
        return radioButtonShipSelection[2];
    }

    public JRadioButton getRadioButtonDestroyer() {
        return radioButtonShipSelection[0];
    }

    public JRadioButton getRadioButtonFrigate() {
        return radioButtonShipSelection[1];
    }

    public JRadioButton getRadioButtonSubmarine() {
        return radioButtonShipSelection[3];
    }

    public void resetShips() {
        for (int i = 0; i < labelShipCount.length; i++) {
            labelShipCount[i].setText("0x");
        }
        for (int y = 0; y < labelShip.length; y++) {
            for (int x = 0; x < labelShip[y].length; x++) {
                labelShip[y][x].setBackground(new Color(1, 124, 232));
            }
        }
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * Löscht den Chatverlauf
     */
    public void clearChatLog() {
        getTextAreaChatLog().setText("");
    }

    /**
     * Löscht den Gamelogverlauf
     */
    public void clearGameLog() {
        getTextAreaGameLog().setText("");
    }
}
package de.hsbremen.battleshipextreme.client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class GUI {
	private JFrame frame;
	
	private JMenuItem saveGameItem, loadGameItem, quitGameItem;
	private JMenuItem manualItem;
	
	private GamePanel gamePanel;
	
	public GUI() {
		frame = new JFrame("Battleship-Extreme");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(createMenuBar());
		
		InitComponents();

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private void InitComponents() {
		gamePanel = new GamePanel();
		frame.add(gamePanel);
	}
	
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		
		// Game Menue
		JMenu gameMenu = new JMenu("Game");
		menuBar.add(gameMenu);

		saveGameItem = new JMenuItem("Save...");
		loadGameItem = new JMenuItem("Load...");
		quitGameItem = new JMenuItem("Quit");
		
		gameMenu.add(saveGameItem);
		gameMenu.add(loadGameItem);
		gameMenu.add(quitGameItem);
		
		// Help Menue
		JMenu helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);
		
		manualItem = new JMenuItem("Manual");
		helpMenu.add(manualItem);
		
		return menuBar;
	}
}

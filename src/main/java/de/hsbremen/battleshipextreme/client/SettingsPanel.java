package de.hsbremen.battleshipextreme.client;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SettingsPanel extends JPanel {

	private JLabel labelPlayers;
	private JLabel labelAiPlayers;
	private JLabel labelDestroyers;
	private JLabel labelFrigates;
	private JLabel labelCorvettes;
	private JLabel labelSubmarines;
	private JLabel labelBoardSize;

	private JTextField textFieldPlayers;
	private JTextField textFieldAiPlayers;
	private JTextField textFieldDestroyers;
	private JTextField textFieldFrigates;
	private JTextField textFieldCorvettes;
	private JTextField textFieldSubmarines;
	private JTextField textFieldBoardSize;

	private JButton buttonApplySettings;

	public SettingsPanel() {
		this.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHEAST;
		c.insets = new Insets(0, 0, 10, 10);
		c.weightx = 0;
		c.weighty = 0;

		c.gridx = 0;
		c.gridy = 0;

		labelPlayers = new JLabel("Players: ");
		this.add(labelPlayers, c);

		c.gridy++;
		labelAiPlayers = new JLabel("Ai-Players: ");
		this.add(labelAiPlayers, c);

		c.gridy++;
		labelDestroyers = new JLabel("Destroyers: ");
		this.add(labelDestroyers, c);

		c.gridy++;
		labelFrigates = new JLabel("Frigates: ");
		this.add(labelFrigates, c);

		c.gridy++;
		labelCorvettes = new JLabel("Corvettes: ");
		this.add(labelCorvettes, c);

		c.gridy++;
		labelSubmarines = new JLabel("Submarines: ");
		this.add(labelSubmarines, c);

		c.gridy++;
		labelBoardSize = new JLabel("Board size: ");
		this.add(labelBoardSize, c);

		c.insets = new Insets(0, 0, 10, 0);
		c.gridx = 1;
		c.gridy = 0;

		textFieldPlayers = new JTextField(10);
		this.add(textFieldPlayers, c);

		c.gridy++;
		textFieldAiPlayers = new JTextField(10);
		this.add(textFieldAiPlayers, c);

		c.gridy++;
		textFieldDestroyers = new JTextField(10);
		this.add(textFieldDestroyers, c);

		c.gridy++;
		textFieldFrigates = new JTextField(10);
		this.add(textFieldFrigates, c);

		c.gridy++;
		textFieldCorvettes = new JTextField(10);
		this.add(textFieldCorvettes, c);

		c.gridy++;
		textFieldSubmarines = new JTextField(10);
		this.add(textFieldSubmarines, c);

		c.gridy++;
		textFieldBoardSize = new JTextField(10);
		this.add(textFieldBoardSize, c);

		c.gridy++;
		buttonApplySettings = new JButton("OK");
		this.add(buttonApplySettings, c);

	}

	// ////////////////////////////////////////////////////////////////
	// get Components
	// ////////////////////////////////////////////////////////////////

	public JLabel getLabelPlayers() {
		return labelPlayers;
	}

	public JLabel getLabelDestroyers() {
		return labelDestroyers;
	}

	public JLabel getLabelFrigates() {
		return labelFrigates;
	}

	public JLabel getLabelCorvettes() {
		return labelCorvettes;
	}

	public JLabel getLabelSubmarines() {
		return labelSubmarines;
	}

	public JLabel getLabelBoardSize() {
		return labelBoardSize;
	}

	public JTextField getTextFieldPlayers() {
		return textFieldPlayers;
	}

	public JTextField getTextFieldAiPlayers() {
		return textFieldAiPlayers;
	}

	public JTextField getTextFieldDestroyers() {
		return textFieldDestroyers;
	}

	public JTextField getTextFieldFrigates() {
		return textFieldFrigates;
	}

	public JTextField getTextFieldCorvettes() {
		return textFieldCorvettes;
	}

	public JTextField getTextFieldSubmarines() {
		return textFieldSubmarines;
	}

	public JTextField getTextFieldBoardSize() {
		return textFieldBoardSize;
	}

	public JButton getButtonApplySettings() {
		return buttonApplySettings;
	}
}

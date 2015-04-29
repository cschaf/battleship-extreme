package de.hsbremen.battleshipextreme.client;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ChatPanel extends JPanel {
	
	JTextArea chatWindow;
	
	public ChatPanel() {
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createTitledBorder("Chat"));
		this.setBackground(Color.ORANGE);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTHWEST;

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		this.chatWindow = new JTextArea("test");
		this.chatWindow.setEditable(false);
		JScrollPane scroll = new JScrollPane(this.chatWindow);
		this.add(scroll, c);
		
		
		
	}
}

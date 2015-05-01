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
import javax.swing.JTextField;

public class ChatPanel extends JPanel {
	
	private JTextArea chatWindowTextArea;
	private JTextField chatMessageTextField;
	private JButton sendMessageButton;
	
	public ChatPanel() {
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createTitledBorder("Chat"));
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTHWEST;

		// Chat Window
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 2;
		
		chatWindowTextArea = new JTextArea();
		chatWindowTextArea.setEditable(false);
		chatWindowTextArea.setRows(6);
		chatWindowTextArea.setLineWrap(true);
		chatWindowTextArea.setWrapStyleWord(true);
		
		JScrollPane scroll = new JScrollPane(this.chatWindowTextArea);
		this.add(scroll, c);
		
		// Chat Message
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 1;
		
		chatMessageTextField = new JTextField();
		this.add(chatMessageTextField, c);
		
		// Button
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0;
		c.weighty = 0;
		
		sendMessageButton = new JButton("send");
		this.add(sendMessageButton, c);
	}
}

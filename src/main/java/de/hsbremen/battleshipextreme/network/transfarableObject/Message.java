package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.network.TransferableType;

/**
 * Created by cschaf on 25.04.2015.
 */
public class Message extends TransferableObject {
	protected String message;

	public Message(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public TransferableType getType() {
		return TransferableType.Message;
	}

	@Override
	public String toString() {
		return this.getMessage() + " - " + String.format("%1$TT", this.getCreatedAt());
	}
}

package se.miun.distsys.messages;

import se.miun.distsys.messages.Message;

public class ElectionMessage extends Message {
	int authorID;
	
	public ElectionMessage(int id) {
		this.authorID = id;
	}
}

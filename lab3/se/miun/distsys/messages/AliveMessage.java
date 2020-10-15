package se.miun.distsys.messages;

import se.miun.distsys.messages.Message;

public class AliveMessage extends Message {
	public int authorID;

	public AliveMessage(int id) {
		this.authorID = id;
	}
}

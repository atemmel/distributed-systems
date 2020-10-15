package se.miun.distsys.messages;

import se.miun.distsys.messages.Message;

public class VictoryMessage extends Message {
	int authorID;

	public VictoryMessage(int id) {
		this.authorID = id;
	}
}

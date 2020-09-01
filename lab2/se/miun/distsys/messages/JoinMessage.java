package se.miun.distsys.messages;

import se.miun.distsys.User;

public class JoinMessage extends Message {

	public User user;

	public JoinMessage(User user) {
		this.user = user;
	}

}

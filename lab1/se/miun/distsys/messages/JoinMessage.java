package se.miun.distsys.messages;

public class JoinMessage extends Message {

	public String user = "";

	public JoinMessage(String user) {
		this.user = user;
	}

}

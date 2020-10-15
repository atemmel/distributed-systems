package se.miun.distsys.messages;

public class AuthMessage extends Message {
	public String username;
	public AuthMessage(String username) {
		this.username = username;
	}
}

package se.miun.distsys.messages;

import se.miun.distsys.User;

public class ChatMessage extends Message {

	public String chat = "";	
	public User user;
	
	public ChatMessage(String chat, User user) {
		this.chat = chat;
		this.user = user;
	}
}

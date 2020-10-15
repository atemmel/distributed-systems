package se.miun.distsys.messages;

import java.io.Serializable;
import java.util.ArrayList;

public class AssignMessage extends Message {
	public int id;
	public ArrayList<Integer> history;
	public ArrayList<ChatMessage> chatHistory;

	public AssignMessage(int id, ArrayList<Integer> history, ArrayList<ChatMessage> chatHistory) {
		this.id = id;
		this.history = history;
		this.chatHistory = chatHistory;
	}
}

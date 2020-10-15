package se.miun.distsys.messages;

public class UserIDMessage extends Message {
	public int userID;
	public int recipientID;

	public UserIDMessage(int userID, int recipientID) {
		this.userID = userID;
		this.recipientID = recipientID;
	}
}

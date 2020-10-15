package se.miun.distsys.messages;

public class GetUserIDMessage extends Message {
	public int sequenceNumber;
	public int authorID;

	public GetUserIDMessage(int sequenceNumber, int authorID) {
		this.sequenceNumber = sequenceNumber;
		this.authorID = authorID;
	}
}

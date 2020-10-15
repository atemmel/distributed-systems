package se.miun.distsys.messages;

public class GenerateSequenceNumberMessage extends Message {
	public int authorID;

	public GenerateSequenceNumberMessage(int authorID) {
		this.authorID = authorID;
	}
}

package se.miun.distsys.messages;

public class SequenceNumberMessage extends Message {
	public int sequenceNumber;
	public int recipientID;

	public SequenceNumberMessage(int sequenceNumber, int recipientID) {
		this.sequenceNumber = sequenceNumber;
		this.recipientID = recipientID;
	}
}

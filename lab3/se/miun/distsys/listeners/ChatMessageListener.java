package se.miun.distsys.listeners;

import se.miun.distsys.messages.ChatMessage;
import se.miun.distsys.messages.JoinMessage;
import se.miun.distsys.messages.LeaveMessage;
import se.miun.distsys.messages.StatusMessage;
import se.miun.distsys.messages.AssignMessage;
import se.miun.distsys.messages.AuthMessage;

import se.miun.distsys.messages.GenerateSequenceNumberMessage;
import se.miun.distsys.messages.SequenceNumberMessage;
import se.miun.distsys.messages.GetUserIDMessage;
import se.miun.distsys.messages.UserIDMessage;

import se.miun.distsys.messages.AliveMessage;
import se.miun.distsys.messages.ElectionMessage;
import se.miun.distsys.messages.VictoryMessage;

public interface ChatMessageListener {
    public void onIncomingChatMessage(ChatMessage chatMessage);
	public void onIncomingJoinMessage(JoinMessage joinMessage);
	public void onIncomingLeaveMessage(LeaveMessage leaveMessage);
	public void onIncomingStatusMessage(StatusMessage statusMessage);

	// Assign
	public void onIncomingAssignMessage(AssignMessage assignMessage);
	public void onIncomingAuthMessage(AuthMessage authMessage);

	// Election
	public void onIncomingAliveMessage(AliveMessage aliveMessage);
	public void onIncomingElectionMessage(ElectionMessage electionMessage);
	public void onIncomingVictoryMessage(VictoryMessage victoryMessage);

	// Total ordering
		// Begäran om nytt sequencenummer
	public void onIncomingGenerateSequenceNumberMessage(GenerateSequenceNumberMessage generateSequenceNumberMessage);
		// Sändning av nytt sequencenummer
	public void onIncomingSequenceNumberMessage(SequenceNumberMessage sequenceNumberMessage);
		// Begäran om matchande användarid
	public void onIncomingGetUserIDMessage(GetUserIDMessage getUserIDMessage);		// Sändning av nytt användarid
	public void onIncomingUserIDMessage(UserIDMessage userIDMessage);
}

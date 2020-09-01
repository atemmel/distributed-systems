package se.miun.distsys.listeners;

import se.miun.distsys.messages.ChatMessage;
import se.miun.distsys.messages.JoinMessage;
import se.miun.distsys.messages.LeaveMessage;
import se.miun.distsys.messages.StatusMessage;

public interface ChatMessageListener {
    public void onIncomingChatMessage(ChatMessage chatMessage);
	public void onIncomingJoinMessage(JoinMessage joinMessage);
	public void onIncomingLeaveMessage(LeaveMessage leaveMessage);
	public void onIncomingStatusMessage(StatusMessage statusMessage);
}

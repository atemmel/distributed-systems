package se.miun.distsys.listeners;

import se.miun.distsys.messages.ChatMessage;
import se.miun.distsys.messages.JoinMessage;
import se.miun.distsys.messages.LeaveMessage;

public interface ChatMessageListener {
    public void onIncomingChatMessage(ChatMessage chatMessage);
	public void onIncomingJoinMessage(JoinMessage joinMessage);
	public void onIncomingLeaveMessage(LeaveMessage leaveMessage);
}

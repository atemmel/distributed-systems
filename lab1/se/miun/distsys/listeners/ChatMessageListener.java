package se.miun.distsys.listeners;

import se.miun.distsys.messages.ChatMessage;
import se.miun.distsys.messages.JoinMessage;

public interface ChatMessageListener {
    public void onIncomingChatMessage(ChatMessage chatMessage);
	public void onIncomingJoinMessage(JoinMessage joinMessage);
}

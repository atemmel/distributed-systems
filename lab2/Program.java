import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.ArrayList;

import se.miun.distsys.GroupCommuncation;
import se.miun.distsys.listeners.ChatMessageListener;
import se.miun.distsys.messages.ChatMessage;
import se.miun.distsys.messages.JoinMessage;
import se.miun.distsys.messages.LeaveMessage;
import se.miun.distsys.messages.StatusMessage;
import se.miun.distsys.User;

//Skeleton code for Distributed systems 9hp, DT050A
// Now extended :^)

public class Program implements ChatMessageListener {

	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));		
	HashMap<String, User> users = new HashMap<String, User>();
	ArrayList<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
	GroupCommuncation gc = null;	
	User currentUser;
	boolean connected = false;
	
	public static void main(String[] args) {
		Program program = new Program();
		program.run();
	}

	public void run() {
		System.out.println("Enter username: ");
		readName();

		join();
		connected = true;
		registerInterruptSignal();
		System.out.println("Group Communcation Started");

		// Message loop
		for(;;) {			
			try {
				System.out.println("Clock is: " + currentUser.clock + " Write message to send: ");	
				String chat = input.readLine();			
				if (chat == null) {
					break;
				}

				if (chat.equals("show")) {
					showChatMessages();
				} else if (chat.isBlank()) {
					continue;
				} else {
					var chatMessage = new ChatMessage(chat, currentUser);
					gc.sendChatMessage(chatMessage);
					currentUser.clock++;
				}
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}

		leave();
		gc.shutdown();
	}

	@Override
	public void onIncomingChatMessage(ChatMessage chatMessage) {		
		System.out.println(chatMessage.user.clock + " Incoming chat message: " + chatMessage.chat);	
		updateClock(chatMessage.user.clock);
		insertMessage(chatMessage);
	}

	@Override
	public void onIncomingJoinMessage(JoinMessage joinMessage) {
		users.put(joinMessage.user.name, joinMessage.user);
		System.out.println("User " + joinMessage.user.name + " has joined");
		listUsers();
		try {
			var statusMessage = new StatusMessage(currentUser);
			gc.sendStatusMessage(statusMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onIncomingLeaveMessage(LeaveMessage leaveMessage) {
		users.remove(leaveMessage.user.name);
		System.out.println("User " + leaveMessage.user.name + " has left");
		listUsers();
	}

	@Override
	public void onIncomingStatusMessage(StatusMessage statusMessage) {
		users.put(statusMessage.user.name, statusMessage.user);
		updateClock(statusMessage.user.clock);
	}

	private void join() {
		gc = new GroupCommuncation();		
		gc.setChatMessageListener(this);
		try {
			var joinMessage = new JoinMessage(currentUser);
			gc.sendJoinMessage(joinMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void listUsers() {
		System.out.println(users.size() + " total users connected");
	}

	private void showChatMessages() {
		for(var msg : chatMessages) {
			System.out.println(msg.user.clock + " " + msg.chat);
		}
	}

	private void leave() {
		if(connected) {
			try {
				var leaveMessage = new LeaveMessage(currentUser);
				gc.sendLeaveMessage(leaveMessage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		connected = false;
	}

	private void readName() {
		try { 
			String name = input.readLine();
			if (name == null) {
				System.exit(-1);
			}
			currentUser = new User(name, 0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private void registerInterruptSignal() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				leave();
			}
		});
	}

	private void updateClock(int newClock) {
		synchronized (currentUser) {
			if(currentUser.clock < newClock) {
				currentUser.clock = newClock;
			} else if(currentUser.clock == newClock) {
				currentUser.clock++;
			}
		}
	}

	private void insertMessage(ChatMessage chatMessage) {
		for(int i = 0; i < chatMessages.size(); i++) {
			if(chatMessages.get(i).user.clock >= chatMessage.user.clock) {
				chatMessages.add(i, chatMessage);
				return;
			}
		}
		chatMessages.add(chatMessage);
	}
}

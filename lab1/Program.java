import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import se.miun.distsys.GroupCommuncation;
import se.miun.distsys.listeners.ChatMessageListener;
import se.miun.distsys.messages.ChatMessage;
import se.miun.distsys.messages.JoinMessage;
import se.miun.distsys.messages.LeaveMessage;
import se.miun.distsys.User;

//Skeleton code for Distributed systems 9hp, DT050A
// Now extended :^)

public class Program implements ChatMessageListener {

	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));		
	HashMap<String, User> users = new HashMap<String, User>();
	GroupCommuncation gc = null;	
	String name = "";
	
	public static void main(String[] args) {
		Program program = new Program();
		program.run();
	}

	public void run() {

		System.out.println("Enter username: ");
		readName();

		join();
		registerInterruptSignal();
		System.out.println("Group Communcation Started");

		// Message loop
		for(;;) {			
			try {
				System.out.println("Write message to send: ");	
				String chat = input.readLine();			
				if (chat == null) {
					break;
				}
				gc.sendChatMessage(chat);
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}

		// Leave
		leave();
		gc.shutdown();
	}

	@Override
	public void onIncomingChatMessage(ChatMessage chatMessage) {		
		System.out.println("Incoming chat message: " + chatMessage.chat);	
	}

	@Override
	public void onIncomingJoinMessage(JoinMessage joinMessage) {
		users.put(joinMessage.user.name, joinMessage.user);
		System.out.println("User " + joinMessage.user.name + " has joined");
		listUsers();
	}

	@Override
	public void onIncomingLeaveMessage(LeaveMessage leaveMessage) {
		users.remove(leaveMessage.user.name);
		System.out.println("User " + leaveMessage.user.name + " has left");
		listUsers();
	}

	private void join() {
		gc = new GroupCommuncation();		
		gc.setChatMessageListener(this);
		try {
			gc.sendJoinMessage(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void listUsers() {
		System.out.println(users.size() + " total users connected");
	}

	private void leave() {
		try {
			gc.sendLeaveMessage(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readName() {
		try { 
			name = input.readLine();
			if (name == null) {
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
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
}

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
import se.miun.distsys.messages.AssignMessage;
import se.miun.distsys.messages.AuthMessage;
import se.miun.distsys.messages.GenerateSequenceNumberMessage;
import se.miun.distsys.messages.SequenceNumberMessage;
import se.miun.distsys.messages.GetUserIDMessage;
import se.miun.distsys.messages.UserIDMessage;
import se.miun.distsys.messages.AliveMessage;
import se.miun.distsys.messages.ElectionMessage;
import se.miun.distsys.messages.VictoryMessage;
import se.miun.distsys.User;
import se.miun.distsys.Coordinator;

//Skeleton code for Distributed systems 9hp, DT050A
// Now extended :^)

public class Program implements ChatMessageListener {

	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));		
	GroupCommuncation gc = null;	
	User user;
	public ArrayList<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
	public HashMap<Integer, ChatMessage> misses = new HashMap<Integer, ChatMessage>();
	boolean connected = false;
	AssignMessage assignMessage = null;

	boolean expectsUserIDMessage = false;
	UserIDMessage recievedUserIDMessage = null;

	boolean expectsSequenceNumberMessage = false;
	SequenceNumberMessage recievedSequenceNumberMessage = null;
	int getSequenceNumberResult = 0;

	boolean wonElection = false;
	boolean electionInProcess = false;
	boolean expectsVictoryMessage = false;
	boolean victoryMessageRecieved = false;
	
	public static void main(String[] args) {
		Program program = new Program();
		program.run();
	}

	public void run() {
		System.out.println("Enter username: ");
		readName();
		gc = new GroupCommuncation();		
		gc.setChatMessageListener(this);
		authenticate();
		while(assignMessage != null && assignMessage.id == Coordinator.InvalidId) {
			assignMessage = null;
			System.out.println("Username was already taken");
			readName();
			authenticate();
		}
		if(assignMessage == null) { // Så är vi coordinatorn
			System.out.println("We are the coordinator");
			user = new Coordinator(user);
			assignMessage = new AssignMessage(0, new ArrayList<Integer>(), new ArrayList<ChatMessage>());
		}

		user.sequenceList = assignMessage.history;
		chatMessages = assignMessage.chatHistory;
		System.out.println("Authenticated with id " + ((Integer)user.id).toString());
		join();
		connected = true;
		registerInterruptSignal();

		// Message loop
		for(;;) {			
			try {
				System.out.println(user.users.keySet() + " Write message to send: ");	
				String chat = input.readLine();			
				if (chat == null) {
					break;
				}

				if (chat.equals("show")) {
					showChatMessages();
				} else if (chat.equals("amicoord")) {
					System.out.println(isCoordinator());
				} else if (chat.isBlank()) {
					continue;
				} else {
					int sequenceNumber = isCoordinator() ? getSequenceNumberAsCoordinator() : getSequenceNumberAsUser();

					ChatMessage chatMessage = new ChatMessage(chat, user, sequenceNumber);
					gc.sendChatMessage(chatMessage);
				}
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}

		leave();
		gc.shutdown();
	}

	public int getSequenceNumberAsCoordinator() {
		Coordinator coord = (Coordinator)user;
		int seq = coord.getSequenceNumber(user.id);
		return seq;
	}

	public int getSequenceNumberAsUser() {
		GenerateSequenceNumberMessage msg = new GenerateSequenceNumberMessage(user.id);

		recievedSequenceNumberMessage = null;
		expectsSequenceNumberMessage = true;

		gc.sendGenerateSequenceNumberMessage(msg);

		Thread th = new Thread() {
			public void run() {
				int i = 0;
				while(recievedSequenceNumberMessage == null) {
					try {
						Thread.sleep(50);
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
					if(i > 20) {
						doElection();
						while(electionInProcess) {
							try {
								Thread.sleep(50);
							} catch(InterruptedException e) {
								e.printStackTrace();
							}
						}

						getSequenceNumberResult = isCoordinator() ? getSequenceNumberAsCoordinator() : getSequenceNumberAsUser();
						return;
					}
					i++;
				}

				getSequenceNumberResult = recievedSequenceNumberMessage.sequenceNumber;

			}

		};

		th.start();
		try {
			th.join();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		return getSequenceNumberResult;
	}

	@Override
	public void onIncomingChatMessage(ChatMessage chatMessage) {		
		if(!isAuthenticated()) {
			return;
		}

		if(isCoordinator()) {
			handleChatMessageAsCoordinator(chatMessage);
		} else {
			handleChatMessageAsUser(chatMessage);
		}
	}

	public void handleChatMessageAsCoordinator(ChatMessage chatMessage) {
		Coordinator coord = (Coordinator)user;
		int uid = coord.getUserIDfromSequenceNumber(chatMessage.sequenceNumber);
		if(uid != chatMessage.user.id) {
			// Om detta sker har sändaren kringgått kordinatorn
			return;
		}

		insertMessage(chatMessage);
	}

	public void handleChatMessageAsUser(ChatMessage chatMessage) {
		if(user.id != chatMessage.user.id) {
			GetUserIDMessage msg = new GetUserIDMessage(chatMessage.sequenceNumber, user.id);
			recievedUserIDMessage = null;
			expectsUserIDMessage = true;
			gc.sendGetUserIDMessage(msg);
		} else {
			user.sequenceList.add(chatMessage.sequenceNumber);
			insertMessage(chatMessage);
			return;
		}

		Thread th = new Thread() {
			public void run() {
						
				int i = 0;
				while(recievedUserIDMessage == null) {
					try {
						Thread.sleep(50);
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
					if(i > 20) {
						doElection();
						while(electionInProcess) {
							try {
								Thread.sleep(50);
							} catch(InterruptedException e) {
								e.printStackTrace();
							}
						}

						try {
							Thread.sleep(50);
						} catch(InterruptedException e) {
							e.printStackTrace();
						}
						if(isCoordinator()) {
							handleChatMessageAsCoordinator(chatMessage);
						} else {
							handleChatMessageAsUser(chatMessage);
						}
						return;
					}
					i++;
				}

				int uid = recievedUserIDMessage.userID;
				if(uid != chatMessage.user.id) {
					// Om detta sker har sändaren kringgått kordinatorn
					return;
				}

				user.sequenceList.add(chatMessage.sequenceNumber);
				insertMessage(chatMessage);
			}
		};
		th.start();

	}

	@Override
	public void onIncomingJoinMessage(JoinMessage joinMessage) {
		if(!isAuthenticated()) {
			return;
		}
		user.users.put(joinMessage.user.name, joinMessage.user);
		System.out.println("User " + joinMessage.user.name + " has joined");
		listUsers();
		try {
			StatusMessage statusMessage = new StatusMessage(user);
			gc.sendStatusMessage(statusMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onIncomingLeaveMessage(LeaveMessage leaveMessage) {
		if(!isAuthenticated()) {
			return;
		}
		user.users.remove(leaveMessage.user.name);
		System.out.println("User " + leaveMessage.user.name + " has left");
		listUsers();
	}

	@Override
	public void onIncomingStatusMessage(StatusMessage statusMessage) {
		if(!isAuthenticated()) {
			return;
		}
		user.users.put(statusMessage.user.name, statusMessage.user);
	}

	@Override
	public void onIncomingAssignMessage(AssignMessage assignMessage) {
		if(isAuthenticated()) {	// Already assigned
			return;
		}

		this.assignMessage = assignMessage;
		user.id = assignMessage.id;
	}

	@Override
	public void onIncomingAuthMessage(AuthMessage authMessage) {
		if(!isAuthenticated() || !isCoordinator()) {
			return;
		}

		Coordinator coord = (Coordinator)user;
		int id = coord.approveUser(authMessage.username);
		gc.sendAssignMessage(new AssignMessage(id, user.sequenceList, chatMessages));
	}

	@Override
	public void onIncomingGenerateSequenceNumberMessage(GenerateSequenceNumberMessage generateSequenceNumberMessage) {
		if(!isAuthenticated() || !isCoordinator()) {
			return;
		}

		Coordinator coord = (Coordinator)user;
		int seq = coord.getSequenceNumber(generateSequenceNumberMessage.authorID);

		SequenceNumberMessage msg = new SequenceNumberMessage(seq, generateSequenceNumberMessage.authorID);
		gc.sendSequenceNumberMessage(msg);
	}

	@Override
	public void onIncomingSequenceNumberMessage(SequenceNumberMessage sequenceNumberMessage) {
		if(!isAuthenticated() || isCoordinator() || !expectsSequenceNumberMessage) {
			return;
		}

		if(sequenceNumberMessage.recipientID != user.id) {
			return;
		}

		expectsSequenceNumberMessage = false;
		recievedSequenceNumberMessage = sequenceNumberMessage;
	}

	@Override
	public void onIncomingGetUserIDMessage(GetUserIDMessage getUserIDMessage) {
		if(!isAuthenticated() || !isCoordinator()) {
			return;
		}

		Coordinator coord = (Coordinator)user;
		int uid = coord.getUserIDfromSequenceNumber(getUserIDMessage.sequenceNumber);

		UserIDMessage msg = new UserIDMessage(uid, getUserIDMessage.authorID);
		gc.sendUserIDMessage(msg);
	}

	@Override
	public void onIncomingUserIDMessage(UserIDMessage userIDMessage) {
		if(!isAuthenticated() || isCoordinator() || !expectsUserIDMessage) {
			return;
		}

		if(userIDMessage.recipientID != user.id) {
			return;
		}

		expectsUserIDMessage = false;
		recievedUserIDMessage = userIDMessage;
	}

	@Override
	public void onIncomingAliveMessage(AliveMessage aliveMessage) {
		if(!isAuthenticated()) {
			return;
		}

		if(aliveMessage.authorID > user.id) {
			System.out.println("I lost the election " + ((Integer)aliveMessage.authorID).toString() + " > " + ((Integer)user.id).toString());
			wonElection = false;
			if(isCoordinator() ) {	// Om du redan råkat tilldelas rollen som kordinator
				synchronized(user) {
					var newuser = new User(user.name);
					newuser.id = user.id;
					newuser.sequenceList = user.sequenceList;
					newuser.users = user.users;
					user = newuser;
				}
			}
		}
	}

	@Override
	public void onIncomingElectionMessage(ElectionMessage electionMessage) {
		if(!isAuthenticated()) {
			return;
		}

		expectsVictoryMessage = true;
		System.out.println("Election is starting...");
		if(electionInProcess) {
			return;
		}
		wonElection = true;
		electionInProcess = true;
		var msg = new AliveMessage(user.id);
		gc.sendAliveMessage(msg);
		partakeInElection();
	}

	@Override
	public void onIncomingVictoryMessage(VictoryMessage victoryMessage) {
		if(!isAuthenticated()) {
			return;
		}

		electionInProcess = false;
		victoryMessageRecieved = true;
	}

	private void join() {
		try {
			JoinMessage joinMessage = new JoinMessage(user);
			gc.sendJoinMessage(joinMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void listUsers() {
		System.out.println(user.users.size() + " total users connected");
	}

	private void showChatMessages() {
		for(ChatMessage msg : chatMessages) {
			System.out.println(msg.sequenceNumber + " " + msg.chat);
		}
	}

	private void leave() {
		if(connected) {
			try {
				LeaveMessage leaveMessage = new LeaveMessage(user);
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
			user = new User(name);
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

	private void insertMessage(ChatMessage chatMessage) {
		if(chatMessages.size() != chatMessage.sequenceNumber) {
			misses.put(chatMessage.sequenceNumber, chatMessage);
			return;
		}
		chatMessages.add(chatMessage);
		clearMisses();
		System.out.println(chatMessage.sequenceNumber + " Incoming chat message: " + chatMessage.chat);	
	}

	private void clearMisses() {
		Integer index = chatMessages.size();
		var msg = misses.get(index);
		if(msg == null) {
			return;
		}

		misses.remove(index);
		chatMessages.add(msg);
		clearMisses();
	}

	private void authenticate() {
		AuthMessage auth = new AuthMessage(user.name);
		gc.sendAuthMessage(auth);
		try {
			Thread.sleep(1000);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void partakeInElection() {
		System.out.println("I am partaking in an election");
		Thread election = new Thread() {
			public void run() {
						
				int i = 0;
				while(electionInProcess) {
					try {
						Thread.sleep(50);
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
					if(i > 20) {
						return;
					}
					i++;
				}
			}
		};

		election.start();
		try {
			election.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if(!wonElection) {
			return;
		}

		Thread victory = new Thread() {
			public void run() {
						
				int i = 0;
				while(electionInProcess) {
					try {
						Thread.sleep(50);
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
					if(i > 20) {
						return;
					}
					i++;
				}
			}
		};
		victory.run();
		try {
			victory.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		electionInProcess = false;
		synchronized(user) {
			user = new Coordinator(user);
		}
		var msg = new VictoryMessage(user.id);
		gc.sendVictoryMessage(msg);
	}

	private void doElection() {
		System.out.println("I need to start an election!");
		wonElection = true;
		electionInProcess = true;
		expectsVictoryMessage = true;
		var msg = new ElectionMessage(user.id);
		gc.sendElectionMessage(msg);
		var alivemsg = new AliveMessage(user.id);
		gc.sendAliveMessage(alivemsg);
		
		Thread election = new Thread() {
			public void run() {
						
				int i = 0;
				while(electionInProcess && wonElection) {
					try {
						Thread.sleep(50);
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
					if(i > 20) {
						if(wonElection) { // Vi vann
							electionInProcess = false;
							System.out.println("I won the election!");
							synchronized(user) {
								user = new Coordinator(user);
							}
							var msg = new VictoryMessage(user.id);
							gc.sendVictoryMessage(msg);
						} else {	// Vi förlorade
							return;
						}
					}
					i++;
				}
			}
		};
		election.start();
		try {
			election.join();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}

		Thread victory = new Thread() {
			public void run() {
						
				int i = 0;
				while(!victoryMessageRecieved) {
					try {
						Thread.sleep(50);
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
					if(i > 20) {
						break;
					}
					i++;
				}
			}
		};

		victory.start();
		try {
			victory.join();
			Thread.sleep(100);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		electionInProcess = false;
	}

	private boolean isAuthenticated() {
		return assignMessage != null;
	}

	private boolean isCoordinator() {
		synchronized(user) {
			return user instanceof Coordinator;
		}
	}
}

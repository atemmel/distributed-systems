package se.miun.distsys;

public class Coordinator extends User {
	public static int InvalidId = -1;

	public int idCounter;
	public Coordinator(User user) {
		super(user.name);
		this.id = user.id;
		this.users = user.users;
		this.sequenceList = user.sequenceList;
		this.idCounter = users.size();
	}

	public int approveUser(String username) {
		synchronized(users) {
			boolean hasNotUsername = users.get(username) == null;
			if (hasNotUsername) {
				users.put(username, new User(username));
				return ++idCounter;
			}
		}
		return InvalidId;
	}

	public int getSequenceNumber(int uid) {
		int nextSequenceNumber = 0;
		synchronized (sequenceList) {
			nextSequenceNumber = sequenceList.size();
			sequenceList.add(uid);
		}
		return nextSequenceNumber;
	}

	public int getUserIDfromSequenceNumber(int sequenceNumber) {
		int uid = 0;
		synchronized(sequenceList) {
			uid = sequenceList.get(sequenceNumber);
		}
		return uid;
	}
}

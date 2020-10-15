package se.miun.distsys;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

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
import se.miun.distsys.messages.Message;
import se.miun.distsys.messages.MessageSerializer;

public class GroupCommuncation {
	
	private int datagramSocketPort = 7331;
	DatagramSocket datagramSocket = null;	
	boolean runGroupCommuncation = true;	
	MessageSerializer messageSerializer = new MessageSerializer();
	
	//Listeners
	ChatMessageListener chatMessageListener = null;	
	
	public GroupCommuncation() {			
		try {
			runGroupCommuncation = true;				
			datagramSocket = new MulticastSocket(datagramSocketPort);
			datagramSocket.setBroadcast(true);
			ReceiveThread rt = new ReceiveThread();
			rt.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void shutdown() {
		runGroupCommuncation = false;		
	}

	class ReceiveThread extends Thread {
		
		@Override
		public void run() {
			byte[] buffer = new byte[65536];		
			DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
			
			while(runGroupCommuncation) {
				try {
					datagramSocket.receive(datagramPacket);										
					byte[] packetData = datagramPacket.getData();					
					Message receivedMessage = messageSerializer.deserializeMessage(packetData);					
					handleMessage(receivedMessage);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
				
		private void handleMessage (Message message) {
			
			if(message instanceof ChatMessage) {				
				ChatMessage chatMessage = (ChatMessage) message;				
				if(chatMessageListener != null){
					chatMessageListener.onIncomingChatMessage(chatMessage);
				}
			} else if(message instanceof JoinMessage) {
				JoinMessage joinMessage = (JoinMessage) message;
				if(chatMessageListener != null) {
					chatMessageListener.onIncomingJoinMessage(joinMessage);
				}
			} else if(message instanceof LeaveMessage) {
				var leaveMessage = (LeaveMessage) message;
				if(chatMessageListener != null) {
					chatMessageListener.onIncomingLeaveMessage(leaveMessage);
				}
			} else if(message instanceof StatusMessage) {
				var statusMessage = (StatusMessage) message;
				if(chatMessageListener != null) {
					chatMessageListener.onIncomingStatusMessage(statusMessage);
				}
			} else if(message instanceof AssignMessage) {
				var assignMessage = (AssignMessage) message;
				if(chatMessageListener != null) {
					chatMessageListener.onIncomingAssignMessage(assignMessage);
				}
			} else if(message instanceof AuthMessage) {
				var authMessage = (AuthMessage) message;
				if(chatMessageListener != null) {
					chatMessageListener.onIncomingAuthMessage(authMessage);
				}
			} else if(message instanceof GenerateSequenceNumberMessage) {
				var generateSequenceNumberMessage = (GenerateSequenceNumberMessage) message;
				if(chatMessageListener != null) {
					chatMessageListener.onIncomingGenerateSequenceNumberMessage(generateSequenceNumberMessage);
				}
			} else if(message instanceof SequenceNumberMessage) {
				var sequenceNumberMessage = (SequenceNumberMessage) message;
				if(chatMessageListener != null) {
					chatMessageListener.onIncomingSequenceNumberMessage(sequenceNumberMessage);
				}
			} else if(message instanceof GetUserIDMessage) {
				var getUserIDMessage = (GetUserIDMessage) message;
				if(chatMessageListener != null) {
					chatMessageListener.onIncomingGetUserIDMessage(getUserIDMessage);
				}
			} else if(message instanceof UserIDMessage) {
				var userIDMessage = (UserIDMessage) message;
				if(chatMessageListener != null) {
					chatMessageListener.onIncomingUserIDMessage(userIDMessage);
				}
			} else if(message instanceof AliveMessage) {
				var aliveMessage = (AliveMessage) message;
				if(chatMessageListener != null) {
					chatMessageListener.onIncomingAliveMessage(aliveMessage);
				}
			} else if(message instanceof ElectionMessage) {
				var electionMessage = (ElectionMessage) message;
				if(chatMessageListener != null) {
					chatMessageListener.onIncomingElectionMessage(electionMessage);
				}
			} else if(message instanceof VictoryMessage) {
				var victoryMessage = (VictoryMessage) message;
				if(chatMessageListener != null) {
					chatMessageListener.onIncomingVictoryMessage(victoryMessage);
				}
			} else {				
				System.out.println("Unknown message type");
			}			
		}		
	}	
	
	public void sendChatMessage(ChatMessage chatMessage) {
		try {
			byte[] data = messageSerializer.serializeMessage(chatMessage);
			sendData(data);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public void sendJoinMessage(JoinMessage joinMessage) {
		try {
			byte[] data = messageSerializer.serializeMessage(joinMessage);
			sendData(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendLeaveMessage(LeaveMessage leaveMessage) {
		try {
			byte[] data = messageSerializer.serializeMessage(leaveMessage);
			sendData(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendStatusMessage(StatusMessage statusMessage) {
		try {
			byte[] data = messageSerializer.serializeMessage(statusMessage);
			sendData(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendAssignMessage(AssignMessage assignMessage) {
		try {
			byte[] data = messageSerializer.serializeMessage(assignMessage);
			sendData(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendAuthMessage(AuthMessage authMessage) {
		try {
			byte[] data = messageSerializer.serializeMessage(authMessage);
			sendData(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendGenerateSequenceNumberMessage(GenerateSequenceNumberMessage generateSequenceNumberMessage) {
		try {
			byte[] data = messageSerializer.serializeMessage(generateSequenceNumberMessage);
			sendData(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendSequenceNumberMessage(SequenceNumberMessage sequenceMessage) {
		try {
			byte[] data = messageSerializer.serializeMessage(sequenceMessage);
			sendData(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendGetUserIDMessage(GetUserIDMessage getUserIDMessage) {
		try {
			byte[] data = messageSerializer.serializeMessage(getUserIDMessage);
			sendData(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendUserIDMessage(UserIDMessage userIDMessage) {
		try {
			byte[] data = messageSerializer.serializeMessage(userIDMessage);
			sendData(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendAliveMessage(AliveMessage aliveMessage) {
		try {
			byte[] data = messageSerializer.serializeMessage(aliveMessage);
			sendData(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendElectionMessage(ElectionMessage electionMessage) {
		try {
			byte[] data = messageSerializer.serializeMessage(electionMessage);
			sendData(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendVictoryMessage(VictoryMessage victoryMessage) {
		try {
			byte[] data = messageSerializer.serializeMessage(victoryMessage);
			sendData(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendData(byte[] data) {
		try {
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName("255.255.255.255"), datagramSocketPort);
			datagramSocket.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setChatMessageListener(ChatMessageListener listener) {
		this.chatMessageListener = listener;		
	}
	
}

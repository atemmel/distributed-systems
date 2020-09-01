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

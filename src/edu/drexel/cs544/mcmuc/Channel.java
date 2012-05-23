package edu.drexel.cs544.mcmuc;

import java.net.DatagramPacket;

import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.actions.Message;

abstract class Channel {
	
	private MulticastChannel mcc;
		
	Channel(int port) {
		 mcc = new MulticastChannel(port);
	}
	
	public void send(Message m) {
		mcc.send(m);
	}
	
	public MulticastChannel getMulticastChannel(){
		return mcc;
	}

	public abstract void handleNewMessage(JSONObject jo);

	public void receive(DatagramPacket dp) {
		mcc.receive(dp);
	}

}

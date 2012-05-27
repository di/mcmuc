package edu.drexel.cs544.mcmuc;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.actions.Timeout;

/**
 * PrimaryTimer implements the following part of the protocol channel timeout algorithm:
 * All channels (except the control channel) follow the following timeout algorithm: 
 * Each client sets random timer for between 5-10 minutes after receiving a message 
 * on port N. When a client doesn't see any further traffic on port N before the timer 
 * expires, and it isn't in the room for Port N itself, it sends out a 'timeout' action
 * on the control channel.
 */
public class PrimaryTimer implements Runnable {

	int port;

	/**
	 * The primary timer is tied to a specific channel's port
	 * @param port in the port to apply the timeout algorithm to
	 */
    public PrimaryTimer(int port) {
        this.port = port;
    }

    public void run() {
    	Controller controller = Controller.getInstance();
		
		// if this client IS NOT in the room
    	if (controller.roomPortsInUse.contains(port)) {
    		List<Integer> lPort = new ArrayList<Integer>();
    		lPort.add(port);
    		
    		// create a timeout message
    		Timeout timeout = new Timeout(lPort);
    		JSONObject json = new JSONObject();
    		try {
    			json.put("action", "timeout");
    			json.put("uid", timeout.getUID());
    			json.put("rooms", lPort);
    		} catch (JSONException e) {
    			
    		}
    		// send a timeout message 
    		controller.send(json);
    		System.out.println("primary timout msg:\n"+json);
    	} 
    	// if this client IS in the room
    	else {
    		// do nothing
    	}
    }
}
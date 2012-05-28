package edu.drexel.cs544.mcmuc;

import java.util.Arrays;
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

    /**
     * When the primary timer for a room expires, and the client is not actively using the room,
     * send a Timeout action on the control channel for the room.
     */
    public void run() {
    	Controller controller = Controller.getInstance();

    	if (!controller.roomPortsInUse.contains(port)) {
    		Timeout timeout = new Timeout(Arrays.asList(port));
    		controller.send(timeout);
    	} 
    }
}
package edu.drexel.cs544.mcmuc.util;

import edu.drexel.cs544.mcmuc.channels.Controller;

/**
 * SecondaryTimer implements the following part of the protocol channel timeout algorithm:
 * Any client that see the 'timeout' action stops its own timer.
 * If any client responds to the 'timeout' poll before a timeout period of 60 seconds,
 * all clients keep port N open and reset their timers. If no clients respond, all 
 * clients shut down on port N.
 */
public class SecondaryTimer implements Runnable {

    int port;
    
	/**
	 * The secondary timer is tied to a specific channel's port
	 * @param port in the port to apply the timeout algorithm to
	 */
    public SecondaryTimer(int port) {
        this.port = port;
    }

    /**
     * If the secondary timer for a room expires, no client on the network is actively using it, 
     * so leave the room (freeing allocated resources)
     */
    public void run() {
    	Controller.getInstance().leaveRoom(port);
    }
}
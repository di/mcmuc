package edu.drexel.cs544.mcmuc;

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

    public void run() {
    	//TODO Remove the room on port through Controller.leaveRoom()
    }
}
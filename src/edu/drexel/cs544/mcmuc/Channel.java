package edu.drexel.cs544.mcmuc;

import java.net.DatagramPacket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.actions.Action;

/**
 * A channel is a a dynamic port that is actively being used for chat by one or more 
 * clients, and which all clients are aware they must forward traffic for.
 * 
 * All channels (except the control channel) follow the following timeout algorithm: 
 * Each client sets random timer for between 5-10 minutes after receiving a message 
 * on port N. When a client doesn't see any further traffic on port N before the timer 
 * expires, and it isn't in the room for Port N itself, it sends out a 'timeout' action
 * on the control channel. Any client that see the 'timeout' action stops its own timer.
 * If any client responds to the 'timeout' poll before a timeout period of 60 seconds,
 * all clients keep port N open and reset their timers. If no clients respond, all 
 * clients shut down on port N.
 */
public abstract class Channel {

    private MulticastChannel mcc;
    
	private PrimaryTimer primary;
	private SecondaryTimer secondary;
	
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
	
	private ScheduledFuture<?> primaryHandler;
	private ScheduledFuture<?> secondaryHandler;
	
	private static int maxDelay = 600;
	private static int minDelay = 300;
	private static int secondDelay = 60;

	/**
	 * Create a new multicast channel on a given port. If this not the control port, start
	 * the first timeout timer (random interval between 5 to 10 minutes).
	 * @param port int port to use
	 */
    Channel(int port) {
        mcc = new MulticastChannel(port);
        if(port != Controller.CONTROL_PORT)
        {
	        primary = new PrimaryTimer(port);
	        resetPrimaryTimer();
        }
    }

    public void send(Action a) {
        mcc.send(a);
    }

    public void send(JSONObject jo) {
        mcc.send(jo);
    }

    public abstract void handleNewMessage(JSONObject jo);

    public void receive(DatagramPacket dp) {
        mcc.receive(dp);
    }

    public int getPort() {
        return mcc.multicastPort;
    }
    
    /**
     * If the primary timer is already running, stop it. Then, set the timer for a
     * random interval between 5 to 10 minutes.
     */
    public void resetPrimaryTimer()
    {
    	if(getPort() != Controller.CONTROL_PORT)
    	{
    		int delay = minDelay + (int)(Math.random() * ((maxDelay - minDelay) + 1));
    		if(primaryHandler != null)
    			primaryHandler.cancel(true);
    		primaryHandler = scheduler.schedule(primary, delay, TimeUnit.SECONDS);
    	}
    }
    
    /**
     * If the secondary timer is already running, stop it. Then, set the timer for
     * 60 seconds.
     */
    public void resetSecondaryTimer()
    {
    	if(getPort() != Controller.CONTROL_PORT)
    	{
    		if(secondaryHandler != null)
    			secondaryHandler.cancel(true);
    		secondaryHandler = scheduler.schedule(secondary, secondDelay, TimeUnit.SECONDS);
    	}
    }
}
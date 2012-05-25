package edu.drexel.cs544.mcmuc;

import java.net.DatagramPacket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.actions.Action;

public abstract class Channel {

    private MulticastChannel mcc;
    
	private PrimaryTimer primary;
	private SecondaryTimer secondary;
	
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
	
	@SuppressWarnings("unused")
	private ScheduledFuture<?> primaryHandler;
	@SuppressWarnings("unused")
	private ScheduledFuture<?> secondaryHandler;
	
	private static int maxDelay = 10;
	private static int minDelay = 5;
	private static int secondDelay = 1;

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
    
    public void resetPrimaryTimer()
    {
    	if(getPort() != Controller.CONTROL_PORT)
    	{
    		int delay = minDelay + (int)(Math.random() * ((maxDelay - minDelay) + 1));
    		primaryHandler = scheduler.schedule(primary, delay, TimeUnit.MINUTES);
    	}
    }
    
    public void resetSecondaryTimer()
    {
    	if(getPort() != Controller.CONTROL_PORT)
    		secondaryHandler = scheduler.schedule(secondary, secondDelay, TimeUnit.MINUTES);
    }
}
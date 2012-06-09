package edu.drexel.cs544.mcmuc.util;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import edu.drexel.cs544.mcmuc.actions.Timeout;
import edu.drexel.cs544.mcmuc.channels.Controller;

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
    private static int maxDelay = 60;
    private static int minDelay = 30;

    private SecondaryTimer secondary;

    private static int secondDelay = 60;
    private ScheduledFuture<?> handler;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    /**
     * The primary timer is tied to a specific channel's port
     * 
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
        Timeout timeout = new Timeout(Arrays.asList(port));
        controller.send(timeout);
        startSecondaryTimer();
    }

    /**
     * Cancel pending timers, and start a new PrimaryTimer
     */
    public void reset() {
        int delay = minDelay + (int) (Math.random() * ((maxDelay - minDelay) + 1));
        if (handler != null) {
            handler.cancel(true);
        }
        handler = scheduler.schedule(this, delay, TimeUnit.SECONDS);
    }

    /**
     * If the secondary timer is already running, stop it. Then, set the timer for
     * 60 seconds.
     */
    public void startSecondaryTimer() {
        secondary = new SecondaryTimer(this.port);
        if (handler != null) {
            handler.cancel(true);
        }
        handler = scheduler.schedule(secondary, secondDelay, TimeUnit.SECONDS);
    }

    /**
     * Cancel all pending timers
     */
    public void cancelAll() {
        handler.cancel(true);
    }
}
package edu.drexel.cs544.mcmuc.channels;

import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.util.PrimaryTimer;

/**
 * Forwarder is a channel for the client simply forwards messages to other clients on
 * the network - that is, they are not actively participating in the room on that channel
 */
public class Forwarder extends Channel {

    private PrimaryTimer primary;

    /**
     * Creates a channel on the given port and starts the multicast thread for the room
     * 
     * @param port int port to forward traffic on
     */
    Forwarder(int port) {
        super(port);
        primary = new PrimaryTimer(port);
        resetPrimaryTimer();
    }

    /**
     * If the message is not a duplicate, forward it to the other clients
     */
    @Override
    public void handleNewMessage(JSONObject jo) {
        this.resetPrimaryTimer();
        this.send(jo);
    }

    /**
     * If the primary timer is already running, stop it. Then, set the timer for a
     * random interval between 5 to 10 minutes.
     */
    public void resetPrimaryTimer() {
        primary.reset();
    }

    /**
     * Shutdown the Forwarder. Cancel all pending timers.
     */
    public void shutdown() {
        primary.cancelAll();
        super.mcc.close();
        super.runner.stop();
    }
}
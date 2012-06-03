package edu.drexel.cs544.mcmuc.channels;

import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.util.MulticastReceiveRunnable;

/**
 * Forwarder is a channel for the client simply forwards messages to other clients on
 * the network - that is, they are not actively participating in the room on that channel
 */
public class Forwarder extends Channel {

    /**
     * Creates a channel on the given port and starts the multicast thread for the room
     * 
     * @param port int port to forward traffic on
     */
    Forwarder(int port) {
        super(port);
        MulticastReceiveRunnable runner = new MulticastReceiveRunnable(this);
        Thread runnerThread = new Thread(runner);
        runnerThread.start();
    }

    /**
     * If the message is not a duplicate, forward it to the other clients
     */
    @Override
    public void handleNewMessage(JSONObject jo) {
        this.send(jo);
    }
}
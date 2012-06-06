package edu.drexel.cs544.mcmuc.channels;

import java.net.DatagramPacket;

import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.actions.Action;
import edu.drexel.cs544.mcmuc.util.MulticastChannel;
import edu.drexel.cs544.mcmuc.util.MulticastReceiveRunnable;

/**
 * A channel is a dynamic port that is actively being used for chat by one or more
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
    private MulticastReceiveRunnable runner;

    /**
     * Create a new multicast channel on a given port. If this not the control port, start
     * the first timeout timer (random interval between 5 to 10 minutes).
     * 
     * @param port int port to use
     */
    Channel(int port) {
        mcc = new MulticastChannel(port);
        runner = new MulticastReceiveRunnable(this);
        Thread runnerThread = new Thread(runner);
        runnerThread.start();
    }

    /**
     * Passes the action to the multicast channel for sending
     * 
     * @param a Action action to send
     * @see MulticastChannel
     */
    public void send(Action a) {
        mcc.send(a);
    }

    /**
     * Passes the action to the multicast channel for sending
     * 
     * @param jo a JSON serialization of an action to send
     * @see MulticastChannel
     */
    public void send(JSONObject jo) {
        mcc.send(jo);
    }

    /**
     * Coordinates the processing of a newly received action (serialized as JSON).
     * Implementation is left to the child classes, who will determine the action type
     * and typically let that action handle its own processing
     * 
     * @param jo a JSON serialization of a newly received action
     * @see Action
     * @see Controller
     * @see Room
     */
    public abstract void handleNewMessage(JSONObject jo);

    /**
     * Passes the datagram packet to the multicast channel for receival
     * 
     * @param dp a datagram packet
     * @see MulticastChannel
     */
    public void receive(DatagramPacket dp) {
        mcc.receive(dp);
    }

    /**
     * Gets the port used by the underlying multicast channel
     * 
     * @return int the multicast port
     */
    public int getPort() {
        return mcc.getPort();
    }

    /**
     * Stop the multicast thread
     */
    public void shutdown() {
        Controller.getInstance().alert("Shutting down Channel [" + this.getPort() + "]");
        runner.stop();
    }
}
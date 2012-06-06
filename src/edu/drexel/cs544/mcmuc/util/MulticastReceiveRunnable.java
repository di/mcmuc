package edu.drexel.cs544.mcmuc.util;

import java.net.DatagramPacket;

import org.json.JSONException;
import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.channels.Channel;

/**
 * MulticastReceiveRunnable (MRR) implements Runnable so that any instances can be executed by a thread.
 * A given MRR is tied to a specific channel and executes the code in run() while the thread
 * is active. MRR receives datagrams on the channel and gets the JSON data payload. MRR records
 * the received JSON in the duplicate detector and passes it to the channel for further handling.
 * 
 * @see DuplicateDetector
 * @see Channel
 */
public class MulticastReceiveRunnable implements Runnable {

    Channel channel;
    Boolean done;

    /**
     * The only necessary parameter to create a MulticastReceiveRunnable object is the channel
     * 
     * @param channel Channel to receive data on
     */
    public MulticastReceiveRunnable(Channel channel) {
        this.channel = channel;
    }

    /**
     * Sets the done flag to true
     */
    public void stop() {
        this.done = true;
        System.out.println("Shutting down thread!");
    }

    /**
     * MRR receives datagrams on the channel and gets the JSON data payload.
     * MRR records the received JSON in the duplicate detector and passes it to
     * the channel for further handling. The length of the datagram packet cannot
     * exceed 1000 bytes.
     */
    public void run() {
        this.done = false;
        while (!this.done) {
            byte[] buf = new byte[65507];
            DatagramPacket dp = new DatagramPacket(buf, buf.length);
            channel.receive(dp);
            String s = new String(dp.getData(), 0, dp.getLength());
            JSONObject jo = null;
            try {
                jo = new JSONObject(s);
            } catch (JSONException e) {
                System.err.println("Got bad JSON data. Contents:\n" + s);
                e.printStackTrace();
            }

            if (DuplicateDetector.getInstance().add(jo)) {
                channel.handleNewMessage(jo);
            } else {
                // Duplicate message
            }
        }
        System.out.println("Thread has been shut down!");
    }
}
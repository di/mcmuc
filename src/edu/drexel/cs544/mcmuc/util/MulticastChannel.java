package edu.drexel.cs544.mcmuc.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.actions.Action;

/**
 * A MulticastChannel is the combination of a multicast socket, address, and port that together
 * represent a multicast group. Multicast is the delivery of a message to a group of associated
 * clients at same time, using only a single transmission at the source.
 */
public class MulticastChannel {

    MulticastSocket multicastSocket;
    InetAddress multicastAddress;
    int multicastPort;

    /**
     * Joins the multicast group using the address 224.5.4.4 and the given port. An error is
     * displayed if the multicast route does not exist (and remediation steps are displayed).
     * 
     * @param port int port of multicast group to join on 224.5.4.4
     */
    public MulticastChannel(int port) {
        try {
            this.multicastPort = port;
            this.multicastSocket = new MulticastSocket(this.multicastPort);
            this.multicastAddress = InetAddress.getByName("224.5.4.4");
            this.multicastSocket.joinGroup(multicastAddress);
        } catch (SocketException e) {
            System.err.println("Error: Multicast route likely does not exist. Add using (for example):\n" + "\t$ ip route add 224.0.0.0/4 dev eth0");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a JSON payload to the joined multicast group, recording it with the duplicate detector.
     * 
     * @param jo JSON to send to multicast group
     * @see DuplicateDetector
     */
    public void send(JSONObject jo) {
        // ActionBase a = new ActionBase(jo);
        DuplicateDetector.getInstance().add(jo);
        String msg = jo.toString();
        // System.out.println("Sending: " + a.getUID());
        DatagramPacket dp = new DatagramPacket(msg.getBytes(), msg.length(), this.multicastAddress, this.multicastPort);
        try {
            this.multicastSocket.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends an Action payload to the joined multicast group (serializing to JSON first),
     * recording it with the duplicate detector.
     * 
     * @param action Action to send to multicast group
     * @see DuplicateDetector
     */
    public void send(Action action) {
        DuplicateDetector.getInstance().add(action.toJSON());
        String msg = action.toJSON().toString();
        // System.out.println("Sending: " + action.getUID());
        // System.out.println("\tJSON: " + action.toJSON().toString());

        DatagramPacket dp = new DatagramPacket(msg.getBytes(), msg.length(), this.multicastAddress, this.multicastPort);
        try {
            this.multicastSocket.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the multicast socket in use
     * 
     * @return MulticastSocket in use
     */
    public MulticastSocket getMulticastSocket() {
        return this.multicastSocket;
    }

    /**
     * Upon receipt of a datagram packet, passes the packet to the instance of MulticastSocket
     * for processing
     * 
     * @param dp DatagramPacket received
     * @see MulticastSocket
     */
    public void receive(DatagramPacket dp) {
        try {
            multicastSocket.receive(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return this.multicastPort;
    }
}

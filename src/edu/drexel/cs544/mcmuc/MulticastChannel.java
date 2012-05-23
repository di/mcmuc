package edu.drexel.cs544.mcmuc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import org.json.JSONException;
import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.actions.Action;

public class MulticastChannel {

    MulticastSocket multicastSocket;
    InetAddress multicastAddress;
    int multicastPort;

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

    public void send(JSONObject jo) {
        DuplicateDetector.getInstance().add(getUID(jo));
        String msg = jo.toString();
        System.out.println("Sending: " + getUID(jo));
        DatagramPacket dp = new DatagramPacket(msg.getBytes(), msg.length(), this.multicastAddress, this.multicastPort);
        try {
            this.multicastSocket.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Action action) {
        DuplicateDetector.getInstance().add(action.getUID());
        String msg = action.toJSON().toString();
        System.out.println("Sending: " + action.getUID());
        DatagramPacket dp = new DatagramPacket(msg.getBytes(), msg.length(), this.multicastAddress, this.multicastPort);
        try {
            this.multicastSocket.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MulticastSocket getMulticastSocket() {
        return this.multicastSocket;
    }

    public void receive(DatagramPacket dp) {
        try {
            multicastSocket.receive(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Probably shouldn't be here
    private String getUID(JSONObject jo) {
        try {
            return jo.getString("uid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}

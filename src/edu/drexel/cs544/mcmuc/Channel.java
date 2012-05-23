package edu.drexel.cs544.mcmuc;

import java.net.DatagramPacket;

import org.json.JSONException;
import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.actions.Action;

public abstract class Channel {

    private MulticastChannel mcc;

    Channel(int port) {
        mcc = new MulticastChannel(port);
    }

    public void send(Action a) {
        mcc.send(a);
    }

    public void send(DatagramPacket dp) {
        mcc.send(dp);
    }

    public abstract void handleNewMessage(DatagramPacket dp);

    public void receive(DatagramPacket dp) {
        mcc.receive(dp);
    }

    public int getPort() {
        return mcc.multicastPort;
    }

    public JSONObject datagramToJSONObject(DatagramPacket dp) {
        String s = new String(dp.getData(), 0, dp.getLength());
        JSONObject jo = null;
        try {
            jo = new JSONObject(s);
        } catch (JSONException e) {
            System.err.println("Got bad JSON data. Contents:\n" + s);
            e.printStackTrace();
        }
        return jo;
    }

}

package edu.drexel.cs544.mcmuc;

import java.net.DatagramPacket;

import org.json.JSONException;
import org.json.JSONObject;

public class MulticastReceiveRunnable implements Runnable {

    Channel channel;

    public MulticastReceiveRunnable(Channel channel) {
        this.channel = channel;
    }

    public void run() {
        while (true) {
            byte[] buf = new byte[1000];
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
            if (DuplicateDetector.getInstance().add(getUID(jo))) {
                channel.handleNewMessage(jo);
            } else {
                // Duplicate message
            }
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
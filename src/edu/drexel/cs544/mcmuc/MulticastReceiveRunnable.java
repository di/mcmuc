package edu.drexel.cs544.mcmuc;

import java.net.DatagramPacket;

import org.json.JSONException;
import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.actions.Action;
import edu.drexel.cs544.mcmuc.actions.ActionBase;

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
            ActionBase a = new ActionBase(jo);
            
            if (DuplicateDetector.getInstance().add(a.getUID())) {
                channel.handleNewMessage(jo);
            } else {
                // Duplicate message
            }
        }
    }
}
package edu.drexel.cs544.mcmuc;

import java.io.IOException;
import java.net.DatagramPacket;

import org.json.JSONException;
import org.json.JSONObject;

public class MulticastReceiveRunnable implements Runnable {

    Room room;

    public MulticastReceiveRunnable(Room room) {
        this.room = room;
    }

    public void run() {
        while (true) {
            byte[] buf = new byte[1000];
            DatagramPacket dp = new DatagramPacket(buf, buf.length);
            try {
                room.getMulticastSocket().receive(dp);
                String s = new String(dp.getData(), 0, dp.getLength());
                JSONObject jo = null;
            	try {
        			jo = new JSONObject(s);
        		} catch (JSONException e) {
        			System.err.println("Got bad string in MUC. Contents:\n" + s);
        			e.printStackTrace();
        		}
        		if (jo != null) {
        			room.receive(jo);
        		}
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}

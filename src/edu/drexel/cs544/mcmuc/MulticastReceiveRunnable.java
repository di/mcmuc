package edu.drexel.cs544.mcmuc;

import java.io.IOException;
import java.net.DatagramPacket;

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
                room.receive(s);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}

package edu.drexel.cs544.mcmuc;

import java.net.DatagramPacket;

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
            channel.handleNewMessage(dp);
        }
    }
}
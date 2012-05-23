package edu.drexel.cs544.mcmuc;

import java.net.DatagramPacket;

public class Forwarder extends Channel {

    Forwarder(int port) {
        super(port);
        MulticastReceiveRunnable runner = new MulticastReceiveRunnable(this);
        Thread runnerThread = new Thread(runner);
        runnerThread.start();
    }

    @Override
    public void handleNewMessage(DatagramPacket dp) {
        this.send(dp);
    }
}
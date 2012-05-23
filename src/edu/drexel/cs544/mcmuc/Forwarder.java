package edu.drexel.cs544.mcmuc;

import org.json.JSONObject;

public class Forwarder extends Channel {

    Forwarder(int port) {
        super(port);
        MulticastReceiveRunnable runner = new MulticastReceiveRunnable(this);
        Thread runnerThread = new Thread(runner);
        runnerThread.start();
    }

    @Override
    public void handleNewMessage(JSONObject jo) {
        this.send(jo);
    }
}
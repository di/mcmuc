package edu.drexel.cs544.mcmuc;

import java.net.DatagramPacket;

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

    public void send(JSONObject jo) {
        mcc.send(jo);
    }

    public abstract void handleNewMessage(JSONObject jo);

    public void receive(DatagramPacket dp) {
        mcc.receive(dp);
    }

    public int getPort() {
        return mcc.multicastPort;
    }
}
package edu.drexel.cs544.mcmuc.actions;

import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.channels.Channel;
import edu.drexel.cs544.mcmuc.channels.Controller;

/**
 * The preserve action is used to reply to a timeout action, indicating that
 * clients should continue to forward traffic for the included rooms.
 * 
 * The JSON format of a Preserve is {'uid':'<uid>','action':'preserve','rooms':'<rooms>'}
 */
public class Preserve extends RoomAction {
    public static final String action = "preserve";

    /**
     * The preserve action must carry a list of rooms that clients should
     * send as a response to a timeout action
     * 
     * @param rooms List<Integer> the list of the rooms
     */
    public Preserve(List<Integer> rooms) {
        super(rooms, Preserve.action);
    }

    /**
     * Deserializes JSON into a Preserve object
     * 
     * @param json the JSON to deserialize
     */
    public Preserve(JSONObject json) {
        super(json, Preserve.action);
    }

    /**
     * For each room in the Preserve action, reset the primary timer
     */
    @Override
    public void process(Channel channel) {
        Controller.getInstance().alert("Got a Preserve message");
        class Runner implements Runnable {
            Preserve message;
            Channel channel;

            Runner(Preserve m, Channel c) {
                channel = c;
                message = m;
            }

            public void run() {
                Iterator<Integer> it = message.getRooms().iterator();
                while (it.hasNext())
                    Controller.getInstance().resetPrimaryTimer(it.next());
                channel.send(message);
            }
        }
        Thread t = new Thread(new Runner(this, channel));
        t.start();
    }
}
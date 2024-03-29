package edu.drexel.cs544.mcmuc.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.channels.Channel;
import edu.drexel.cs544.mcmuc.channels.Controller;

/**
 * The timeout action is used by clients to find channels clients are no longer
 * actively using to chat, and thus whose allocated resources can be freed.
 * The timeout is used to ask the other clients which of the provided rooms they
 * are using.
 * 
 * The JSON format of a Timeout is {'uid':'<uid>','action':'timeout','rooms':'<rooms>'}
 * 
 */
public class Timeout extends RoomAction {
    public static final String action = "timeout";

    /**
     * The timeout action must carry a list of rooms that receiving clients may
     * reply with a preserve action to indicate continuing use of the room
     * 
     * @param rooms List<Integer> the list of the rooms
     */
    public Timeout(List<Integer> rooms) {
        super(rooms, Timeout.action);
    }

    /**
     * Deserializes JSON into a Timeout object
     * 
     * @param json the JSON to deserialize
     */
    public Timeout(JSONObject json) {
        super(json, Timeout.action);
    }

    /**
     * Upon receiving a Timeout action on the control channel, respond with a Preserve action
     * containing the intersection of the rooms in use and the rooms queried about in the
     * Timeout action.
     * 
     * For each room listed in the Timeout action, if the client is in the room, reset the
     * primary timer; else, stop the primary timer and start the secondary timer.
     */
    @Override
    public void process(Channel channel) {
        class Runner implements Runnable {
            Timeout message;

            Runner(Timeout m) {
                message = m;
            }

            public void run() {
                Collection<Integer> roomsInUse = Controller.getInstance().getRoomsInUse(message.getRooms());

                if (!roomsInUse.isEmpty()) {
                    Preserve reply = new Preserve(new ArrayList<Integer>(roomsInUse));
                    Controller.getInstance().send(reply);
                } else {
                    Controller.getInstance().send(message);
                }
            }
        }
        Thread t = new Thread(new Runner(this));
        t.start();
    }
}

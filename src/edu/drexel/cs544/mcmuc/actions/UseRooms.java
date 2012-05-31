package edu.drexel.cs544.mcmuc.actions;

import java.util.List;

import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.Channel;
import edu.drexel.cs544.mcmuc.Controller;

/**
 * The use-rooms action is used to reply to a list-rooms action. Clients will either
 * reply with a collection of all channels they know are in use or the subset of
 * channels in use that were asked about in the original list-rooms action. In
 * addition, use-rooms is also used to create a new room.
 * 
 * The JSON format of a UseRooms is {'uid':'<uid>','action':'use-rooms','rooms':'<rooms>'}
 */
public class UseRooms extends RoomAction {

    public static final String action = "use-rooms";

    /**
     * The use-rooms action must carry a list of rooms that receiving clients should
     * begin or continue forwarding traffic for.
     * 
     * @param rooms List<Integer> the list of the rooms
     */
    public UseRooms(List<Integer> rooms) {
        super(rooms, UseRooms.action);
    }

    /**
     * Deserializes JSON into a UseRooms object
     * 
     * @param json the JSON to deserialize
     */
    public UseRooms(JSONObject json) {
        super(json, UseRooms.action);
    }

    /**
     * Upon receiving a UseRooms action, iterate through the list of rooms, and pass the port
     * to Controller to reserve resources for the room.
     * 
     * @see Controller
     */
    @Override
    public void process(Channel channel) {
        class Runner implements Runnable {
            UseRooms message;

            Runner(UseRooms m) {
                message = m;
            }

            public void run() {
                Controller controller = Controller.getInstance();
                for (int room : message.getRooms()) {
                    controller.useRoom(room);
                }
            }
        }
        Thread t = new Thread(new Runner(this));
        t.start();
    }
}
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

    public UseRooms(List<Integer> rooms) {
        super(rooms, UseRooms.action);
    }

    public UseRooms(JSONObject json) {
        super(json, UseRooms.action);
    }

    @Override
    public void process(Channel channel) {
        Controller controller = Controller.getInstance();
        for (int room : this.getRooms()) {
            controller.useRoom(room);
        }
    }
}
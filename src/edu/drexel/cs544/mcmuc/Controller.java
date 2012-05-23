package edu.drexel.cs544.mcmuc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.actions.Action;

public class Controller extends Channel {

    public static final int CONTROL_PORT = 31941;
    public Set<Integer> portsInUse = Collections.synchronizedSet(new TreeSet<Integer>());
    private final Map<String, Room> rooms = Collections.synchronizedMap(new HashMap<String, Room>());

    Controller(int port) {
        super(port);
        portsInUse.add(CONTROL_PORT);
    }

    private static final Controller instance = new Controller(CONTROL_PORT);

    public static Controller getInstance() {
        return instance;
    }

    @Override
    public void handleNewMessage(JSONObject jo) {
        // TODO Auto-generated method stub

    }

    public void display(String displayString) {
        System.out.println(displayString);
    }

    public void useRoom(String roomName) {
        Room room = new Room(roomName, portsInUse);
        portsInUse.add(room.getPort());
        rooms.put(roomName, room);
    }

    public void sendToRoom(String roomName, Action action) {
        Room room = rooms.get(roomName);
        if (room != null) {
            room.send(action);
        } else {
            System.err.println("Room not found!");
        }
    }
}
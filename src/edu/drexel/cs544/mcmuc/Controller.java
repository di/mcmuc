package edu.drexel.cs544.mcmuc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.actions.Action;
import edu.drexel.cs544.mcmuc.actions.ListRooms;
import edu.drexel.cs544.mcmuc.actions.Presence.Status;
import edu.drexel.cs544.mcmuc.actions.Preserve;
import edu.drexel.cs544.mcmuc.actions.Timeout;
import edu.drexel.cs544.mcmuc.actions.UseRooms;

public class Controller extends Channel {

    public static final int CONTROL_PORT = 31941;
    public Set<Integer> portsInUse = Collections.synchronizedSet(new TreeSet<Integer>());
    public Set<Integer> roomPortsInUse = Collections.synchronizedSet(new TreeSet<Integer>());
    private final Map<String, Room> rooms = Collections.synchronizedMap(new HashMap<String, Room>());
    private final Map<Integer, Forwarder> forwards = Collections.synchronizedMap(new HashMap<Integer, Forwarder>());

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
        Action action;
        String actionString = "";
        try {
            actionString = jo.getString("action");
        } catch (JSONException e) {
            System.err.println("Message does not have action.");
            e.printStackTrace();
        }
        if (actionString.equalsIgnoreCase(UseRooms.action)) {
            action = new UseRooms(jo);
            action.process(this);
        } else if(actionString.equalsIgnoreCase(ListRooms.action)) {
        	action = new ListRooms(jo);
        	action.process(this);
        } else if(actionString.equalsIgnoreCase(Timeout.action)) {
        	action = new Timeout(jo);
        	action.process(this);
        } else if(actionString.equalsIgnoreCase(Preserve.action)) {
        	action = new Preserve(jo);
        	action.process(this);
        } else {
        
            System.err.println("Message action type not supported: " + actionString);
        }
    }

    public void display(String displayString) {
        System.out.println(displayString);
    }

    public void useRoom(int roomPort) {
        if (!portsInUse.contains(roomPort)) {
            Forwarder fwd = new Forwarder(roomPort);
            portsInUse.add(roomPort);
            forwards.put(roomPort, fwd);
        } else {
            // This port is already in use, either by a Room or Forwarder
        }
    }
    
    public void leaveRoom(String roomName)
    {
    	Room r = rooms.remove(roomName);
    	roomPortsInUse.remove(r.getPort());
    }

    public void useRoom(String roomName, String userName) {
        Room room = new Room(roomName, portsInUse, userName);
        portsInUse.add(room.getPort());
        rooms.put(roomName, room);
        roomPortsInUse.add(room.getPort());
    }
    
    public void setRoomStatus(String roomName, Status presence)
    {
        Room room = rooms.get(roomName);
        if (room != null) {
            room.setStatus(presence);
        } else {
            System.err.println("Room not found!");
        }
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
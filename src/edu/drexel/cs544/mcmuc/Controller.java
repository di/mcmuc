package edu.drexel.cs544.mcmuc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.actions.Action;
import edu.drexel.cs544.mcmuc.actions.ListRooms;
import edu.drexel.cs544.mcmuc.actions.Presence.Status;
import edu.drexel.cs544.mcmuc.actions.Preserve;
import edu.drexel.cs544.mcmuc.actions.Timeout;
import edu.drexel.cs544.mcmuc.actions.UseRooms;

/**
 * Controller represents a control channel, which is a fixed port for the sending of
 * messages, such as room creation and detection, that must reach all clients on the network.
 * Controller exposes two sets of ports - all ports in use, and the ports in use that for which
 * the client is actively using the room.
 */
public class Controller extends Channel {

    public static final int CONTROL_PORT = 31941;
    public final Map<String, Integer> roomNames = Collections.synchronizedMap(new HashMap<String, Integer>());
    public final Map<Integer, Channel> channels = Collections.synchronizedMap(new HashMap<Integer, Channel>());
    private UI ui;

    /**
     * Creates the controller channel on the given port and adds it to the set of Channels
     * 
     * @param port int port to use (will always be CONTROL_PORT)
     */
    private Controller(int port) {
        super(port);
        channels.put(port, this);
    }

    private static final Controller instance = new Controller(CONTROL_PORT);

    /**
     * Restricts the instantiation of the Controller class to one object (the Singleton
     * design pattern)
     * 
     * @return Controller singleton
     */
    public static Controller getInstance() {
        return instance;
    }

    /**
     * Supports the processing of UseRooms, ListRooms, Timeout, and Preserve actions. Checks
     * the action key of the JSON received and uses that to pass processing to the
     * correct type. Errors are display if the message does not have an action or the
     * type is not supported (not list-rooms, use-rooms, timeout, and preserve).
     */
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
        } else if (actionString.equalsIgnoreCase(ListRooms.action)) {
            action = new ListRooms(jo);
            action.process(this);
        } else if (actionString.equalsIgnoreCase(Timeout.action)) {
            action = new Timeout(jo);
            action.process(this);
        } else if (actionString.equalsIgnoreCase(Preserve.action)) {
            action = new Preserve(jo);
            action.process(this);
        } else {
            System.err.println("Message action type not supported: " + actionString);
        }
    }

    /**
     * Takes the given String and outputs it, either through a UI or System.out
     * 
     * @param outputString String to display
     */
    public void output(String outputString) {
        if (this.ui != null) {
            this.ui.output(outputString);
        } else {
            // Poor man's UI
            System.out.println(outputString);
        }
    }

    /**
     * Resets the primary timer for the Room on roomPort
     * 
     * @param roomPort int port of room to reset primary timer for
     */
    public void resetPrimaryTimer(int roomPort) {
        channels.get(roomPort).resetPrimaryTimer();
    }

    /**
     * Stops the primary timer for the Room on roomPort
     * 
     * @param roomPort int port of room to stop primary timer for
     */
    public void stopPrimaryTimer(int roomPort) {
        channels.get(roomPort).stopPrimaryTimer();
    }

    /**
     * Starts the secondary timer for the Room on roomPort
     * 
     * @param roomPort int port of room to start secondary timer for
     */
    public void startSecondaryTimer(int roomPort) {
        channels.get(roomPort).startSecondaryTimer();
    }

    /**
     * If the set of Channels does not contain roomPort, create a new Forwarder on that port,
     * and add the port to the set of Channels.
     * 
     * @param roomPort int
     */
    public void useRoom(int roomPort) {
        if (!channels.keySet().contains(roomPort)) {
            Forwarder fwd = new Forwarder(roomPort);
            channels.put(roomPort, fwd);
        } else {
            // This port is already in use, either by a Room or Forwarder
        }
    }

    /**
     * If the set of Channels contains roomPort, remove it. If the channel is a room, set the
     * client's presence to Offline.
     * 
     * @param roomPort int port to leave
     */
    public void leaveRoom(int roomPort) {
        Channel c = channels.remove(roomPort);
        if (c != null && c instanceof Room) {
            Room r = (Room) c;
            r.setStatus(Status.Offline);
        }
    }

    /**
     * Remove the room that corresponds to the name given by roomName, set that client's status
     * to Offline, and remove the port from the set of Channels.
     * 
     * @param roomName String name of room to leave
     */
    public void leaveRoom(String roomName) {
        Integer p = roomNames.remove(roomName);
        if (p != null) {
            leaveRoom(p);
        }
    }

    /**
     * Create a new room on the port chosen by the hash algorithm given the roomName and portsInUse.
     * Set the user's name in the room to userName. Add the room and add the port to the set of Channels.
     * 
     * @param roomName String name of room to use
     * @param userName String name to associate with user in the room
     */
    public void useRoom(String roomName, String userName) {
        Room room = new Room(roomName, channels.keySet(), userName);
        channels.put(room.getPort(), room);
        roomNames.put(roomName, room.getPort());
        this.send(new UseRooms(Arrays.asList(room.getPort())));
    }
    
    /**
     * Gets the user's name in a given room (identified by the room name)
     * @param roomName String room to return the user name for
     * @return String the user's name in that room
     */
    public String getUserName(String roomName)
    {
    	Room room = (Room) channels.get(roomNames.get(roomName));
        if (room != null) {
            return room.getUserName();
        } else {
            System.err.println("Room not found!");
            return "";
        }
    }

    /**
     * Set that user's presence in the room associated with roomName to the given presence
     * 
     * @param roomName String name of room to set presence for
     * @param presence Presence to set
     * @see Status
     */
    public void setRoomStatus(String roomName, Status presence) {
        Room room = (Room) channels.get(roomNames.get(roomName));
        if (room != null) {
            room.setStatus(presence);
        } else {
            System.err.println("Room not found!");
        }
    }

    /**
     * Send the given action to the room associated with roomName
     * 
     * @param roomName String name of room to send action to
     * @param action Action to send
     */
    public void sendToRoom(String roomName, Action action) {
        Room room = (Room) channels.get(roomNames.get(roomName));
        if (room != null) {
            room.send(action);
        } else {
            System.err.println("Room not found!");
        }
    }

    public void setUI(UI ui) {
        this.ui = ui;
    }
}
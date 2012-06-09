package edu.drexel.cs544.mcmuc.channels;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.actions.Action;
import edu.drexel.cs544.mcmuc.actions.ListRooms;
import edu.drexel.cs544.mcmuc.actions.Message;
import edu.drexel.cs544.mcmuc.actions.PollPresence;
import edu.drexel.cs544.mcmuc.actions.Presence;
import edu.drexel.cs544.mcmuc.actions.Preserve;
import edu.drexel.cs544.mcmuc.actions.Timeout;
import edu.drexel.cs544.mcmuc.actions.UseRooms;
import edu.drexel.cs544.mcmuc.actions.Presence.Status;
import edu.drexel.cs544.mcmuc.ui.UI;
import edu.drexel.cs544.mcmuc.util.Certificate;
import edu.drexel.cs544.mcmuc.util.RoomDoesNotExistException;

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
        this.send(new ListRooms());
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
    public void output(String string) {
        if (this.ui != null) {
            this.ui.output(string);
        } else {
            // Poor man's UI
            System.out.println(string);
        }
    }

    /**
     * Takes the given String and outputs it, either through a UI or System.out, prefixed with a '*'
     * 
     * @param string String to display
     */
    public void alert(String string) {
        if (this.ui != null) {
            this.ui.alert(string);
        } else {
            // Poor man's UI
            System.out.println("* " + string);
        }
    }

    public boolean hasForwarder(int port) {
        return (!roomNames.containsValue(port) && channels.containsKey(port));
    }

    /**
     * Resets the primary timer for the Room on roomPort
     * 
     * @param roomPort int port of room to reset primary timer for
     */
    public void resetPrimaryTimer(int roomPort) {
        if (hasForwarder(roomPort)) {
            ((Forwarder) channels.get(roomPort)).resetPrimaryTimer();
        }
    }

    /**
     * If the set of Channels does not contain roomPort, create a new Forwarder on that port,
     * and add the port to the set of Channels.
     * 
     * @param roomPort int
     */
    public synchronized void useRoom(int roomPort) {
        if (!channels.keySet().contains(roomPort) && roomPort != Controller.CONTROL_PORT) {
            startForwarder(roomPort);
        } else {
            // Do nothing, this port is already in use, either by a Room or Forwarder
        }
    }

    /**
     * Start a forwarder on the given port
     * 
     * @param roomPort the port
     */
    public void startForwarder(int roomPort) {
        Forwarder fwd = new Forwarder(roomPort);
        channels.put(roomPort, fwd);
        fwd.send(new PollPresence());
    }

    /**
     * Shutdown the forwarder on a given port
     * 
     * @param port the port
     */

    public void stopForwarder(int port) {
        Forwarder f = (Forwarder) channels.remove(port);
        f.shutdown();
    }

    /**
     * If the set of Channels contains roomPort, remove it. If the channel is a room, set the
     * client's presence to Offline. Then, start a Forwarder to replace the Room.
     * 
     * @param roomPort int port to leave
     */
    public void leaveRoom(int roomPort) {
        Room r = (Room) channels.remove(roomPort);
        r.setStatus(Status.Offline);
        r.shutdown();
        startForwarder(roomPort);
    }

    /**
     * Remove the room that corresponds to the name given by roomName, set that client's status
     * to Offline, and remove the port from the set of Channels.
     * 
     * @param roomName String name of room to leave
     * @throws RoomDoesNotExistException
     */
    public void leaveRoom(String roomName) throws RoomDoesNotExistException {
        Integer p = roomNames.remove(roomName);
        if (p != null) {
            leaveRoom(p);
            this.alert("Left room: \"" + roomName + "\"");
        } else {
            throw new RoomDoesNotExistException(roomName);
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
        Room room;
        if (roomNames.containsKey(roomName)) {
            room = (Room) channels.get(roomNames.get(roomName));
            if (room.getUserName().equals(userName)) {
                this.alert("Room \"" + roomName + "\" already exists with user \"" + userName + "\"");
            } else {
                room.setStatus(Presence.Status.Offline);
                room.setUserName(userName);
                room.setStatus(Presence.Status.Online);
                this.alert("Room \"" + roomName + "\" already exists, updating user to \"" + userName + "\"");
            }
        } else {
            room = new Room(roomName, new HashSet<Integer>(roomNames.values()), userName);
            Forwarder oldChannel = (Forwarder) channels.put(room.getPort(), room);
            if (oldChannel != null) {
                oldChannel.shutdown();
            }
            roomNames.put(roomName, room.getPort());
            this.send(new UseRooms(Arrays.asList(room.getPort())));
            this.alert("Created new room: \"" + roomName + "\" with user \"" + userName + "\"");
        }
    }

    /**
     * Gets the user's name in a given room (identified by the room name)
     * 
     * @param roomName String room to return the user name for
     * @return String the user's name in that room
     */
    public String getUserName(String roomName) {
        Room room = (Room) channels.get(roomNames.get(roomName));
        if (room != null) {
            return room.getUserName();
        } else {
            return "";
        }
    }

    /**
     * Set that user's presence in the room associated with roomName to the given presence
     * 
     * @param roomName String name of room to set presence for
     * @param presence Presence to set
     * @throws RoomDoesNotExistException
     * @see Status
     */
    public void setRoomStatus(String roomName, Status presence) throws RoomDoesNotExistException {
        Room room = (Room) channels.get(roomNames.get(roomName));
        if (room != null) {
            room.setStatus(presence);
            this.alert("Set presence for \"" + room.getUserName() + "@" + roomName + "\" to \"" + presence.toString().toLowerCase() + "\"");
        } else {
            throw new RoomDoesNotExistException(roomName);
        }
    }

    /**
     * Adds a public/private key pair to the room identified by roomName
     * 
     * @param roomName String the room
     * @param publicKey Certificate public key
     * @param privateKey Certificate private key
     * @throws RoomDoesNotExistException
     * @see Room
     */
    public void addKeyPair(String roomName, Certificate publicKey, Certificate privateKey) throws RoomDoesNotExistException {
        Room room = (Room) channels.get(roomNames.get(roomName));
        if (room != null) {
            room.addKeyPair(publicKey, privateKey);
            this.alert("Added key pair for \"" + room.getUserName() + "@" + roomName + "\"");
        } else {
            throw new RoomDoesNotExistException(roomName);
        }
    }

    /**
     * Removes a public/private key pair from the room identified by roomName
     * 
     * @param roomName String the room
     * @param publicKey Certificate public key
     * @throws RoomDoesNotExistException
     */
    public void removeKeyPair(String roomName, Certificate publicKey) throws RoomDoesNotExistException {
        Room room = (Room) channels.get(roomNames.get(roomName));
        if (room != null) {
            room.removeKeyPair(publicKey);
            this.alert("Removed key pair for \"" + room.getUserName() + "@" + roomName + "\"");
        } else {
            throw new RoomDoesNotExistException(roomName);
        }
    }

    /**
     * Send the given message to the room associated with roomName
     * 
     * @param roomName String name of room to send action to
     * @param message Message to send
     * @throws RoomDoesNotExistException
     */
    public void messageRoom(String roomName, Message message) throws RoomDoesNotExistException {
        Room room = (Room) channels.get(roomNames.get(roomName));
        if (room != null) {
            room.send(message);
            this.output(message.getFrom() + "@" + roomName + ": " + (message.hasKey() ? "*encrypted*" : message.getBody())); // To create a new command prompt
        } else {
            throw new RoomDoesNotExistException(roomName);
        }
    }

    /**
     * Sets the Controller's UI
     * 
     * @param ui UI to set
     */
    public void setUI(UI ui) {
        this.ui = ui;
    }

    /**
     * Return a list of the rooms in use. If a set of rooms is provided,
     * only respond about those rooms. Otherwise, return all rooms in use.
     * 
     * @param list
     * @return
     */
    public Collection<Integer> getRoomsInUse(List<Integer> list) {
        Collection<Integer> roomsInUse = roomNames.values();
        if (list != null) {
            roomsInUse.retainAll(list);
        }
        return roomsInUse;
    }

    /**
     * Return a list of the channels in use. If a set of channels is provided,
     * only respond about those channels. Otherwise, return all channels in use.
     * 
     * @param list
     * @return
     */
    public Collection<Integer> getChannelsInUse(List<Integer> list) {
        Collection<Integer> roomsInUse = channels.keySet();
        if (list != null) {
            roomsInUse.retainAll(list);
        }
        return roomsInUse;
    }

    /**
     * Shutdown the Controller.
     */
    public synchronized void shutdown() {
        for (Channel c : channels.values()) {
            if (c != this) {
                c.shutdown();
            }
        }
        super.mcc.close();
        super.runner.stop();
    }
}
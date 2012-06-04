package edu.drexel.cs544.mcmuc.channels;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.actions.Action;
import edu.drexel.cs544.mcmuc.actions.Message;
import edu.drexel.cs544.mcmuc.actions.PollPresence;
import edu.drexel.cs544.mcmuc.actions.Presence;
import edu.drexel.cs544.mcmuc.actions.Presence.Status;
import edu.drexel.cs544.mcmuc.util.MulticastReceiveRunnable;
import edu.drexel.cs544.mcmuc.util.Certificate;

/**
 * A Room is a Channel tied to a specific port that users can exchange message actions on.
 * The port is determined by a hash of the room name and the set of Channels already in use,
 * both of which must be used to construct the Room. The Room carries the user's presence and nickname.
 * The nickname is passed in at construction, the status is set to Online at construction but may
 * be changed later.
 */
public class Room extends Channel {

    private Status roomPresence;
    private String userName;
    private String name;
    private Map<Certificate, Certificate> keyPairs;

    /**
     * Add a public/private key pair to the room
     * @param publicKey Certificate public key
     * @param privateKey Certificate private key
     */
    public void addKeyPair(Certificate publicKey, Certificate privateKey)
    {
    	if(keyPairs.get(publicKey) != privateKey){
    		keyPairs.put(publicKey, privateKey);
    		sendStatus();
    	}
    }
    
    /**
     * Remove a public/private key pair from the room
     * @param publicKey Certificate public key
     */
    public void removeKeyPair(Certificate publicKey)
    {
    	if(keyPairs.remove(publicKey) != null)
    		sendStatus();
    }
    
    /**
     * Returns the public/private key pairs for the room
     * @return Map<Certificate, Certificate> public/private key pairs
     */
    public Map<Certificate, Certificate> getKeyPairs()
    {
    	return keyPairs;
    }
    
    /**
     * Sets the user's per-room status
     * 
     * @param newPresence one of the enumerated Status values
     */
    public void setStatus(Status newPresence) {
        roomPresence = newPresence;
        sendStatus();
    }
    
    /**
     * Sends the user's per-room status out to the Room channel
     */
    private void sendStatus()
    {
        if(this.keyPairs.keySet().isEmpty())
        	this.send(new Presence(this.getUserName(), roomPresence));
        else
        	this.send(new Presence(this.getUserName(), roomPresence, new ArrayList<Certificate>(keyPairs.keySet())));
    }

    /**
     * Accessor for the room's name
     * 
     * @return String the room's name
     */
    public String getName() {
        return name;
    }

    /**
     * Accessor for the user's per-room status
     * 
     * @return Status user's per-room status
     */
    public Status getStatus() {
        return roomPresence;
    }

    /**
     * Accessor for the user's per-room nickname
     * 
     * @return String the user's per-room nickname
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Chooses a port for the room using the hash algorithm and the set of ports already in use,
     * sets the use's per-room status to Online, passes the userName to the room, and starts the
     * multicast thread for the room.
     * 
     * @param name String descriptive room name
     * @param portsInUse Set<Integer> set of ports already in use (for hashing algorithm)
     * @param userName String user's per-room nickname
     */
    public Room(String name, Set<Integer> portsInUse, String userName) {
        super(choosePort(name, portsInUse));
        this.name = name;
        this.userName = userName;
        MulticastReceiveRunnable runner = new MulticastReceiveRunnable(this);
        Thread runnerThread = new Thread(runner);
        runnerThread.start();

        // Send the necessary initial messages
        setStatus(Status.Online);
        this.send(new PollPresence());
    }

    /**
     * The protocol uses a double hashing algorithm: it uses one hash value as a starting
     * point and repeatedly steps forward an interval determined by second hash function
     * until an unused port is found. A good hash function will map inputs evenly over the
     * output range, as the computational cost of hashing increases with the number of
     * collisions. Double hashing minimizes collisions more effectively than linear or
     * quadratic probing strategies. The i-th position of given value x, in a range of
     * size n is then F(x, i) = G(x) + i * H(x) mod n. Here, G and H should each produce
     * a 32-bit signed two's complement integer (twice the range of dynamic ports) using
     * the concatenated first four bytes of the MD5 cryptographic hash for G and SHA-1 for H.
     * 
     * @param name String descriptive name of room
     * @param portsInUse Set<Integer> ports of rooms already being used (to avoid collisions)
     * @return int the port to use for the given room
     */
    public static int choosePort(String name, Set<Integer> portsInUse) {
        int f = -1;

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");

            byte[] md5_digest = md5.digest(name.getBytes());
            byte[] sha1_digest = sha1.digest(name.getBytes());

            int g = ByteBuffer.wrap(md5_digest, 0, 4).getInt();
            int h = ByteBuffer.wrap(sha1_digest, 0, 4).getInt();

            int i = 1;
            do {
                f = ((g + i * h) % 16382) + 49152;
                i++;
            } while (portsInUse.contains(f));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return f;
    }

    /**
     * Supports the processing of Message, Presence, and PollPresence actions. Checks
     * the action key of the JSON received and uses that to pass processing to the
     * correct type. Errors are display if the message does not have an action or the
     * type is not supported (not message, presence, or poll-presence).
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

        if (actionString.equalsIgnoreCase(Message.action)) {
            action = new Message(jo);
            action.process(this);
        } else if (actionString.equalsIgnoreCase(Presence.action)) {
            try {
                action = new Presence(jo);
                action.process(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (actionString.equalsIgnoreCase(PollPresence.action)) {
            action = new PollPresence(jo);
            action.process(this);
        } else {
            System.err.println("Message action type not supported: " + actionString);
        }
    }
}

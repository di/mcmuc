package edu.drexel.cs544.mcmuc.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * DuplicateDetector is a ConcurrentLimitedLinkedQueue (CLLQ) of string-based unique identifiers,
 * limited in size to 200 members
 * 
 * For the multicast flooding algorithm, each node must have a way of tracking of each message
 * it has forwarded, to avoid forwarding them again. Simply requiring the clients to keep
 * the last n messages is insufficient, as there is no method to distinguish between
 * duplicates and messages that are new but the same as old messages (such as when a room
 * gets re-created or a message with the same body/from fields). Thus the protocol will
 * require unique message ID numbers; each client can generate a unique ID for each message
 * it creates, and every other client can use this ID to determine if the message is a
 * duplicate or not.
 * 
 * @see ConcurrentLimitedLinkedQueue
 */
public class DuplicateDetector {
    private static final DuplicateDetector instance = new DuplicateDetector();

    private ConcurrentLimitedLinkedQueue<String> q;
    private int size = 200;

    /**
     * Create a CLLQ of strings of size 200
     */
    private DuplicateDetector() {
        q = new ConcurrentLimitedLinkedQueue<String>(size);
    }

    /**
     * Add a seen message's UID. If the UID is already in the CLLQ then the message
     * is a duplicate that has already been forwarded, and must not be forwarded further
     * 
     * @param uid String UID
     * @return true if hash was added to the CLLQ, false otherwise
     */
    public boolean add(JSONObject jo) {
        String uid = null;
        try {
            uid = jo.getString("uid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return q.add(uid);
    }

    /**
     * Restricts the instantiation of the DuplicateDectector class to one object (the Singleton
     * design pattern)
     * 
     * @return DuplicateDetector singleton
     */
    public static DuplicateDetector getInstance() {
        return instance;
    }
}
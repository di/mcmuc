package edu.drexel.cs544.mcmuc;

/**
 * DuplicateDetector is a ConcurrentLimitedLinkedQueue (CLLQ) of string-based unique identifiers,
 * limited in size to 200 members
 * 
 * For the multicast flooding algorithm, each node must have a way of tracking of each  message
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
     * If the uid is already in the CLLQ then the message is a duplicate that has already been
     * forwarded, and must not be forwarded further
     * @param uid String unique identifier
     * @return true if previously unseen uid, false otherwise
     */
    public boolean isDuplicate(String uid) {
        if (q.contains(uid)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Add a seen unique identifier
     * @param uid String unique identifier
     * @return true if uid was added to the CLLQ, false otherwise
     */
    public boolean add(String uid) {
        return q.add(uid);
    }

    public static DuplicateDetector getInstance() {
        return instance;
    }
}
